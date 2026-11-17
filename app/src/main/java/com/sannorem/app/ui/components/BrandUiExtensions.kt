package com.sannorem.app.ui.components

import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.sannorem.app.domain.model.Brand
import com.sannorem.app.ui.theme.AlfamartBlue
import com.sannorem.app.ui.theme.DaliRed
import com.sannorem.app.ui.theme.SevenElevenGreen

val Brand.displayName: String
    get() = when (this) {
        Brand.DALI -> "DALI Everyday Grocery"
        Brand.SEVEN_ELEVEN -> "7-Eleven"
        Brand.ALFAMART -> "Alfamart"
    }

val Brand.shortName: String
    get() = when (this) {
        Brand.DALI -> "DALI"
        Brand.SEVEN_ELEVEN -> "7-Eleven"
        Brand.ALFAMART -> "Alfamart"
    }

val Brand.brandColor: Color
    get() = when (this) {
        Brand.DALI -> DaliRed
        Brand.SEVEN_ELEVEN -> SevenElevenGreen
        Brand.ALFAMART -> AlfamartBlue
    }

val Brand.markerHue: Float
    get() = when (this) {
        Brand.DALI -> BitmapDescriptorFactory.HUE_RED
        Brand.SEVEN_ELEVEN -> BitmapDescriptorFactory.HUE_GREEN
        Brand.ALFAMART -> BitmapDescriptorFactory.HUE_AZURE
    }

val Brand.defaultRadiusKm: Double
    get() = Brand.defaultRadiusKm(this)
