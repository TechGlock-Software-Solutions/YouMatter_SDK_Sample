package com.techglock.health.app.model.sudLifePolicy

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PolicyProductsModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("Screen")
        @Expose
        val screen: String = "",
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
        var orgName: String = "",
        @SerializedName("UserType")
        @Expose
        var userType: String = ""
    )

    data class PolicyProductsResponse(
        @SerializedName("PolicyProducts")
        @Expose
        val policyProducts: List<PolicyProducts> = listOf()
    )

    data class PolicyProducts(
        @SerializedName("ID")
        @Expose
        val id: Int = 0,
        @SerializedName("ProductID")
        @Expose
        val productID: Int = 0,
        @SerializedName("ProductCode")
        @Expose
        var productCode: String = "",
        @SerializedName("ProductName")
        @Expose
        val productName: String = "",
        @SerializedName("ProductImageURL")
        @Expose
        val productImageURL: String = "",
        @SerializedName("ProductRedirectionURL")
        @Expose
        val productRedirectionURL: String = "",
        @SerializedName("DisclaimerText")
        @Expose
        var disclaimerText: String = "",
        @SerializedName("DisclaimerUrl")
        @Expose
        var disclaimerUrl: String = "",
        @SerializedName("ProductShareURL")
        @Expose
        var productShareURL: String = "",
        @SerializedName("Screen")
        @Expose
        val screen: String = "",
        @SerializedName("PopUpEnabled")
        @Expose
        var popUpEnabled: Boolean = false
    )

}