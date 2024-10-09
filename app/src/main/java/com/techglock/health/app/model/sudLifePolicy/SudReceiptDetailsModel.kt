package com.techglock.health.app.model.sudLifePolicy

import com.techglock.health.app.model.BaseRequest
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SudReceiptDetailsModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: JsonObject, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

/*    data class SudReceiptDetailsResponse(
        @SerializedName("Result")
        @Expose
        val result: List<ReceiptDetails> = listOf()
    )*/

    data class SudReceiptDetailsResponse(
        @Expose
        @SerializedName("Result")
        val result: Result = Result()
    )

    data class Result(
        @Expose
        @SerializedName("Status")
        val status: String? = "",
        @Expose
        @SerializedName("InstantIssuanceResponse")
        val instantIssuanceResponse: List<InstantIssuanceResponse> = listOf()
    )

    data class InstantIssuanceResponse(
        @Expose
        @SerializedName("Address1")
        val address1: String? = "",
        @Expose
        @SerializedName("Address2")
        val address2: String? = "",
        @Expose
        @SerializedName("Address3")
        val address3: String? = "",
        @Expose
        @SerializedName("Address4")
        val address4: String? = "",
        @Expose
        @SerializedName("Address5")
        val address5: String? = "",
        @Expose
        @SerializedName("Application_no")
        val applicationNo: String? = "",
        @Expose
        @SerializedName("Base_Premium_CGST_Amount")
        val basePremiumCGSTAmount: String? = "",
        @Expose
        @SerializedName("Base_Premium_IGST_Amount")
        val basePremiumIGSTAmount: String? = "",
        @Expose
        @SerializedName("Base_Premium_SGST_Amount")
        val basePremiumSGSTAmount: String? = "",
        @Expose
        @SerializedName("Bnfy_Name")
        val bnfyName: String? = "",
        @Expose
        @SerializedName("Contract_Type_Desc")
        val contractTypeDesc: String? = "",
        @Expose
        @SerializedName("Covr_Prem")
        val covrPrem: String? = "",
        @Expose
        @SerializedName("Covr_Prem_Term")
        val covrPremTerm: String? = "",
        @Expose
        @SerializedName("CovrSI")
        val covrSI: String? = "",
        @Expose
        @SerializedName("Current_Bill_Channel_Desc")
        val currentBillChannelDesc: String? = "",
        @Expose
        @SerializedName("Current_Bill_freq_Desc")
        val currentBillFreqDesc: String? = "",
        @Expose
        @SerializedName("Dear")
        val dear: String? = "",
        @Expose
        @SerializedName("Owner_Age")
        val ownerAge: String? = "",
        @Expose
        @SerializedName("Owner_DOB")
        val ownerDOB: String? = "",
        @Expose
        @SerializedName("Owner_Email_Addr")
        val ownerEmailAddr: String? = "",
        @Expose
        @SerializedName("Owner_Gender")
        val ownerGender: String? = "",
        @Expose
        @SerializedName("Owner_Mobile")
        val ownerMobile: String? = "",
        @Expose
        @SerializedName("Policy_Number")
        val policyNumber: String? = "",
        @Expose
        @SerializedName("Post_Code")
        val postCode: String? = "",
        @Expose
        @SerializedName("Product_UIN_Code")
        val productUINCode: String? = "",
        @Expose
        @SerializedName("Proposal_Date")
        val proposalDate: String? = "",
        @Expose
        @SerializedName("Receipt_Amount")
        val receiptAmount: String? = "",
        @Expose
        @SerializedName("Receipt_Date")
        val receiptDate: String? = "",
        @Expose
        @SerializedName("Receipt_ID")
        val receiptID: String? = "",
        @Expose
        @SerializedName("Rider_Opted")
        val riderOpted: String? = "",
        @Expose
        @SerializedName("Risk_cess_term")
        val riskCessTerm: String? = "",
        @Expose
        @SerializedName("Tot_covr_prem")
        val totCovrPrem: String? = "",
        @Expose
        @SerializedName("Tot_Prem")
        val totPrem: String? = ""
    )

    data class ReceiptDetails(
        @SerializedName("Address1")
        val address1: String? = "",
        @SerializedName("Address2")
        val address2: String? = "",
        @SerializedName("Address3")
        val address3: String? = "",
        @SerializedName("Address4")
        val address4: String? = "",
        @SerializedName("Address5")
        val address5: String? = "",
        @SerializedName("Application_no")
        val applicationNo: String? = "",
        @SerializedName("Base_Premium_CGST_Amount")
        val basePremiumCGSTAmount: Double? = 0.0,
        @SerializedName("Base_Premium_IGST_Amount")
        val basePremiumIGSTAmount: Double? = 0.0,
        @SerializedName("Base_Premium_SGST_Amount")
        val basePremiumSGSTAmount: Double? = 0.0,
        @SerializedName("Bnfy_Name")
        val bnfyName: String? = "",
        @SerializedName("Contract_Type_Desc")
        val contractTypeDesc: String? = "",
        @SerializedName("Covr_Prem")
        val covrPrem: Double? = 0.0,
        @SerializedName("Covr_Prem_Term")
        val covrPremTerm: String? = "",
        @SerializedName("CovrSI")
        val covrSI: Double? = 0.0,
        @SerializedName("Current_Bill_Channel_Desc")
        val currentBillChannelDesc: String? = "",
        @SerializedName("Current_Bill_freq_Desc")
        val currentBillFreqDesc: String? = "",
        @SerializedName("Dear")
        val dear: String? = "",
        @SerializedName("ETL_DATE")
        val eTLDATE: String? = "",
        @SerializedName("Flag")
        val flag: String? = "",
        @SerializedName("Owner_Age")
        val ownerAge: Int? = 0,
        @SerializedName("Owner_DOB")
        val ownerDOB: String? = "",
        @SerializedName("Owner_Email_Addr")
        val ownerEmailAddr: String? = "",
        @SerializedName("Owner_Gender")
        val ownerGender: String? = "",
        @SerializedName("Owner_Mobile")
        val ownerMobile: String? = "",
        @SerializedName("Policy_Number")
        val policyNumber: String? = "",
        @SerializedName("Post Code")
        val postCode: String? = "",
        @SerializedName("Product_UIN_Code")
        val productUINCode: String? = "",
        @SerializedName("Proposal_Date")
        val proposalDate: String? = "",
        @SerializedName("Receipt_Amount")
        val receiptAmount: Double? = 0.0,
        @SerializedName("Receipt_Date")
        val receiptDate: String? = "",
        @SerializedName("Receipt_ID")
        val receiptID: String? = "",
        @SerializedName("Rider_Opted")
        val riderOpted: String? = "",
        @SerializedName("Risk_cess_term")
        val riskCessTerm: String? = "",
        @SerializedName("Tot_covr_prem")
        val totCovrPrem: Double? = 0.0,
        @SerializedName("Tot_Prem")
        val totPrem: Double? = 0.0
    )

}