package com.sannorem.app.domain.geo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeoUtilsTest {

    @Test
    fun haversineKm_knownDistance_barangay4ToBarangay11IsAbout1Point5Km() {
        // Barangay 4 (Poblacion): 14.0735, 120.6343
        val brgy4Lat = 14.0735
        val brgy4Lng = 120.6343

        // Barangay 11 (Poblacion): 14.0604, 120.6344
        val brgy11Lat = 14.0604
        val brgy11Lng = 120.6344

        val distanceKm = GeoUtils.haversineKm(brgy4Lat, brgy4Lng, brgy11Lat, brgy11Lng)

        // Expected distance is approximately 1.46 km (~1.5 km)
        assertTrue("Expected ~1.5 km but was $distanceKm km", distanceKm in 1.40..1.55)
        assertEquals(1.46, distanceKm, 0.05)
    }

    @Test
    fun haversineKm_sameCoordinates_returnsZero() {
        val distance = GeoUtils.haversineKm(14.0734, 120.6315, 14.0734, 120.6315)
        assertEquals(0.0, distance, 0.00001)
    }

    @Test
    fun haversineKm_symmetry_returnsIdenticalDistance() {
        val lat1 = 14.077563
        val lon1 = 120.630062
        val lat2 = 14.162812
        val lon2 = 120.628563

        val d1 = GeoUtils.haversineKm(lat1, lon1, lat2, lon2)
        val d2 = GeoUtils.haversineKm(lat2, lon2, lat1, lon1)

        assertEquals(d1, d2, 0.0000001)
    }

    @Test
    fun haversineKm_knownLongDistance_nasugbuToKaylaway() {
        // Municipal center (14.0734, 120.6315) to Kaylaway (14.0747, 120.8172)
        val distance = GeoUtils.haversineKm(14.0734, 120.6315, 14.0747, 120.8172)
        // Distance is ~20 km east
        assertTrue("Distance should be around 20 km", distance in 19.5..20.5)
    }
}
