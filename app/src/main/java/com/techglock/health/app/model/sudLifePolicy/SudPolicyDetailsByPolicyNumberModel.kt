package com.techglock.health.app.model.sudLifePolicy

import com.techglock.health.app.model.BaseRequest
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SudPolicyDetailsByPolicyNumberModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: JsonObject, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class SudPolicyByMobileNumberResponse(
        @SerializedName("Result")
        @Expose
        val result: List<PolicyDetails> = listOf()
    )

    data class PolicyDetails(
        @SerializedName("Policy_No")
        var policyNo: String? = "",
        @SerializedName("Customer_Code")
        var customerCode: String? = "",
        @SerializedName("Plan_Name")
        var planName: String? = "",
        @SerializedName("Policy_Status")
        var policyStatus: String? = "",
        @SerializedName("Premium_Amount")
        var premiumAmount: String? = "",
        @SerializedName("Next_Renewal_Due")
        var nextRenewalDue: String? = "",
        @SerializedName("Premium_Payment_Term")
        var premiumPaymentTerm: String? = "",
        @SerializedName("Policy_Term")
        var policyTerm: String? = "",
        @SerializedName("Payment_Frequency")
        var paymentFrequency: String? = "",
        @SerializedName("Maturity_Date")
        var maturityDate: String? = "",
        @SerializedName("Risk_Commencement_Date")
        var riskCommencementDate: String? = "",
        @SerializedName("Sum_Assured_Amount")
        var sumAssuredAmount: String? = ""
    )

}