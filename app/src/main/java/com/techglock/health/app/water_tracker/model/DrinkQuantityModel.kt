package com.techglock.health.app.water_tracker.model

data class DrinkQuantityModel(
    var quantity: String = "",
    var unit: String = "ml",
    var isCustom: Boolean = false
)