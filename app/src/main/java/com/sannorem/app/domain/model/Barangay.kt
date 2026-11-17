package com.sannorem.app.domain.model

data class Barangay(
    val id: String,
    val name: String,
    val group: BarangayGroup,
    val latitude: Double?,
    val longitude: Double?
)
