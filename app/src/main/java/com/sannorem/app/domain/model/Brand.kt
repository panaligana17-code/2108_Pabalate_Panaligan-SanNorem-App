package com.sannorem.app.domain.model

enum class Brand {
    DALI,
    SEVEN_ELEVEN,
    ALFAMART;

    companion object {
        const val DEFAULT_RADIUS_DALI_KM: Double = 1.0
        const val DEFAULT_RADIUS_SEVEN_ELEVEN_KM: Double = 0.6
        const val DEFAULT_RADIUS_ALFAMART_KM: Double = 0.5

        fun defaultRadiusKm(brand: Brand): Double = when (brand) {
            DALI -> DEFAULT_RADIUS_DALI_KM
            SEVEN_ELEVEN -> DEFAULT_RADIUS_SEVEN_ELEVEN_KM
            ALFAMART -> DEFAULT_RADIUS_ALFAMART_KM
        }
    }
}
