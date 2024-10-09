package com.techglock.health.app.medication_tracker.model

import com.techglock.health.app.R

data class MedTypeModel(
    var medTypeTitle: String = "",
    var medTypeCode: String = "",
    var medTypeImageId: Int = R.drawable.img_capsul
)