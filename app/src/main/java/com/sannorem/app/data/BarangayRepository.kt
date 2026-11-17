package com.sannorem.app.data

import com.sannorem.app.domain.model.Barangay

interface BarangayRepository {
    fun getBarangays(): List<Barangay>
    fun findByNameOrAlias(query: String): Barangay?
}
