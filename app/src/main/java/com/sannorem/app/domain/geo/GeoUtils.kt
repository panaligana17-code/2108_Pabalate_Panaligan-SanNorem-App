package com.sannorem.app.domain.geo

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object GeoUtils {
    const val EARTH_RADIUS_KM: Double = 6371.0088
    const val NASUGBU_CENTER_LAT: Double = 14.0734
    const val NASUGBU_CENTER_LNG: Double = 120.6315
    const val NASUGBU_MAX_TEST_DISTANCE_KM: Double = 20.0

    /**
     * Calculates the great-circle distance between two coordinates on Earth in kilometers
     * using the Haversine formula.
     * Earth radius = 6371.0088 km.
     */
    fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val rLat1 = Math.toRadians(lat1)
        val rLat2 = Math.toRadians(lat2)

        val a = sin(dLat / 2.0) * sin(dLat / 2.0) +
                cos(rLat1) * cos(rLat2) *
                sin(dLon / 2.0) * sin(dLon / 2.0)

        val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))
        return EARTH_RADIUS_KM * c
    }
}
