package com.sannorem.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sannorem.app.data.BarangayRepository
import com.sannorem.app.data.BranchRepository
import com.sannorem.app.domain.geo.GeoUtils
import com.sannorem.app.domain.model.Barangay
import com.sannorem.app.domain.model.Brand
import com.sannorem.app.domain.model.RetailBranch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed interface LocationTestResult {
    data class Success(val latitude: Double, val longitude: Double) : LocationTestResult
    data class OutOfBounds(val latitude: Double, val longitude: Double, val distanceKm: Double) : LocationTestResult
    data class Failed(val fallbackLat: Double = GeoUtils.NASUGBU_CENTER_LAT, val fallbackLng: Double = GeoUtils.NASUGBU_CENTER_LNG) : LocationTestResult
}

class DashboardViewModel(
    private val barangayRepository: BarangayRepository,
    private val branchRepository: BranchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val barangays = barangayRepository.getBarangays()
        val branches = branchRepository.getBranches()

        val daliCount = branches.count { it.brand == Brand.DALI }
        val sevenElevenCount = branches.count { it.brand == Brand.SEVEN_ELEVEN }
        val alfamartCount = branches.count { it.brand == Brand.ALFAMART }

        _uiState.update {
            it.copy(
                barangays = barangays,
                branches = branches,
                filteredBranches = branches,
                daliCount = daliCount,
                sevenElevenCount = sevenElevenCount,
                alfamartCount = alfamartCount,
                isLoading = false,
                errorMessage = if (barangays.isEmpty() || branches.isEmpty()) {
                    "Failed to load datasets"
                } else null
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            val updated = state.copy(searchQuery = query)
            updated.copy(filteredBranches = computeFilteredBranches(updated))
        }
    }

    fun selectBrandFilter(brand: Brand?) {
        _uiState.update { state ->
            val updated = state.copy(selectedBrandFilter = brand)
            updated.copy(filteredBranches = computeFilteredBranches(updated))
        }
    }

    fun selectBarangay(barangay: Barangay?) {
        _uiState.update { state ->
            val updated = state.copy(selectedBarangay = barangay)
            updated.copy(filteredBranches = computeFilteredBranches(updated))
        }
    }

    private fun computeFilteredBranches(state: DashboardUiState): List<RetailBranch> {
        val query = state.searchQuery.trim().lowercase()
        return state.branches.filter { branch ->
            // Brand filter
            val matchesBrand = state.selectedBrandFilter == null || branch.brand == state.selectedBrandFilter
            // Barangay filter
            val matchesBarangay = state.selectedBarangay == null ||
                branch.barangayId.equals(state.selectedBarangay.id, ignoreCase = true) ||
                branch.address.contains(state.selectedBarangay.name, ignoreCase = true) ||
                branch.branchName.contains(state.selectedBarangay.name, ignoreCase = true)
            // Query filter
            val matchesQuery = query.isEmpty() ||
                branch.branchName.lowercase().contains(query) ||
                branch.id.lowercase().contains(query) ||
                branch.brand.name.lowercase().contains(query) ||
                branch.address.lowercase().contains(query) ||
                branch.strategy.lowercase().contains(query)

            matchesBrand && matchesBarangay && matchesQuery
        }
    }

    fun evaluateCustomerLocation(lat: Double?, lng: Double?): LocationTestResult {
        if (lat == null || lng == null) {
            return LocationTestResult.Failed()
        }

        val distanceKm = GeoUtils.haversineKm(
            lat,
            lng,
            GeoUtils.NASUGBU_CENTER_LAT,
            GeoUtils.NASUGBU_CENTER_LNG
        )

        return if (distanceKm > GeoUtils.NASUGBU_MAX_TEST_DISTANCE_KM) {
            LocationTestResult.OutOfBounds(
                latitude = GeoUtils.NASUGBU_CENTER_LAT,
                longitude = GeoUtils.NASUGBU_CENTER_LNG,
                distanceKm = distanceKm
            )
        } else {
            LocationTestResult.Success(latitude = lat, longitude = lng)
        }
    }

    companion object {
        fun provideFactory(
            barangayRepository: BarangayRepository,
            branchRepository: BranchRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(barangayRepository, branchRepository) as T
            }
        }
    }
}
