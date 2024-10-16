package com.techglock.health.app.model.nimeya

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveRiskoMeterModel(
    @Expose
    @SerializedName("JSONData")
    private val jsonData: String,
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @Expose
        @SerializedName("PersonID")
        var personID: Int = 0,
        @Expose
        @SerializedName("AccountID")
        var accountID: Int = 0,
        @Expose
        @SerializedName("DateTime")
        val dateTime: String? = "",
        @Expose
        @SerializedName("Request")
        val request: List<Request> = listOf()
    )

    data class Request(
        @SerializedName("question_Id")
        @Expose
        val questionId: String? = "",
        @SerializedName("answer_Id")
        @Expose
        val answerId: String? = ""
    )

    data class RiskoMeterSaveResponse(
        @SerializedName("SaveRiskoMeter")
        @Expose
        val saveRiskoMeter: SaveRiskoMeter = SaveRiskoMeter()
    )

    data class SaveRiskoMeter(
        @SerializedName("status")
        @Expose
        val status: String? = "",
        @SerializedName("data")
        @Expose
        val data: Data = Data()
    )

    data class Data(
        @Expose
        @SerializedName("score")
        val score: Int? = 0,
        @Expose
        @SerializedName("riskMeter")
        val riskMeter: String? = "",
        @Expose
        @SerializedName("riskText")
        val riskText: String? = "",
        @Expose
        @SerializedName("updatedAt")
        val updatedAt: String? = ""
    )

}