package com.techglock.health.app.model.sudLifePolicy

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SudGroupCOIModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("Mobile_No")
        @Expose
        var mobileNo: String = "",
        @SerializedName("DOB")
        @Expose
        var dob: String = ""
    )

    data class SudGroupCOIResponse(
        @Expose
        @SerializedName("GroupCOIResponse")
        val groupCOIResponse: GroupCOIResponse = GroupCOIResponse()
    )

    data class GroupCOIResponse(
        @SerializedName("status")
        @Expose
        val status: String? = "",
        @SerializedName("BASE64")
        @Expose
        val pdfBASE64: String? = ""
    )

}
