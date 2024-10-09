package com.techglock.health.app.model.waterTracker

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveWaterIntakeGoalModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("Request")
        @Expose
        var Request: Request = Request()
    )

    data class Request(
        @SerializedName("PersonID")
        @Expose
        val personID: Int = 0,
        @SerializedName("Weight")
        @Expose
        val weight: String = "",
        @SerializedName("Exercise")
        @Expose
        val exercise: String = "",
        @SerializedName("ExerciseDuration")
        @Expose
        val exerciseDuration: String = "",
        @SerializedName("IsNotification")
        @Expose
        val isNotification: String = "",
        @SerializedName("RecordDate")
        @Expose
        val recordDate: String = "",
        @SerializedName("WaterValue")
        @Expose
        val waterGoal: String = "",
        @SerializedName("Type")
        @Expose
        val type: String = ""
    )

    data class SaveWaterIntakeGoalResponse(
        @SerializedName("Result")
        @Expose
        var result: Result = Result()
    )

    data class Result(
        @SerializedName("PersonID")
        @Expose
        val personID: Int? = 0,
        @SerializedName("WaterID")
        @Expose
        val waterID: String? = "",
        @SerializedName("Weight")
        @Expose
        val weight: String? = "",
        @SerializedName("Exercise")
        @Expose
        val exercise: String? = "",
        @SerializedName("ExerciseDuration")
        @Expose
        val exerciseDuration: String? = "",
        @SerializedName("IsNotification")
        @Expose
        val isNotification: String? = "",
        @SerializedName("RecordDate")
        @Expose
        val recordDate: String? = "",
        @SerializedName("WaterValue")
        @Expose
        val waterGoal: String? = "",
        @SerializedName("Type")
        @Expose
        val type: String? = ""
    )

}
