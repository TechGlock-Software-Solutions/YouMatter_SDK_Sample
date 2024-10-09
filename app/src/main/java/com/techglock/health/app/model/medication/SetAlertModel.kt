package com.techglock.health.app.model.medication

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SetAlertModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("MedicationID")
        val medicationID: String = "",
        @SerializedName("SetAlert")
        val setAlert: Boolean = false
    )

    data class SetAlertResponse(
        @SerializedName("IsProcessed")
        var isProcessed: Boolean = false
    )

}