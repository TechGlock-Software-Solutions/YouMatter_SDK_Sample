package com.techglock.health.app.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "AppCacheMaster")
data class AppCacheMaster(
    @PrimaryKey
    @SerializedName("mapKey")
    var mapKey: String = "",
    @SerializedName("mapValue")
    var mapValue: String? = ""
)