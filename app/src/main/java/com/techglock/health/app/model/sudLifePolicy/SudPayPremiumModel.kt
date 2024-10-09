package com.techglock.health.app.model.sudLifePolicy

import com.techglock.health.app.model.BaseRequest
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SudPayPremiumModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: JsonObject, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class SudPayPremiumResponse(
        @SerializedName("Result")
        @Expose
        val result: Result = Result()
    )

    data class Result(
        @SerializedName("ResponseCode")
        @Expose
        val responseCode: String? = "",
        @SerializedName("ResponseMesage")
        @Expose
        val responseMesage: String? = "",
        @SerializedName("ShortlUrl")
        @Expose
        val shortlUrl: String? = "",
        @SerializedName("Premium_Amount")
        @Expose
        val premiumAmount: String? = "",
        @SerializedName("DueDate")
        @Expose
        val dueDate: String? = ""
    )

}