package com.techglock.health.app.model.sudLifePolicy

import com.techglock.health.app.model.BaseRequest
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SudKypPdfModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: JsonObject, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class SudKypPdfResponse(
        @SerializedName("Result")
        @Expose
        val result: Result = Result()
    )

    data class Result(
        @SerializedName("Status")
        @Expose
        val status: String? = "",
        @SerializedName("BASE64")
        @Expose
        val pdf_Base64: String? = "",
        @SerializedName("PDF_LINK")
        @Expose
        val pdf_Link: String? = ""
    )

}