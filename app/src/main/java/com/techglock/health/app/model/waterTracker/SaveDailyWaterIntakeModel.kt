package com.techglock.health.app.model.waterTracker

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveDailyWaterIntakeModel(
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
        @SerializedName("BeverageCode")
        @Expose
        val beverageCode: String = "",
        @SerializedName("WaterValue")
        @Expose
        val waterValue: String = "",
        @SerializedName("RecordDate")
        @Expose
        val recordDate: String = "",
        @SerializedName("IsCurrentDateLog")
        @Expose
        val isCurrentDateLog: String = ""
    )

    data class SaveDailyWaterIntakeResponse(
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
        @SerializedName("BeverageCode")
        @Expose
        val beverageCode: String? = "",
        @SerializedName("WaterValue")
        @Expose
        val waterValue: String? = "",
        @SerializedName("RecordDate")
        @Expose
        val recordDate: String? = ""
    )

}
