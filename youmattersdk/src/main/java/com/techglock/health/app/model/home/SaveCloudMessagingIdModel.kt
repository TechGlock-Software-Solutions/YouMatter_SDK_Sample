package com.techglock.health.app.model.home

//import com.caressa.allizhealth.app.model.tempconst.Configuration
import com.techglock.health.app.common.constants.Configuration
import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class SaveCloudMessagingIdModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {


    data class JSONDataRequest(
        @SerializedName("RequestType")
        val requestType: String = "POST",
        @SerializedName("Key")
        val key: Key = Key()
    )

    data class Key(
        @SerializedName("Mode")
        val mode: String = "ADD",
        @SerializedName("AppIdentifier")
        val appIdentifier: String = Configuration.AppIdentifier,
        @SerializedName("DeviceType")
        val deviceType: String = "ANDROID",
        @SerializedName("AccountID")
        val accountID: String = "",
        @SerializedName("RegistrationID")
        val registrationID: String = "",
        @SerializedName("UserMetaData") val userMetaData: UserMetaData = UserMetaData()
    )

    data class UserMetaData(

        @SerializedName("name")
        val name: String = "",
        @SerializedName("email")
        val email: String = "",
        @SerializedName("contact")
        val contact: String = "",
        @SerializedName("gender")
        val gender: String = "",
        @SerializedName("dateofbirth")
        val dateofbirth: String? = "",
        @SerializedName("languageCode")
        val languageCode: String = Configuration.LanguageCode,
        @SerializedName("location")
        val location: String = "",
        @SerializedName("timezone")
        val timezone: String = TimeZone.getDefault().id,
        @SerializedName("standardname")
        val standardname: String = TimeZone.getDefault().getDisplayName(Locale("en")),
    )

    data class SaveCloudMessagingIdResponse(
        @SerializedName("RegistrationID")
        val registrationID: String = ""
    )
}