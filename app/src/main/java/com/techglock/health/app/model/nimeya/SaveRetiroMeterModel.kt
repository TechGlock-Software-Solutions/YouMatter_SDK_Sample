package com.techglock.health.app.model.nimeya

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveRetiroMeterModel(
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
        @SerializedName("dateOfBirth")
        val dateOfBirth: String? = "",
        @Expose
        @SerializedName("plannedRetirementAge")
        val plannedRetirementAge: Int? = 0,
        @Expose
        @SerializedName("monthlyIncome")
        val monthlyIncome: Int? = 0,
        @Expose
        @SerializedName("desiredRetirementIncome")
        val desiredRetirementIncome: Int? = 0,
        @Expose
        @SerializedName("retirementSavings")
        val retirementSavings: RetirementSavings = RetirementSavings(),
        @Expose
        @SerializedName("monthlySavingsRate")
        val monthlySavingsRate: MonthlySavingsRate = MonthlySavingsRate()
    )

    data class RetirementSavings(
        @Expose
        @SerializedName("ppf")
        val ppf: Int? = 0,
        @Expose
        @SerializedName("epf")
        val epf: Int? = 0,
        @Expose
        @SerializedName("nps")
        val nps: Int? = 0,
        @Expose
        @SerializedName("equityMutualFunds")
        val equityMutualFunds: Int? = 0
    )

    data class MonthlySavingsRate(
        @Expose
        @SerializedName("ppf")
        val ppf: Int? = 0,
        @Expose
        @SerializedName("epf")
        val epf: Int? = 0,
        @Expose
        @SerializedName("nps")
        val nps: Int? = 0,
        @Expose
        @SerializedName("equityMutualFunds")
        val equityMutualFunds: Int? = 0
    )

    data class SaveRetiroMeterResponse(
        @SerializedName("SaveRetiroMeter")
        @Expose
        val saveRetiroMeter: SaveRetiroMeter = SaveRetiroMeter()
    )

    data class SaveRetiroMeter(
        @Expose
        @SerializedName("status")
        val status: String? = "",
        @Expose
        @SerializedName("data")
        val data: Data = Data()
    )

    data class Data(
        @Expose
        @SerializedName("score")
        val score: Int? = 0,
        @Expose
        @SerializedName("score_text")
        val scoreText: String? = "",
        @Expose
        @SerializedName("message")
        val message: String? = "",
        @Expose
        @SerializedName("your_desired_retirement_income")
        val yourDesireRetirementIncome: Int? = 0,
        @Expose
        @SerializedName("projected_monthly_retirement_income_per_month")
        val projectedMonthlyRetirementIncomePerMonth: Int? = 0,
        @Expose
        @SerializedName("required_monthly_retirement_savings")
        val requiredMonthlyRetirementSavings: Int? = 0,
        @Expose
        @SerializedName("required_ppf")
        val requiredPpf: Int? = 0,
        @Expose
        @SerializedName("required_epf")
        val requiredEpf: Int? = 0,
        @Expose
        @SerializedName("required_nps")
        val requiredNps: Int? = 0,
        @Expose
        @SerializedName("required_equityMutualFunds")
        val requiredEquityMutualFunds: Int? = 0,
        @Expose
        @SerializedName("age")
        val age: Int? = 0,
        @Expose
        @SerializedName("plannedRetirementAge")
        val plannedRetirementAge: Int? = 0,
        @Expose
        @SerializedName("yourCurrentMonthlyIncome")
        val yourCurrentMonthlyIncome: Int? = 0,
        /*        @Expose
                @SerializedName("req_detail")
                val reqDetail: String? = ""*/
    )

    data class ReqDetail(
        @Expose
        @SerializedName("dateOfBirth")
        val dateOfBirth: String? = "",
        @Expose
        @SerializedName("desiredRetirementIncome")
        val desiredRetirementIncome: Int? = 0,
        @Expose
        @SerializedName("plannedRetirementAge")
        val plannedRetirementAge: Int? = 0,
        @Expose
        @SerializedName("monthlyIncome")
        val monthlyIncome: Int? = 0,
        @Expose
        @SerializedName("monthlySavingsRate")
        val monthlySavingsRate: MonthlySavingsRate = MonthlySavingsRate(),
        @Expose
        @SerializedName("retirementSavings")
        val retirementSavings: RetirementSavings = RetirementSavings()
    )

}