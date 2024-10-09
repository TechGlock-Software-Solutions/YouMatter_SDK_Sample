package com.techglock.health.app.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "HRASummaryTable")
data class HRASummary(
    @PrimaryKey
    @SerializedName("Age")
    var age: Int = 0,
    @SerializedName("BMI")
    var bmi: Double = 0.0,
    @SerializedName("BMIObservation")
    var bmiObservation: String = "",
    @SerializedName("BPObservation")
    var bpObservation: String = "",
    @SerializedName("BloodGroup")
    var bloodGroup: String = "",
    @SerializedName("CurrentHRAHistoryID")
    var currentHRAHistoryID: Int = 0,
    @SerializedName("DateOfBirth")
    var dateOfBirth: String = "",
    @SerializedName("Diastolic")
    var diastolic: Double = 0.0,
    @SerializedName("FirstName")
    var firstName: String = "",
    @SerializedName("Gender")
    var gender: String = "",
    @SerializedName("HRACutOff")
    var hraCutOff: String = "",
    @SerializedName("Height")
    var height: Double = 0.0,
    @SerializedName("Hip")
    var hip: Double = 0.0,
    @SerializedName("LastName")
    var lastName: String = "",
    @SerializedName("PersonID")
    var personID: Int = 0,
    @SerializedName("ScorePercentile")
    var scorePercentile: Double = 0.0,
    @SerializedName("Systolic")
    var systolic: Double = 0.0,
    @SerializedName("WHR")
    var wHR: Double = 0.0,
    @SerializedName("WHRObservation")
    var whrObservation: String = "",
    @SerializedName("Waist")
    var waist: Double = 0.0,
    @SerializedName("Weight")
    var weight: Double = 0.0,
    @field:SerializedName("LastUpdatedTime")
    var LastUpdatedTime: String = currentUTCDatetimeInMillisecAsString
)

/*data class HRASummary(
    @PrimaryKey
    @field:SerializedName("PersonID")
    val PersonID : String,
    @field:SerializedName("Score")
    val Score : String,
    @field:SerializedName("CurrentHRAHistoryID")
    val CurrentHRAHistoryID : String ,
    @field:SerializedName("HRACutOff")
    val HRACutOff : String ,
    @field:SerializedName("Observation")
    val Observation : String ,
    @field:SerializedName("status")
    val status : String ,
    @field:SerializedName("Sync")
    val Sync : String ,
    @field:SerializedName("LastUpdatedTime")
    val LastUpdatedTime : String = currentUTCDatetimeInMillisecAsString) */ {
    companion object {
        val DATE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss"
        val currentUTCDatetimeInMillisecAsString: String
            get() {
                val sdf = SimpleDateFormat(DATE_FORMAT_UTC, Locale.ENGLISH)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                return sdf.format(Date())
            }
    }
}


