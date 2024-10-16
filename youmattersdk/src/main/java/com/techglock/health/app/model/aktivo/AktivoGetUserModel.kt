package com.techglock.health.app.model.aktivo

import com.techglock.health.app.model.BaseRequest
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AktivoGetUserModel(
    @Expose
    @SerializedName("JSONData")
    private val jsonData: JsonObject, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class AktivoGetUserResponse(
        @Expose
        @SerializedName("data")
        val data: Data = Data()
    )

    data class Data(
        @Expose
        @SerializedName("bedtime")
        val bedtime: String? = "",
        @Expose
        @SerializedName("bmi")
        val bmi: Bmi? = Bmi(),
        @Expose
        @SerializedName("email")
        val email: String? = "",
        @Expose
        @SerializedName("first_login")
        val firstLogin: Any? = Any(),
        @Expose
        @SerializedName("firstname")
        val firstname: String? = "",
        @Expose
        @SerializedName("_id")
        val id: String? = "",
        @Expose
        @SerializedName("lastname")
        val lastname: String? = "",
        @Expose
        @SerializedName("platform_connected")
        val platformConnected: Boolean? = false,
        @Expose
        @SerializedName("platform_connected_datetime")
        val platformConnectedDatetime: String? = "",
        @Expose
        @SerializedName("platform_connected_forever")
        val platformConnectedForever: Boolean? = false,
        @Expose
        @SerializedName("platform_connected_type")
        val platformConnectedType: String? = "",
        @Expose
        @SerializedName("sex")
        val sex: String? = "",
        @Expose
        @SerializedName("timezone")
        val timezone: String? = "",
        @Expose
        @SerializedName("wakeup")
        val wakeup: String? = ""
    )

    data class Bmi(
        @Expose
        @SerializedName("value")
        val value: Double? = 0.0,
        @Expose
        @SerializedName("obesityStatus")
        val obesityStatus: String? = ""
    )

}