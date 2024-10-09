package com.techglock.health.app.model.entity

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

//@Entity(tableName = "DataSyncMaster" , primaryKeys = ["apiName","personId"] )
@Entity(tableName = "DataSyncMaster", primaryKeys = ["apiName"])
data class DataSyncMaster(
    @SerializedName("ApiName")
    var apiName: String = "",
    @SerializedName("SyncDate")
    var syncDate: String? = "",
    @SerializedName("PersonId")
    var personId: String = "007"
)