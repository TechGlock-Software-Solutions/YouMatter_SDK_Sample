package com.techglock.health.app.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "HRASaveLabTable")
data class HRALabDetails(
    @PrimaryKey
    @field:SerializedName("ParameterCode")
    var ParameterCode: String = "",
    @field:SerializedName("RecordDate")
    var RecordDate: String? = "",
    @field:SerializedName("Value")
    var LabValue: String? = "",
    @field:SerializedName("PersonID")
    var PersonID: String? = "",
    @field:SerializedName("Unit")
    var Unit: String? = ""
)