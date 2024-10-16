package com.techglock.health.app.model.aktivo

import com.techglock.health.app.model.BaseRequest
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AktivoGetUserTokenModel(
    @Expose
    @SerializedName("JSONData")
    private val jsonData: JsonObject, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class AktivoGetUserTokenResponse(
        @Expose
        @SerializedName("token_type")
        val tokenType: String? = "",
        @Expose
        @SerializedName("access_token")
        val accessToken: String? = "",
        @Expose
        @SerializedName("refresh_token")
        val refreshToken: String? = "",
        @Expose
        @SerializedName("expires_in")
        val expiresIn: Int? = 0,
        @Expose
        @SerializedName("scope")
        val scope: List<String> = listOf()
    )

}