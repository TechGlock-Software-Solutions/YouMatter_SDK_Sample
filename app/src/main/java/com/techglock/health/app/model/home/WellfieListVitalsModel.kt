package com.techglock.health.app.model.home

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WellfieListVitalsModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("Criteria")
        val criteria: Criteria = Criteria()
    )

    data class Criteria(
        @SerializedName("Mode")
        val mode: String = "",
        @SerializedName("PersonID")
        val personID: Int = 0,
        @SerializedName("FromDate")
        val fromDate: String = "",
        @SerializedName("ToDate")
        val toDate: String = ""
    )

    data class WellfieListVitalsResponse(
        @SerializedName("SSO")
        val sso: SSO = SSO()
    )

    data class SSO(
        @SerializedName("SSOURL")
        val ssoUrl: String = ""
    )

}
