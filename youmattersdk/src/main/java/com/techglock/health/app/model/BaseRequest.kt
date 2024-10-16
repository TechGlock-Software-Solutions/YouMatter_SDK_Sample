package com.techglock.health.app.model


//import com.caressa.allizhealth.app.model.tempconst.Configuration
import com.techglock.health.app.common.constants.Configuration
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.Utilities
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*


open class BaseRequest(
    @SerializedName("Header")
    @Expose
    val header: Header
) {

    data class Header(
        @SerializedName("RequestID")
        @Expose
        private val requestID: String = Configuration.RequestID,
        @SerializedName("DateTime")
        @Expose
        private val dateTime: String = currentDateAsString_yyyy_MM_dd,
        @SerializedName("ApplicationCode")
        @Expose
        private val applicationCode: String = Configuration.ApplicationCode,
        @SerializedName("AuthTicket")
        @Expose
        private val authTicket: String = "",
        @SerializedName("PartnerCode")
        @Expose
        private val partnerCode: String = Constants.PartnerCode,
        @SerializedName("EntityType")
        @Expose
        private val entityType: String = Configuration.EntityType,
        @SerializedName("HandShake")
        @Expose
        private val handShake: String = Configuration.Handshake,
        @SerializedName("UTMSource")
        @Expose
        private val utmSource: String = Configuration.UTMSource,
        @SerializedName("UTMMedium")
        @Expose
        private val utmMedium: String = Configuration.UTMMedium,
        @SerializedName("EntityID")
        @Expose
        private val entityID: String = Configuration.EntityID.ifEmpty { "0" },
        @SerializedName("RefID")
        @Expose
        private val refID: String = Utilities.getRefID(),
        @SerializedName("AppVersion")
        @Expose
        private val appVersion: String = Constants.versionCode.toString()
    )

    companion object {
        fun replaceChars(strVal: String): String {
            return strVal.replace("\\u003d", "=")
        }

        /* Current Date : */ val currentDateAsString_yyyy_MM_dd: String
            get() {
                val calendar = Calendar.getInstance()
                val df = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                return df.format(calendar.time)
            }
    }


}
