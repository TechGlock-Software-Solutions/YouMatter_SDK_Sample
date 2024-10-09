package com.techglock.health.app.model.hra

import com.techglock.health.app.model.BaseRequest
import com.techglock.health.app.model.entity.HRASummary
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HraMedicalProfileSummaryModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("PersonID")
        @Expose
        private val PersonID: String = "",
        @SerializedName("LastSyncDate")
        @Expose
        private val LastSyncDate: String = "01-JAN-1901"
    )

    data class MedicalProfileSummaryResponse(
        @SerializedName("MedicalProfileSummary")
        @Expose
        var MedicalProfileSummary: HRASummary? = null
    )

}