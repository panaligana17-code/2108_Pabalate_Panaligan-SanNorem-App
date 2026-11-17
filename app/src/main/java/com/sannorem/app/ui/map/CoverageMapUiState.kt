package com.sannorem.app.ui.map

import com.google.android.gms.maps.model.LatLng
import com.sannorem.app.domain.model.Barangay
import com.sannorem.app.domain.model.Brand
import com.sannorem.app.domain.model.CoverageResult
import com.sannorem.app.domain.model.RetailBranch

data class CoverageMapUiState(
    val initialCenter: LatLng = LatLng(14.0734, 120.6315),
    val locationTitle: String = "Nasugbu, Batangas",
    val isApproximateCenter: Boolean = false,
    val barangays: List<Barangay> = emptyList(),
    val selectedBarangay: Barangay? = null,
    val branches: List<RetailBranch> = emptyList(),
    val filteredBranches: List<RetailBranch> = emptyList(),
    val branchDistancesKm: Map<String, Double> = emptyMap(),
    val activeBrandFilters: Set<Brand> = Brand.entries.toSet(),
    val activeRadiusBranch: RetailBranch? = null,
    val customerLocation: LatLng? = null,
    val customerCoverageResults: List<CoverageResult> = emptyList(),
    val isBottomSheetVisible: Boolean = false,
    val isMapsApiKeyMissing: Boolean = false,
    // Feature 6: selected branch for OSM marker-tap bottom sheet (name + address)
    val selectedBranch: RetailBranch? = null,
    val isBranchSheetVisible: Boolean = false,
    val proximityRadiusKm: Double = 1.5
)
