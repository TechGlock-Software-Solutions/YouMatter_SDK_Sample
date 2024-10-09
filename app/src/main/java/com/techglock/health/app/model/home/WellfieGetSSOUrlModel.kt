package com.techglock.health.app.model.home

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WellfieGetSSOUrlModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("AccountID")
        val accountID: Int = 0,
        @SerializedName("PersonID")
        val personID: Int = 0
    )

    data class WellfieGetSSOUrlResponse(
        @SerializedName("SSO")
        val sso: SSO = SSO()
    )

    data class SSO(
        @SerializedName("BearerToken")
        val bearerToken: String? = "",
        @SerializedName("Token")
        val token: String? = "",
        @SerializedName("Wellfie_Socket_Url")
        val wellfie_Socket_Url: String? = "",
        @SerializedName("Wellfie_Socket_Token")
        val wellfie_Socket_Token: String? = "",
        @SerializedName("ProcessDataAPIUrl")
        val processDataAPIUrl: String? = "",
        @SerializedName("ClientID")
        val clientID: String? = "",
        @SerializedName("PersonID")
        val personID: Int? = 0,
        @SerializedName("AccountID")
        val accountID: Int? = 0
    )

}
