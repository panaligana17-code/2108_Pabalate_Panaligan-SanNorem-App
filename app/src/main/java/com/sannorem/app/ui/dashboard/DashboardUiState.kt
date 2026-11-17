package com.sannorem.app.ui.dashboard

import com.sannorem.app.domain.model.Barangay
import com.sannorem.app.domain.model.RetailBranch

data class DashboardUiState(
    val barangays: List<Barangay> = emptyList(),
    val branches: List<RetailBranch> = emptyList(),
    val filteredBranches: List<RetailBranch> = emptyList(),
    val selectedBarangay: Barangay? = null,
    val selectedBrandFilter: com.sannorem.app.domain.model.Brand? = null, // null = All Brands
    val searchQuery: String = "",
    val daliCount: Int = 0,
    val sevenElevenCount: Int = 0,
    val alfamartCount: Int = 0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
