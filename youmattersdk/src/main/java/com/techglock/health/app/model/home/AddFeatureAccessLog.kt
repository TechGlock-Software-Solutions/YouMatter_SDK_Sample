package com.techglock.health.app.model.home

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddFeatureAccessLog(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("FeatureAccessLog")
        val featureAccessLog: FeatureAccessLog = FeatureAccessLog()
    )

    data class FeatureAccessLog(
        @SerializedName("PartnerCode")
        var partnerCode: String = "",
        @SerializedName("PersonID")
        var personId: Int = 0,
        @SerializedName("Service")
        var service: String = "",
        @SerializedName("Code")
        var code: String = "",
        @SerializedName("URL")
        var url: String = "",
        @SerializedName("Description")
        var description: String = "",
        @SerializedName("AppVersion")
        var appversion: String = "",
        @SerializedName("Device")
        var device: String = "",
        @SerializedName("DeviceType")
        var devicetype: String = "",
        @SerializedName("Platform")
        var platform: String = ""
    )

    data class AddFeatureAccessLogResponse(
        @SerializedName("FeatureAccessLogID")
        var featureAccessLogID: String = ""
    )

}
