package com.techglock.health.app.model.home

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class EventsBannerModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("AllEvents")
        @Expose
        val allProducts: String = "ALL",
        @SerializedName("PersonID")
        @Expose
        var personID: Int = 0,
        @SerializedName("AccountID")
        @Expose
        var accountId: Int = 0,
        @SerializedName("OrgEmpID")
        @Expose
        var orgEmpID: String = "",
        @SerializedName("OrgName")
        @Expose
        var orgName: String = ""
    )

    data class EventsBannerResponse(
        @SerializedName("EventsBannerDetailList")
        @Expose
        val eventsBannerDetailList: List<EventsBannerDetail> = listOf()
    )

    data class EventsBannerDetail(
        @SerializedName("ID")
        @Expose
        val id: Int = 0,
        @SerializedName("Name")
        @Expose
        val name: String = "",
        @SerializedName("Title")
        @Expose
        val title: String = "",
        @SerializedName("Description")
        @Expose
        val description: String = "",
        @SerializedName("RedirectURL")
        @Expose
        val redirectURL: String = "",
        @SerializedName("RedirectType")
        @Expose
        val redirectType: String = "",
        @SerializedName("FeatureCode")
        @Expose
        val featureCode: String = "",
        @SerializedName("StartDate")
        @Expose
        val startDate: String = "",
        @SerializedName("EndDate")
        @Expose
        val endDate: String = ""
    )

}