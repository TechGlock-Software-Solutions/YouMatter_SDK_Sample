package com.techglock.health.app.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "AppUpdateDetailsTable")
data class AppVersion(
    @PrimaryKey
    @SerializedName("ID")
    var id: Int = 0,
    @SerializedName("CurrentVersion")
    var currentVersion: String? = "",
    @SerializedName("ForceUpdate")
    var forceUpdate: Boolean = false,
    @SerializedName("Description")
    var description: String? = "",
    @SerializedName("ImagePath")
    var imagePath: String? = "",
    @SerializedName("APICallInterval")
    var apiCallInterval: Int = 0,
    @SerializedName("LastUpdateDate")
    var lastUpdateDate: String? = currentDateInYYYYMMDD,
    @SerializedName("Application")
    var application: String? = "",
    @SerializedName("DeviceType")
    var deviceType: String? = "",
    @SerializedName("Features")
    var features: String? = "",
    @SerializedName("ReleasedDate")
    var releasedDate: String? = ""
) {

    companion object {
        val currentDateInYYYYMMDD: String
            get() {
                val calendar = Calendar.getInstance()
                val df = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                return df.format(calendar.time)
            }
    }
}
