package com.techglock.health.app.model.waterTracker

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetWaterIntakeHistoryByDateModel(
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
        @SerializedName("FromDate")
        @Expose
        val fromDate: String = "",
        @SerializedName("ToDate")
        @Expose
        val toDate: String = "",
        @SerializedName("Type")
        @Expose
        val type: String = ""
    )

    data class GetWaterIntakeHistoryByDateResponse(
        @SerializedName("Result")
        var result: Result = Result()
    )

    data class Result(
        @SerializedName("Result")
        val result: List<ResultData> = listOf()
    )

    data class ResultData(
        @SerializedName("ID")
        @Expose
        var id: Int? = 0,
        @SerializedName("RecordDate")
        @Expose
        var recordDate: String? = "",
        @SerializedName("TotalWaterIntake")
        @Expose
        var totalWaterIntake: String? = "",
        @SerializedName("WaterGoal")
        @Expose
        var waterGoal: String? = "2500",
        @SerializedName("WaterGoalPercentage")
        @Expose
        var waterGoalPercentage: String? = ""
    )

}
