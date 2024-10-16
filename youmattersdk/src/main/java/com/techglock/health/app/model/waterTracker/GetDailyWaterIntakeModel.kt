package com.techglock.health.app.model.waterTracker

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetDailyWaterIntakeModel(
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
        @SerializedName("RecordDate")
        @Expose
        val recordDate: String = ""
    )

    data class GetDailyWaterIntakeResponse(
        @SerializedName("Result")
        @Expose
        var result: Result = Result()
    )

    data class Result(
        @SerializedName("Result")
        val result: List<ResultData> = listOf()
    )

    data class ResultData(
        @SerializedName("WaterTrackingID")
        @Expose
        val waterTrackingID: Int? = 0,
        @SerializedName("TotalWaterIntake")
        @Expose
        val totalWaterIntake: String? = "",
        @SerializedName("WaterGoal")
        @Expose
        val waterGoal: String? = "",
        @SerializedName("WaterGoalPercentage")
        @Expose
        val waterGoalPercentage: String? = ""
    )

}
