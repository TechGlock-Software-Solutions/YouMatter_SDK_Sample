package com.techglock.health.app.home.common

import com.techglock.health.app.R

class CalculatorModel(
    name: String,
    description: String,
    imageId: Int,
    color: Int,
    code: String
) {
    var name = ""
    var description = ""
    var imageId = 0
    var color: Int = R.color.dark_gray
    var code = "NA"

    init {
        this.name = name
        this.description = description
        this.imageId = imageId
        this.color = color
        this.code = code
    }
}