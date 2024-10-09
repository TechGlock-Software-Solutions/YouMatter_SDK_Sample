package com.techglock.health.app.model.home

//import com.caressa.allizhealth.app.model.tempconst.Configuration
import com.techglock.health.app.common.constants.Configuration
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ContactUsModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("ApplicationCode")
        val applicationCode: String = Configuration.ApplicationCode,
        @SerializedName("PartnerCode")
        val partnerCode: String = Constants.PartnerCode,
        @SerializedName("EmailAddress")
        val emailAddress: String = "",
        @SerializedName("FromEmail")
        val fromEmail: String = "",
        @SerializedName("FromMobile")
        val fromMobile: String = "",
        @SerializedName("Message")
        val message: String = "",
        @SerializedName("Source ")
        val source: String = "MobileApp",
        @SerializedName("RequestType")
        val requestType: String = "POST"
    )

    data class ContactUsResponse(
        // random parameter , since nothing in Response JSON Data
        @SerializedName("IsProcessed")
        var isProcessed: Boolean = false
    )

}