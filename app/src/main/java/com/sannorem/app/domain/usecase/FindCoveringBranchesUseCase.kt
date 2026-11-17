package com.sannorem.app.domain.usecase

import com.sannorem.app.data.BranchRepository
import com.sannorem.app.domain.geo.GeoUtils
import com.sannorem.app.domain.model.CoverageResult

class FindCoveringBranchesUseCase(private val repository: BranchRepository) {
    operator fun invoke(lat: Double, lon: Double): List<CoverageResult> {
        return repository.getBranches()
            .map { branch ->
                val distance = GeoUtils.haversineKm(lat, lon, branch.latitude, branch.longitude)
                CoverageResult(branch = branch, distanceKm = distance)
            }
            .filter { it.distanceKm <= it.branch.serviceRadiusKm }
            .sortedBy { it.distanceKm }
    }
}
