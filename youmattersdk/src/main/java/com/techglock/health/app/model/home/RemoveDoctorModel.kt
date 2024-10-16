package com.techglock.health.app.model.home

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RemoveDoctorModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("ID")
        val id: List<Int>
    )


    data class RemoveDoctorResponse(
        @SerializedName("IsProcessed")
        val isProcessed: String = ""
    )

}