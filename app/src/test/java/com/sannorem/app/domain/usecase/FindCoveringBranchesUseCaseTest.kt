package com.sannorem.app.domain.usecase

import com.sannorem.app.data.BranchRepository
import com.sannorem.app.domain.geo.GeoUtils
import com.sannorem.app.domain.model.Brand
import com.sannorem.app.domain.model.RetailBranch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FindCoveringBranchesUseCaseTest {

    private class FakeBranchRepository(
        private val branches: List<RetailBranch>
    ) : BranchRepository {
        override fun getBranches(): List<RetailBranch> = branches
    }

    @Test
    fun invoke_pointInsideServiceRadius_isIncluded() {
        val branch = RetailBranch(
            id = "DALI-TEST",
            brand = Brand.DALI,
            branchName = "DALI Poblacion",
            latitude = 14.077563,
            longitude = 120.630062,
            serviceRadiusKm = 1.0
        )
        val repository = FakeBranchRepository(listOf(branch))
        val useCase = FindCoveringBranchesUseCase(repository)

        // Customer is ~300 meters away
        val customerLat = 14.075000
        val customerLng = 120.630062

        val results = useCase(customerLat, customerLng)

        assertEquals(1, results.size)
        assertEquals("DALI-TEST", results[0].branch.id)
        assertTrue(results[0].distanceKm < 1.0)
    }

    @Test
    fun invoke_pointOutsideServiceRadius_isExcluded() {
        val branch = RetailBranch(
            id = "ALFA-TEST",
            brand = Brand.ALFAMART,
            branchName = "Alfamart Bucana",
            latitude = 14.067687,
            longitude = 120.628438,
            serviceRadiusKm = 0.5
        )
        val repository = FakeBranchRepository(listOf(branch))
        val useCase = FindCoveringBranchesUseCase(repository)

        // Customer is ~1.5 km away (outside 0.5 km radius)
        val customerLat = 14.081000
        val customerLng = 120.628438

        val results = useCase(customerLat, customerLng)

        assertTrue(results.isEmpty())
    }

    @Test
    fun invoke_pointExactlyOnBoundary_isIncluded() {
        val centerLat = 14.0734
        val centerLng = 120.6315
        val targetRadiusKm = 0.6 // 7-Eleven radius

        // Compute coordinate exactly 0.6 km north
        // 1 deg latitude is approximately (pi / 180) * 6371.0088 = 111.1949 km
        val deltaLatDeg = targetRadiusKm / (GeoUtils.EARTH_RADIUS_KM * Math.PI / 180.0)
        val boundaryLat = centerLat + deltaLatDeg
        val boundaryLng = centerLng

        val actualDistance = GeoUtils.haversineKm(centerLat, centerLng, boundaryLat, boundaryLng)
        assertEquals(targetRadiusKm, actualDistance, 0.0001)

        val branch = RetailBranch(
            id = "7E-BOUNDARY",
            brand = Brand.SEVEN_ELEVEN,
            branchName = "7-Eleven Boundary Test",
            latitude = centerLat,
            longitude = centerLng,
            serviceRadiusKm = actualDistance // Service radius equals exact distance
        )
        val repository = FakeBranchRepository(listOf(branch))
        val useCase = FindCoveringBranchesUseCase(repository)

        val results = useCase(boundaryLat, boundaryLng)

        assertEquals("Boundary point where distance <= radius must be included", 1, results.size)
        assertEquals("7E-BOUNDARY", results[0].branch.id)
        assertEquals(actualDistance, results[0].distanceKm, 0.00001)
    }

    @Test
    fun invoke_multipleBranches_returnsNearestFirstOrdering() {
        val centerLat = 14.0734
        val centerLng = 120.6315

        // Create 3 branches at varying distances from customer
        val branchFar = RetailBranch(
            id = "BRANCH-FAR",
            brand = Brand.DALI,
            branchName = "Far Branch",
            latitude = 14.0734 + (0.8 / 111.1949), // ~0.8 km
            longitude = centerLng,
            serviceRadiusKm = 1.0
        )
        val branchNear = RetailBranch(
            id = "BRANCH-NEAR",
            brand = Brand.ALFAMART,
            branchName = "Near Branch",
            latitude = 14.0734 + (0.2 / 111.1949), // ~0.2 km
            longitude = centerLng,
            serviceRadiusKm = 0.5
        )
        val branchMid = RetailBranch(
            id = "BRANCH-MID",
            brand = Brand.SEVEN_ELEVEN,
            branchName = "Mid Branch",
            latitude = 14.0734 + (0.4 / 111.1949), // ~0.4 km
            longitude = centerLng,
            serviceRadiusKm = 0.6
        )

        // Pass branches in unsorted order
        val repository = FakeBranchRepository(listOf(branchFar, branchNear, branchMid))
        val useCase = FindCoveringBranchesUseCase(repository)

        val results = useCase(centerLat, centerLng)

        assertEquals(3, results.size)
        assertEquals("BRANCH-NEAR", results[0].branch.id)
        assertEquals("BRANCH-MID", results[1].branch.id)
        assertEquals("BRANCH-FAR", results[2].branch.id)

        // Verify strictly ascending order
        assertTrue(results[0].distanceKm < results[1].distanceKm)
        assertTrue(results[1].distanceKm < results[2].distanceKm)
    }
}
