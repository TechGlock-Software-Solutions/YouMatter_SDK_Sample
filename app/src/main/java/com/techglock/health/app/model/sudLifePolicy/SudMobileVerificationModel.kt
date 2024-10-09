package com.techglock.health.app.model.sudLifePolicy

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SudMobileVerificationModel {

    /*    @Parcelize
        data class SudMobileVerificationRequest(
            @field:SerializedName("MobileNumber")
            @Expose
            var mobileNumber: String? = "",
            ) : Parcelable*/

    data class SudMobileVerificationRequest(
        @SerializedName("MobileNumber")
        @Expose
        var mobileNumber: String? = ""
    )

    data class SudMobileVerificationResponse(
        @SerializedName("MobileNumber")
        @Expose
        val mobileNumber: String? = "",
        @SerializedName("record")
        @Expose
        val record: List<Record> = listOf(),
        @SerializedName("status")
        @Expose
        val status: String? = ""
    )

    data class Record(
        @SerializedName("Emp_Code")
        @Expose
        val empCode: String? = "",
        @SerializedName("Role")
        @Expose
        val role: String? = ""
    )

}
