package com.techglock.health.app.model.nimeya


import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetProtectoMeterHistoryModel(
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

    data class ProtectoMeterHistoryResponse(
        @SerializedName("GetProtectoMeterHistory")
        @Expose
        val getProtectoMeterHistory: GetProtectoMeterHistory = GetProtectoMeterHistory()
    )

    data class GetProtectoMeterHistory(
        @Expose
        @SerializedName("PersonID")
        var personID: Int = 0,
        @Expose
        @SerializedName("AccountID")
        var accountID: Int = 0,
        @Expose
        @SerializedName("LifeInsuranceScore")
        val lifeInsuranceScore: String? = "",
        @Expose
        @SerializedName("LifeInsuranceScoreText")
        val lifeInsuranceScoreText: String? = "",
        @Expose
        @SerializedName("HealthInsuranceScore")
        val healthInsuranceScore: String? = "",
        @Expose
        @SerializedName("HealthInsuranceScoreText")
        val healthInsuranceScoreText: String? = "",
        @Expose
        @SerializedName("HealthInsuranceNeed")
        val healthInsuranceNeed: String? = "",
        @Expose
        @SerializedName("LifeInsuranceNeed")
        val lifeInsuranceNeed: String? = "",
        @Expose
        @SerializedName("LifeInsuranceCover")
        val lifeInsuranceCover: String? = "",
        @Expose
        @SerializedName("HealthInsuranceCover")
        val healthInsuranceCover: String? = "",
        @Expose
        @SerializedName("FamilyDetails")
        //val familyDetails: String? = ""
        var familyDetails: MutableList<FamilyDetail> = mutableListOf(),
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

}