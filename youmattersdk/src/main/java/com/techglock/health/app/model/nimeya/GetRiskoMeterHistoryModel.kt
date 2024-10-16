package com.techglock.health.app.model.nimeya

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetRiskoMeterHistoryModel(
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

    data class RiskoMeterHistoryResponse(
        @SerializedName("GetRiskoMeterHistory")
        @Expose
        val getRiskoMeterHistory: GetRiskoMeterHistory = GetRiskoMeterHistory()
    )

    data class GetRiskoMeterHistory(
        @Expose
        @SerializedName("PersonID")
        var personID: Int = 0,
        @Expose
        @SerializedName("AccountID")
        var accountID: Int = 0,
        @Expose
        @SerializedName("Score")
        val score: Int? = 0,
        @Expose
        @SerializedName("RiskMeter")
        val riskMeter: String? = "",
        @Expose
        @SerializedName("RiskText")
        val riskText: String? = "",
        @Expose
        @SerializedName("UpdatedAt")
        val updatedAt: String? = "",
        @Expose
        @SerializedName("DateTime")
        val dateTime: String? = "",
        @Expose
        @SerializedName("CreatedAt")
        val createdAt: String? = "",
        @Expose
        @SerializedName("ModifiedAt")
        val modifiedAt: String? = ""
    )

}