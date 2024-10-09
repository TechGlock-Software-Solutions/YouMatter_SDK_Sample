package com.techglock.health.app.model.home

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetSSOUrlModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("AccountID")
        var accountID: Int = 0,
        @SerializedName("PersonID")
        var personID: Int = 0,
        @SerializedName("ModuleCode")
        var moduleCode: String = "",
        @SerializedName("IntegrationCode")
        var integrationCode: String = ""
    )

    data class GetSSOUrlResponse(
        @SerializedName("SSO")
        val sso: SSO = SSO()
    )

    data class SSO(
        @SerializedName("SSOURL")
        val ssoUrl: String = ""
    )

}
