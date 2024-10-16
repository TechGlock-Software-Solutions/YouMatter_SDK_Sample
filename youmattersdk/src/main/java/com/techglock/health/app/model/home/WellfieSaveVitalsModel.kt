package com.techglock.health.app.model.home

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WellfieSaveVitalsModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("Mode")
        val mode: String = "",
        @SerializedName("PersonID")
        val personID: Int = 0,
        @SerializedName("RecordDate")
        val recordDate: String = "",
        @SerializedName("Height")
        val height: String = "",
        @SerializedName("Weight")
        val weight: String = "",
        @SerializedName("Gender")
        val gender: String = "",
        @SerializedName("WellfieParameters")
        val wellfieParameters: List<WellfieParameter> = listOf()
    )

    data class WellfieParameter(
        @SerializedName("ParameterCode")
        val parameterCode: String = "",
        @SerializedName("Description")
        val description: String = "",
        @SerializedName("Value")
        val value: String = "",
        @SerializedName("Unit")
        val unit: String = "",
        @SerializedName("Observation")
        val observation: String = "",
        @SerializedName("ColorCode")
        val colorCode: String = ""
    )

    data class WellfieSaveVitalsResponse(
        @SerializedName("IsSave")
        val isSave: String? = ""
    )

}
