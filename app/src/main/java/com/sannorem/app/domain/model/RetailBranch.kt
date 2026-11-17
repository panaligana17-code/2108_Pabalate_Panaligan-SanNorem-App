package com.sannorem.app.domain.model

data class RetailBranch(
    val id: String,
    val brand: Brand,
    val branchName: String,
    val latitude: Double,
    val longitude: Double,
    val serviceRadiusKm: Double,
    val address: String = "",
    val hours: String = "24/7",
    val barangayId: String = "",
    val barangayName: String = "",
    val verified: Boolean = true,
    val attributes: List<String> = emptyList(),
    val strategy: String = ""
)
