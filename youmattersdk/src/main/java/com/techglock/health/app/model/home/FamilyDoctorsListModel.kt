package com.techglock.health.app.model.home

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FamilyDoctorsListModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("AccountID")
        @Expose
        private val accountID: String = ""
    )

    data class FamilyDoctorsResponse(
        @SerializedName("ListFamilyDoctors")
        val listFamilyDoctors: List<FamilyDoctor> = listOf()
    )

}