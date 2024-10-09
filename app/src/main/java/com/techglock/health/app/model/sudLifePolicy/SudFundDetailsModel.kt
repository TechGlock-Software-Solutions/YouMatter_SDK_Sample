package com.techglock.health.app.model.sudLifePolicy

import com.techglock.health.app.model.BaseRequest
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SudFundDetailsModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: JsonObject, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class SudFundDetailsResponse(
        @SerializedName("Result")
        @Expose
        val result: Result = Result()
    )

    data class Result(
        @SerializedName("status")
        @Expose
        val status: String? = "",
        @SerializedName("FundDetails")
        @Expose
        val fundDetails: List<FundDetail> = listOf()
    )

    data class FundDetail(
        @SerializedName("Contract_Number")
        @Expose
        val ContractNumber: String? = "",
        @SerializedName("Fund_Name")
        @Expose
        val fundName: String? = "",
        @SerializedName("Fund_Value")
        @Expose
        val fundValue: String? = "",
        @SerializedName("Total_Fund_Value")
        @Expose
        val totalFundValue: String? = "",
        @SerializedName("Units_Allocated")
        @Expose
        val unitsAllocated: String? = "",
        @SerializedName("NAV")
        @Expose
        val nav: String? = "",
        @SerializedName("NAV_Date")
        @Expose
        val navDate: String? = ""
    )

}