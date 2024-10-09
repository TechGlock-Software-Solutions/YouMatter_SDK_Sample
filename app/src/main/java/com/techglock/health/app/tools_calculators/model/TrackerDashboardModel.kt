package com.techglock.health.app.tools_calculators.model

import com.techglock.health.app.R

/*class TrackerDashboardModel(
    name: String,
    description: String,
    imageId: Int,
    color: Int,
    code: String) {
    var name = ""
    var description = ""
    var imageId = 0
    var color: Int = R.color.textViewColor
    var code = "NA"

    init {
        this.name = name
        this.description = description
        this.imageId = imageId
        this.color = color
        this.code = code
    }
}*/

data class TrackerDashboardModel(
    var name: String = "",
    var description: String = "",
    var imageId: Int = 0,
    var color: Int = R.color.textViewColor,
    var code: String = "NA"
)