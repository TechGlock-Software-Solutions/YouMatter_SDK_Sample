package com.techglock.health.app.model.nimeya

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveProtectoMeterModel(
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
        @SerializedName("dateOfBirth")
        @Expose
        val dateOfBirth: String? = "",
        @SerializedName("monthlyIncome")
        @Expose
        val monthlyIncome: Int? = 0,
        @SerializedName("monthlyExpenses")
        @Expose
        val monthlyExpenses: Int? = 0,
        @SerializedName("parentsMonthlyCost")
        @Expose
        val parentsMonthlyCost: Int? = 0,
        @SerializedName("lifeInsuranceCoverage")
        @Expose
        val lifeInsuranceCoverage: Int? = 0,
        @SerializedName("healthInsuranceCoverage")
        @Expose
        val healthInsuranceCoverage: Int? = 0,
        @SerializedName("outstandingLoans")
        @Expose
        val outstandingLoans: Int? = 0,
        @SerializedName("otherLiabilities")
        @Expose
        val otherLiabilities: Int? = 0,
        @SerializedName("investmentPortfolio")
        @Expose
        val investmentPortfolio: Int? = 0,
        @SerializedName("retirementAge")
        @Expose
        val retirementAge: Int? = 0,
        @SerializedName("familyDetails")
        @Expose
        val familyDetails: List<FamilyDetail> = listOf()
    )

    data class FamilyDetail(
        @SerializedName("id")
        @Transient
        var id: Int = 0,
        @SerializedName("name")
        @Expose
        var name: String? = "",
        @SerializedName("relation")
        @Expose
        var relation: String? = "",
        @SerializedName("dob")
        @Expose
        var dob: String? = "",
        @SerializedName("age")
        @Expose
        var age: String? = "",
        @SerializedName("is_dependent")
        @Expose
        var isDependent: String? = "",
        @SerializedName("monthly_expense")
        @Expose
        var monthlyExpense: String? = "0"
    )

    data class ProtectoMeterSaveResponse(
        @SerializedName("SaveProtectoMeter")
        @Expose
        val saveProtectoMeter: SaveProtectoMeter = SaveProtectoMeter()
    )

    data class SaveProtectoMeter(
        @Expose
        @SerializedName("status")
        val status: String? = "",
        @Expose
        @SerializedName("data")
        val data: Data = Data()
    )

    data class Data(
        @Expose
        @SerializedName("lifeInsuranceScore")
        val lifeInsuranceScore: Int? = 0,
        @Expose
        @SerializedName("lifeInsuranceScoreText")
        val lifeInsuranceScoreText: String? = "",
        @Expose
        @SerializedName("healthInsuranceScore")
        val healthInsuranceScore: Int? = 0,
        @Expose
        @SerializedName("healthInsuranceScoreText")
        val healthInsuranceScoreText: String? = "",
        @Expose
        @SerializedName("healthInsuranceNeed")
        val healthInsuranceNeed: Int? = 0,
        @Expose
        @SerializedName("lifeInsuranceNeed")
        val lifeInsuranceNeed: Int? = 0,
        @Expose
        @SerializedName("LifeInsuranceCover")
        val lifeInsuranceCover: Int? = 0,
        @Expose
        @SerializedName("HealthInsuranceCover")
        val healthInsuranceCover: Int? = 0,
        @Expose
        @SerializedName("DateTime")
        val dateTime: String? = "",
        @Expose
        @SerializedName("familyDetails")
        val familyDetails: List<FamilyDetail> = listOf()
        //val familyDetails: String? = ""
    )

}