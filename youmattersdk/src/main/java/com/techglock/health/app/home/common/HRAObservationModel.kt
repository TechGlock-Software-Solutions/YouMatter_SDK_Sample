package com.techglock.health.app.home.common


import com.techglock.health.app.R

data class HRAObservationModel(
    var color: Int = R.color.dark_gray,
    var obaservation: String = "",
    var hraScore: Int = 0
)