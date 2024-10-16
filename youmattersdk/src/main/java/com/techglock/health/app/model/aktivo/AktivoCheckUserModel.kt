package com.techglock.health.app.model.aktivo

import com.techglock.health.app.model.BaseRequest
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AktivoCheckUserModel(
    @Expose
    @SerializedName("JSONData")
    private val jsonData: JsonObject, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class AktivoCheckUserResponse(
        @Expose
        @SerializedName("data")
        val result: Data = Data()
    )

    data class Data(
        @Expose
        @SerializedName("member")
        val Member: Member = Member(),
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
        @SerializedName("status")
        val status: String? = "",
        @Expose
        @SerializedName("exists")
        val exists: Boolean = false
    )

    data class Company(
        @Expose
        @SerializedName("_id")
        val id: String? = "",
        @Expose
        @SerializedName("externalId")
        val externalId: String? = ""
    )

}