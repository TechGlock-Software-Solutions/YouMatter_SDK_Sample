package com.techglock.health.app.model.sudLifePolicy

import com.techglock.health.app.model.BaseRequest
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SudRenewalPremiumModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: JsonObject, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class SudRenewalPremiumResponse(
        @SerializedName("Result")
        @Expose
        val records: List<RenewalPremium> = listOf(),
    )

    data class RenewalPremium(
        @SerializedName("Contract_Number")
        val contractNumber: String? = "",
        @SerializedName("Next_Renewal_Due")
        val nextRenewalDue: String? = "",
        @SerializedName("Premium_Amount")
        val premiumAmount: String? = ""
    )
}