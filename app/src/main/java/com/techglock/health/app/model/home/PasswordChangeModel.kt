package com.techglock.health.app.model.home

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PasswordChangeModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("LoginName")
        @Expose
        private val loginName: String = "",
        @SerializedName("EmailAddress")
        @Expose
        private val emailAddress: String = "",
        @SerializedName("PrimaryPhone")
        @Expose
        private val primaryPhone: String = "",
        @SerializedName("OldPassword")
        @Expose
        private val oldPassword: String = "",
        @SerializedName("NewPassword")
        @Expose
        private val newPassword: String = ""
    )

    data class ChangePasswordResponse(
        @SerializedName("NewPassword")
        @Expose
        val newPassword: String = ""
    )
}