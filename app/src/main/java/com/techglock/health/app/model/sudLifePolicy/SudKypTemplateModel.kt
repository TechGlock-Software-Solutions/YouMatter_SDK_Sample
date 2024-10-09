package com.techglock.health.app.model.sudLifePolicy

import com.techglock.health.app.model.BaseRequest
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SudKypTemplateModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: JsonObject, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class SudKypTemplateResponse(
        @SerializedName("Result")
        @Expose
        val result: Result = Result()
    )

    data class Result(
        @SerializedName("id")
        @Expose
        val id: String? = "",
        @SerializedName("message_id")
        @Expose
        val messageId: String? = "",
        @SerializedName("success")
        @Expose
        val success: Boolean = false,
        @SerializedName("response")
        @Expose
        val response: String? = "",
        @SerializedName("description")
        @Expose
        val description: String? = "",
        @SerializedName("data")
        @Expose
        val data: String? = ""
    )

}