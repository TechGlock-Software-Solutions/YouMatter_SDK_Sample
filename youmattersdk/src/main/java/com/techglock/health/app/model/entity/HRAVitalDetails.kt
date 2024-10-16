package com.techglock.health.app.model.entity

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "HRASaveVitalTable", primaryKeys = ["VitalsKey"])
data class HRAVitalDetails(
    @field:SerializedName("VitalsKey")
    var VitalsKey: String = "",
    @field:SerializedName("VitalsValue")
    var VitalsValue: String = ""
)