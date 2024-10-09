package com.techglock.health.app.model.home

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WellfieGetVitalsModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("Mode")
        val mode: String = "",
        @SerializedName("PersonID")
        val personID: Int = 0
    )

    data class WellfieGetVitalsResponse(
        @SerializedName("Result")
        val result: Result? = Result()
    )

    data class Result(
        @SerializedName("Report")
        val report: List<Report> = listOf()
    )

    data class Report(
        @SerializedName("ParamCode")
        val paramCode: String? = "",
        @SerializedName("Name")
        val name: String? = "",
        @SerializedName("Value")
        val value: String? = "",
        @SerializedName("Observation")
        val observation: Any? = Any(),
        @SerializedName("Unit")
        val unit: Any? = Any(),
        @SerializedName("ColorCode")
        val colorCode: Any? = Any(),
        @SerializedName("RecordDateTime")
        val recordDateTime: String? = "",

        @SerializedName("Gender")
        val gender: String? = "",
        @SerializedName("Height")
        val height: String? = "",
        @SerializedName("Weight")
        val weight: String? = "",
        @SerializedName("PersonID")
        val personID: Int? = 0,
        @SerializedName("PartnerID")
        val partnerID: Int? = 0,
        @SerializedName("ClusterID")
        val clusterID: Int? = 0,
    )

}