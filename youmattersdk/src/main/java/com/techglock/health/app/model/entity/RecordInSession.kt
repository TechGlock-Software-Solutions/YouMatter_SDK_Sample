package com.techglock.health.app.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "RecordsInSessionTable")
data class RecordInSession(
    @SerializedName("Id")
    var Id: String = "",
    @PrimaryKey
    @SerializedName("Name")
    var Name: String = "",
    @SerializedName("OriginalFileName")
    var OriginalFileName: String = "",
    @SerializedName("Path")
    var Path: String = "",
    @SerializedName("Type")
    var Type: String = "",
    @SerializedName("FileUri")
    var FileUri: String = "",
    @SerializedName("Sync")
    var Sync: String = "",
    @SerializedName("Code")
    var Code: String = ""
)