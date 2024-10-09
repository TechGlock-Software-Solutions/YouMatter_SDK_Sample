package com.techglock.health.app.model.home

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NimeyaModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("AccountID")
        var accountID: Int = 0,
        @SerializedName("PersonID")
        var personID: Int = 0
    )

    data class NimeyaResponse(
        @SerializedName("url")
        val url: String = ""
    )

}