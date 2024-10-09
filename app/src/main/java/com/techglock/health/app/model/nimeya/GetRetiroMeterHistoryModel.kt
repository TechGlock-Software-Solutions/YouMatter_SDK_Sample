package com.techglock.health.app.model.nimeya

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetRetiroMeterHistoryModel(
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

    data class RetiroMeterHistoryResponse(
        @Expose
        @SerializedName("GetRetiroMeterHistory")
        val data: GetRetiroMeterHistory = GetRetiroMeterHistory()
    )

    data class GetRetiroMeterHistory(
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
        @SerializedName("Score")
        val score: Int? = 0,
        @Expose
        @SerializedName("ScoreText")
        val scoreText: String? = "",
        @Expose
        @SerializedName("Message")
        val message: String? = "",

        @Expose
        @SerializedName("YourDesiredRetirementIncome")
        val yourDesireRetirementIncome: Int? = 0,
        @Expose
        @SerializedName("ProjectedMonthlyRetirementIncomePerMonth")
        val projectedMonthlyRetirementIncomePerMonth: Int? = 0,
        @Expose
        @SerializedName("RequiredMonthlyRetirementSavings")
        val requiredMonthlyRetirementSavings: Int? = 0,
        @Expose
        @SerializedName("RequiredPPF")
        val requiredPpf: Int? = 0,
        @Expose
        @SerializedName("RequiredEPF")
        val requiredEpf: Int? = 0,
        @Expose
        @SerializedName("RequiredNPS")
        val requiredNps: Int? = 0,
        @Expose
        @SerializedName("RequiredEquityMutualFunds")
        val requiredEquityMutualFunds: Int? = 0
    )

}