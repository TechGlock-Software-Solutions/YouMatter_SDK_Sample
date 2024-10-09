package com.techglock.health.app.model.security

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DarwinBoxDataModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String
) : BaseRequest(Header(authTicket = "")) {

    data class JSONDataRequest(
        @SerializedName("encodedInput")
        @Expose
        var encodedInput: String = "",
    )

    data class DarwinBoxDataResponse(
        @SerializedName("DarwinBoxCheckTokenAPI")
        @Expose
        val darwinBoxCheckTokenAPI: DarwinBoxCheckTokenAPI = DarwinBoxCheckTokenAPI(),
        @SerializedName("DarwinBoxEmployeeAPI")
        @Expose
        val darwinBoxEmployeeAPI: DarwinBoxEmployeeAPI = DarwinBoxEmployeeAPI(),
        @SerializedName("OrgEmpID")
        @Expose
        val orgEmpID: String? = "",
        @SerializedName("OrgName")
        @Expose
        val orgName: String? = "",
        @SerializedName("IsAccountExists")
        @Expose
        val isAccountExists: String? = ""
    )

    data class DarwinBoxCheckTokenAPI(
        @SerializedName("status")
        @Expose
        val status: Int? = 0,
        @SerializedName("message")
        @Expose
        val message: String? = ""
    )

    data class DarwinBoxEmployeeAPI(
        @SerializedName("status")
        @Expose
        val status: Int? = 0,
        @SerializedName("message")
        @Expose
        val message: String? = "",
        @SerializedName("employee_data")
        @Expose
        val employeeData: List<EmployeeData> = listOf()
    )

    data class EmployeeData(
        @SerializedName("employee_id")
        @Expose
        val employeeId: String? = "",
        @SerializedName("first_name")
        @Expose
        val firstName: String? = "",
        @SerializedName("last_name")
        @Expose
        val lastName: String? = "",
        @SerializedName("department_name")
        @Expose
        val departmentName: String? = "",
        @SerializedName("designation_name")
        @Expose
        val designationName: String? = "",
        @SerializedName("date_of_joining")
        @Expose
        val dateOfJoining: String? = "",
        @SerializedName("date_of_exit")
        @Expose
        val dateOfExit: String? = "",
        @SerializedName("company_email_id")
        @Expose
        val companyEmailId: String? = "",
        @SerializedName("band")
        @Expose
        val band: String? = "",
        @SerializedName("date_of_activation")
        @Expose
        val dateOfActivation: String? = "",
        @SerializedName("confirmation_status")
        @Expose
        val confirmationStatus: String? = "",
        @SerializedName("primary_mobile_number")
        @Expose
        val primaryMobileNumber: String? = "",
        @SerializedName("date_of_birth")
        @Expose
        val dateOfBirth: String? = "",
        @SerializedName("gender")
        @Expose
        val gender: String? = "",
        @SerializedName("Uid")
        @Expose
        val uid: String? = "",
        @SerializedName("company_name")
        @Expose
        val companyName: String? = "",
        @SerializedName("office_mobile")
        @Expose
        val officeMobile: String? = "",
        @SerializedName("office_location_city")
        @Expose
        val officeLocationCity: String? = "",
        @SerializedName("office_location_pincode")
        @Expose
        val officeLocationPincode: String? = ""
    )

}