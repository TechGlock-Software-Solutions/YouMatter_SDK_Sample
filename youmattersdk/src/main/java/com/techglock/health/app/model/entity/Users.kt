package com.techglock.health.app.model.entity

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["accountId"])
data class Users(
    @SerializedName("AccountID")
    var accountId: Int = 0,
    @SerializedName("PersonID")
    var personId: Int = 0,
    @SerializedName("FirstName")
    var firstName: String = "",
    @SerializedName("LastName")
    var lastName: String = "",
    @SerializedName("DateOfBirth")
    var dateOfBirth: String? = "",
    @SerializedName("CreatedDate")
    var createdDate: String? = "",
    @SerializedName("Gender")
    var gender: String = "",
    @SerializedName("Age")
    var age: Int = 0,
    @SerializedName("EmailAddress")
    var emailAddress: String = "",
    @SerializedName("PhoneNumber")
    var phoneNumber: String = "",
    @SerializedName("PATH")
    var path: String = "",
    @SerializedName("Name")
    var name: String = "",
    @SerializedName("Context")
    var authToken: String = "",
    @SerializedName("LanguageCode")
    var languageCode: String = "",
    @SerializedName("PartnerCode")
    var partnerCode: String = "",
    @SerializedName("PartnerID")
    var partnerId: Int = 0,
    @SerializedName("MaritalStatus")
    var maritalStatus: String = "",
    @SerializedName("NumberOfKids")
    var numberOfCode: Int = 0,
    @SerializedName("ProfileImageID")
    var profileImageID: Int = 0,
    @SerializedName("AccountStatus")
    var accountStatus: String = "",
    @SerializedName("AccountType")
    var accountType: String = "",
    @SerializedName("ClusterAssociationNo")
    var clusterAssociationNo: String = "",
    @SerializedName("ClusterID")
    var clusterId: String = "",
    @SerializedName("CountryName")
    var countryName: String = "",
    @SerializedName("DialingCode")
    var dialingCode: String = "",
    @SerializedName("IsActive")
    var isActive: Boolean = false,
    @SerializedName("IsAuthenticated")
    var isAuthenticated: Boolean = false,
    @SerializedName("PROFILE_IMG_PATH")
    var profileImgPath: String = "",
    @SerializedName("RELATIVE_ID")
    var relativeId: String = "",
    @SerializedName("OrgEmpID")
    var orgEmpID: String = "",
    @SerializedName("OrgName")
    var orgName: String = ""
)