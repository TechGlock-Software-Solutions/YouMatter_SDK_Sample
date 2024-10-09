package com.techglock.health.app.model.aktivo

import com.techglock.health.app.model.BaseRequest
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AktivoCreateUserModel(
    @Expose
    @SerializedName("JSONData")
    private val jsonData: JsonObject, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class AktivoCreateUserResponse(
        @Expose
        @SerializedName("data")
        val resultData: Data = Data()
    )

    data class Data(
        @Expose
        @SerializedName("member")
        val member: Member = Member(),
        @Expose
        @SerializedName("company")
        val company: Company = Company()
    )

    data class Member(
        @Expose
        @SerializedName("_id")
        val id: String? = "",
        @Expose
        @SerializedName("externalId")
        val externalId: String? = "",
        @Expose
        @SerializedName("isNew")
        val isNew: Boolean = false
    )

    data class Company(
        @Expose
        @SerializedName("_id")
        val id: String? = "",
        @Expose
        @SerializedName("externalId")
        val externalId: String? = "",
        @Expose
        @SerializedName("isNew")
        val isNew: Boolean = false
    )

}