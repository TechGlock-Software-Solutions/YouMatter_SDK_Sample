package com.techglock.health.app.model.nimeya

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetRiskoMeterModel(
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
        var accountID: Int = 0
    )

    data class RiskoMeterQuesResponse(
        @SerializedName("GetRiskoMeter")
        @Expose
        val getRiskoMeter: GetRiskoMeter = GetRiskoMeter()
    )

    data class GetRiskoMeter(
        @SerializedName("data")
        @Expose
        val data: List<Data> = listOf()
    )

    data class Data(
        @Expose
        @SerializedName("id")
        val id: String? = "",
        @Expose
        @SerializedName("question")
        val question: String? = "",
        @Expose
        @SerializedName("answers")
        val answers: List<Answer> = listOf()
    )

    data class Answer(
        @Expose
        @SerializedName("id")
        val id: String? = "",
        @Expose
        @SerializedName("answer")
        val answer: String? = "",
        @Expose
        @SerializedName("isSelected")
        var isSelected: Boolean = false
    )

}