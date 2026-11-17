package com.sannorem.app.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.sannorem.app.data.BarangayRepository
import com.sannorem.app.data.BranchRepository
import com.sannorem.app.domain.geo.GeoUtils
import com.sannorem.app.domain.model.Barangay
import com.sannorem.app.domain.model.Brand
import com.sannorem.app.domain.model.RetailBranch
import com.sannorem.app.domain.usecase.FindCoveringBranchesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.abs

class CoverageMapViewModel(
    private val barangayRepository: BarangayRepository,
    private val branchRepository: BranchRepository,
    private val findCoveringBranchesUseCase: FindCoveringBranchesUseCase,
    initialLat: Double,
    initialLng: Double,
    title: String,
    isApproximate: Boolean,
    isCustomerLocation: Boolean,
    isMapsApiKeyMissing: Boolean
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CoverageMapUiState(
            initialCenter = LatLng(initialLat, initialLng),
            locationTitle = title,
            isApproximateCenter = isApproximate,
            isMapsApiKeyMissing = isMapsApiKeyMissing
        )
    )
    val uiState: StateFlow<CoverageMapUiState> = _uiState.asStateFlow()

    init {
        loadData(initialLat, initialLng, title)
        if (isCustomerLocation) {
            setCustomerLocation(LatLng(initialLat, initialLng))
        }
    }

    private fun loadData(initialLat: Double, initialLng: Double, initialTitle: String) {
        val allBarangays = barangayRepository.getBarangays()
        val allBranches = branchRepository.getBranches()

        val matchingBarangay = allBarangays.firstOrNull {
            it.name.equals(initialTitle, ignoreCase = true) ||
                    (it.latitude != null && abs(it.latitude - initialLat) < 0.001 &&
                            it.longitude != null && abs(it.longitude - initialLng) < 0.001)
        } ?: allBarangays.firstOrNull()

        _uiState.update { current ->
            current.copy(
                barangays = allBarangays,
                branches = allBranches,
                selectedBarangay = matchingBarangay
            )
        }

        if (matchingBarangay != null) {
            applyProximityFilter(matchingBarangay)
        } else {
            applyProximityFilterToLocation(initialLat, initialLng, initialTitle)
        }
    }

    /**
     * Feature 1: Smoothly center & zoom to selected barangay
     * Feature 2: Proximity filtering - calculate real-time distance, only show branches <= 1.5 km
     */
    fun selectBarangay(barangay: Barangay) {
        applyProximityFilter(barangay)
    }

    private fun applyProximityFilter(barangay: Barangay) {
        val lat = barangay.latitude ?: GeoUtils.NASUGBU_CENTER_LAT
        val lng = barangay.longitude ?: GeoUtils.NASUGBU_CENTER_LNG
        applyProximityFilterToLocation(lat, lng, barangay.name, barangay)
    }

    private fun applyProximityFilterToLocation(
        centerLat: Double,
        centerLng: Double,
        locationTitle: String,
        barangay: Barangay? = null
    ) {
        _uiState.update { current ->
            val nearbyBranchesWithDist = current.branches.mapNotNull { branch ->
                val distKm = GeoUtils.haversineKm(centerLat, centerLng, branch.latitude, branch.longitude)
                if (distKm <= current.proximityRadiusKm && branch.brand in current.activeBrandFilters) {
                    branch to distKm
                } else {
                    null
                }
            }.sortedBy { it.second }

            val nearbyBranches = nearbyBranchesWithDist.map { it.first }
            val distancesMap = nearbyBranchesWithDist.associate { it.first.id to it.second }

            current.copy(
                initialCenter = LatLng(centerLat, centerLng),
                locationTitle = locationTitle,
                selectedBarangay = barangay,
                filteredBranches = nearbyBranches,
                branchDistancesKm = distancesMap,
                activeRadiusBranch = null,
                isBranchSheetVisible = false,
                selectedBranch = null
            )
        }
    }

    fun onMarkerClicked(branch: RetailBranch) {
        _uiState.update { current ->
            current.copy(
                activeRadiusBranch = branch,
                // Feature 6: show branch details bottom sheet (name + address)
                selectedBranch = branch,
                isBranchSheetVisible = true
            )
        }
    }

    fun dismissBranchSheet() {
        _uiState.update { it.copy(isBranchSheetVisible = false, selectedBranch = null) }
    }

    fun onMapClick(latLng: LatLng) {
        setCustomerLocation(latLng)
    }

    private fun setCustomerLocation(latLng: LatLng) {
        val results = findCoveringBranchesUseCase(latLng.latitude, latLng.longitude)
        _uiState.update { current ->
            current.copy(
                activeRadiusBranch = null,
                customerLocation = latLng,
                customerCoverageResults = results,
                isBottomSheetVisible = true,
                isBranchSheetVisible = false
            )
        }
    }

    fun clearActiveRadius() {
        _uiState.update { it.copy(activeRadiusBranch = null) }
    }

    fun dismissBottomSheet() {
        _uiState.update { it.copy(isBottomSheetVisible = false) }
    }

    fun toggleBrandFilter(brand: Brand) {
        _uiState.update { current ->
            val updatedFilters = if (current.activeBrandFilters.contains(brand)) {
                if (current.activeBrandFilters.size > 1) {
                    current.activeBrandFilters - brand
                } else {
                    current.activeBrandFilters
                }
            } else {
                current.activeBrandFilters + brand
            }

            val centerLat = current.initialCenter.latitude
            val centerLng = current.initialCenter.longitude

            val nearbyBranches = current.branches.filter { branch ->
                val distKm = GeoUtils.haversineKm(centerLat, centerLng, branch.latitude, branch.longitude)
                distKm <= current.proximityRadiusKm && branch.brand in updatedFilters
            }

            current.copy(
                activeBrandFilters = updatedFilters,
                filteredBranches = nearbyBranches,
                activeRadiusBranch = if (current.activeRadiusBranch?.brand !in updatedFilters) null else current.activeRadiusBranch
            )
        }
    }

    companion object {
        fun provideFactory(
            barangayRepository: BarangayRepository,
            branchRepository: BranchRepository,
            findCoveringBranchesUseCase: FindCoveringBranchesUseCase,
            initialLat: Double,
            initialLng: Double,
            title: String,
            isApproximate: Boolean,
            isCustomerLocation: Boolean,
            isMapsApiKeyMissing: Boolean
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CoverageMapViewModel(
                    barangayRepository,
                    branchRepository,
                    findCoveringBranchesUseCase,
                    initialLat,
                    initialLng,
                    title,
                    isApproximate,
                    isCustomerLocation,
                    isMapsApiKeyMissing
                ) as T
            }
        }
    }
}
