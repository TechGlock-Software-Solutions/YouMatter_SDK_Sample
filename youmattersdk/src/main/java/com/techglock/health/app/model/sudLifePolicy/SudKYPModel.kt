package com.techglock.health.app.model.sudLifePolicy

import com.techglock.health.app.model.BaseRequest
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SudKYPModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: JsonObject, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    /*    data class SudKYPResponse(
            @SerializedName("status")
            @Expose
            val status: String? = "",
            @SerializedName("KYPList")
            @Expose
            val kYPList: List<KYP> = listOf()
        )*/

    data class SudKYPResponse(
        @SerializedName("Result")
        @Expose
        val result: Result = Result()
    )

    data class Result(
        @SerializedName("status")
        @Expose
        val status: String? = "",
        @SerializedName("KYPList")
        @Expose
        val kYPList: List<KYP> = listOf()
    )

    data class KYP(
        @SerializedName("Receipt_ID")
        @Expose
        val receiptID: String? = "",
        @SerializedName("Receipt_Date")
        @Expose
        val receiptDate: String? = "",
        @SerializedName("Application_no")
        @Expose
        val applicationNo: String? = "",
        @SerializedName("Dear")
        val dear: String? = "",
        @SerializedName("Bnfy_Name")
        @Expose
        val bnfyName: String? = "",
        @SerializedName("Policy_Number")
        @Expose
        val policyNumber: String? = "",
        @SerializedName("Contract_Type_Desc")
        @Expose
        val contractTypeDesc: String? = "",
        @SerializedName("Owner_Email_Addr")
        @Expose
        val ownerEmailAddr: String? = "",
        @SerializedName("Owner_Mobile")
        @Expose
        val ownerMobile: String? = "",
        @SerializedName("Address1")
        @Expose
        val address1: String? = "",
        @SerializedName("Address2")
        @Expose
        val address2: String? = "",
        @SerializedName("Address3")
        @Expose
        val address3: String? = "",
        @SerializedName("Address4")
        @Expose
        val address4: String? = "",
        @SerializedName("Address5")
        @Expose
        val address5: String? = "",
        @SerializedName("Post_Code")
        @Expose
        val postCode: String? = "",
        @SerializedName("Basic_Sum_Assured")
        @Expose
        val basicSumAssured: String? = "",
        @SerializedName("Coverage_Period")
        @Expose
        val coveragePeriod: String? = "",
        @SerializedName("Free_Look_Period")
        @Expose
        val freeLookPeriod: String? = "",
        @SerializedName("GST")
        @Expose
        val gST: String? = "",
        @SerializedName("LA_Age")
        @Expose
        val lAAge: String? = "",
        @SerializedName("LA_Gender")
        @Expose
        val lAGender: String? = "",
        @SerializedName("LA_Name")
        @Expose
        val lAName: String? = "",
        @SerializedName("Last_Premium_Due_Date")
        @Expose
        val lastPremiumDueDate: String? = "",
        @SerializedName("Maturity_Date")
        @Expose
        val maturityDate: String? = "",
        @SerializedName("Mode_of_Premium_Payment")
        @Expose
        val modeOfPremiumPayment: String? = "",
        @SerializedName("Next_Premium_Due_Date")
        @Expose
        val nextPremiumDueDate: String? = "",
        @SerializedName("Premium_Paying_Term")
        @Expose
        val premiumPayingTerm: String? = "",
        @SerializedName("RCD_Date")
        @Expose
        val rCDDate: String? = "",
        @SerializedName("Total_Basic_Premium_excl_GST")
        @Expose
        val totalBasicPremiumExclGST: String? = "",
        @SerializedName("Total_Premium_Amount")
        @Expose
        val totalPremiumAmount: String? = ""
    )

}