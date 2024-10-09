package com.techglock.health.app.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.local.dao.VivantUserDao
import com.techglock.health.app.model.ApiResponse
import com.techglock.health.app.model.BaseResponse
import com.techglock.health.app.model.entity.Users
import com.techglock.health.app.model.security.ChangePasswordModel
import com.techglock.health.app.model.security.DarwinBoxDataModel
import com.techglock.health.app.model.security.EmailExistsModel
import com.techglock.health.app.model.security.GenerateOtpModel
import com.techglock.health.app.model.security.LoginNameExistsModel
import com.techglock.health.app.model.security.PhoneExistsModel
import com.techglock.health.app.model.security.TermsConditionsModel
import com.techglock.health.app.remote.SecurityDatasource
import com.techglock.health.app.repository.utils.NetworkBoundResource
import com.techglock.health.app.repository.utils.Resource

interface UserRepository {

    suspend fun fetchSsoResponse(encryptedData: String): LiveData<Resource<Users>>

    suspend fun getLoginResponse(
        forceRefresh: Boolean = false,
        encryptedData: String,
        isOtpAuthenticated: Boolean
    ): LiveData<Resource<Users>>

    suspend fun fetchRegistrationResponse(
        encryptedData: String,
        isOtpAuthenticated: Boolean
    ): LiveData<Resource<Users>>

    suspend fun isEmailExist(
        forceRefresh: Boolean = false,
        data: EmailExistsModel
    ): LiveData<Resource<EmailExistsModel.IsExistResponse>>

    suspend fun isPhoneExist(
        forceRefresh: Boolean = false,
        data: PhoneExistsModel
    ): LiveData<Resource<PhoneExistsModel.IsExistResponse>>

    //    suspend fun fetchFacebookLogin(forceRefresh: Boolean = false, data: EmailExistsModel) :LiveData<Resource<EmailOrPhoneExistResponse>>
//    suspend fun fetchGoogleLogin(forceRefresh: Boolean = false, data: EmailExistsModel) :LiveData<Resource<EmailOrPhoneExistResponse>>
    suspend fun getTermsConditionsResponse(
        forceRefresh: Boolean = false,
        encryptedData: TermsConditionsModel
    ): LiveData<Resource<TermsConditionsModel.TermsConditionsResponse>>

    suspend fun getGenerateOTPResponse(
        forceRefresh: Boolean = false,
        data: GenerateOtpModel
    ): LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>>

    suspend fun getValidateOTPResponse(
        forceRefresh: Boolean = false,
        data: GenerateOtpModel
    ): LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>>

    suspend fun darwinBoxDataResponse(
        forceRefresh: Boolean = false,
        data: DarwinBoxDataModel
    ): LiveData<Resource<DarwinBoxDataModel.DarwinBoxDataResponse>>

    suspend fun updatePassword(
        forceRefresh: Boolean = false,
        data: ChangePasswordModel
    ): LiveData<Resource<ChangePasswordModel.ChangePasswordResponse>>

    suspend fun isLoginNameExist(
        forceRefresh: Boolean = false,
        data: LoginNameExistsModel
    ): LiveData<Resource<LoginNameExistsModel.IsExistResponse>>

    suspend fun saveUserInfo(data: Users)

    //suspend fun updateOtpAuthentication(isOtpAuthenticated: Boolean)

}

class UserRepositoryImpl(
    private val datasource: SecurityDatasource,
    private val vudao: VivantUserDao,
    val context: Context
) : UserRepository {
    override suspend fun fetchSsoResponse(encryptedData: String): LiveData<Resource<Users>> {
        Utilities.printLog("Inside => fetchSsoResponse UserRepository")
        return object : NetworkBoundResource<Users, ApiResponse<Users>>(context) {

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): Users {
                val db = vudao.getUser()
                //Utilities.printLog("SsoProcessResponse=> $db")
                return db
            }

            override suspend fun createCallAsync(): ApiResponse<Users> {
                return datasource.fetchSsoResponse(encryptedData)
            }

            override fun processResponse(response: ApiResponse<Users>): Users {
                return response.data
/*                return if (response.statusCode == "400") {
                    response.data
                } else {
                    response.data
                }*/
            }

            override suspend fun saveCallResults(items: Users) {
                vudao.deleteAllRecords()
                items.name = ""
                vudao.insert(items)
            }

            override fun shouldFetch(data: Users?): Boolean = true

        }.build().asLiveData()
    }

    override suspend fun getLoginResponse(
        forceRefresh: Boolean,
        encryptedData: String,
        isOtpAuthenticated: Boolean
    ): LiveData<Resource<Users>> {

        return object : NetworkBoundResource<Users, ApiResponse<Users>>(context) {

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): Users {
                val db = vudao.getUser()
//                Utilities.printLog("processResponse=> $db")
                return db
            }

            override suspend fun createCallAsync(): ApiResponse<Users> {
                return datasource.getLoginResponse(encryptedData)
            }

            override fun processResponse(response: ApiResponse<Users>): Users {
                if (response.statusCode.equals("400")) {
                    return response.data
                } else {
                    return response.data
                }
            }

            override suspend fun saveCallResults(items: Users) {
                vudao.deleteAllRecords()
                items.name = ""
                //items.isOtpAuthenticated = isOtpAuthenticated
                vudao.insert(items)
            }

            override fun shouldFetch(data: Users?): Boolean = true

        }.build().asLiveData()

    }

    override suspend fun fetchRegistrationResponse(
        encryptedData: String,
        isOtpAuthenticated: Boolean
    ): LiveData<Resource<Users>> {
        Utilities.printLog("Inside => fetchRegistrationResponse UserRepository")

        return object : NetworkBoundResource<Users, ApiResponse<Users>>(context) {

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): Users {
                val db = vudao.getUser()
//                Utilities.printLog("processResponse=> $db")
                return db
            }

            override suspend fun createCallAsync(): ApiResponse<Users> {
                return datasource.fetchRegistrationResponse(encryptedData)
            }

            override fun processResponse(response: ApiResponse<Users>): Users {
                if (response.statusCode.equals("400")) {
                    return response.data
                } else {
                    return response.data
                }
            }

            override suspend fun saveCallResults(items: Users) {
                vudao.deleteAllRecords()
                //items.isOtpAuthenticated = isOtpAuthenticated
                vudao.insert(items)
            }

            override fun shouldFetch(data: Users?): Boolean = true

        }.build().asLiveData()
    }

    override suspend fun isPhoneExist(
        forceRefresh: Boolean,
        data: PhoneExistsModel
    ): LiveData<Resource<PhoneExistsModel.IsExistResponse>> {

        return object :
            NetworkBoundResource<PhoneExistsModel.IsExistResponse, BaseResponse<PhoneExistsModel.IsExistResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override fun processResponse(response: BaseResponse<PhoneExistsModel.IsExistResponse>): PhoneExistsModel.IsExistResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: PhoneExistsModel.IsExistResponse) {

            }

            override fun shouldFetch(data: PhoneExistsModel.IsExistResponse?): Boolean {
                return true
            }

            override suspend fun loadFromDb(): PhoneExistsModel.IsExistResponse {
                return PhoneExistsModel.IsExistResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<PhoneExistsModel.IsExistResponse> {
                return datasource.fetchPhoneExistResponse(data)
            }

        }.build().asLiveData()
    }

    override suspend fun isEmailExist(
        forceRefresh: Boolean,
        data: EmailExistsModel
    ): LiveData<Resource<EmailExistsModel.IsExistResponse>> {

        return object :
            NetworkBoundResource<EmailExistsModel.IsExistResponse, BaseResponse<EmailExistsModel.IsExistResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override fun processResponse(response: BaseResponse<EmailExistsModel.IsExistResponse>): EmailExistsModel.IsExistResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: EmailExistsModel.IsExistResponse) {

            }

            override fun shouldFetch(data: EmailExistsModel.IsExistResponse?): Boolean {
                return true
            }

            override suspend fun loadFromDb(): EmailExistsModel.IsExistResponse {
                return EmailExistsModel.IsExistResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<EmailExistsModel.IsExistResponse> {
                return datasource.fetchEmailExistResponse(data)
            }


        }.build().asLiveData()
    }

    override suspend fun isLoginNameExist(
        forceRefresh: Boolean,
        data: LoginNameExistsModel
    ): LiveData<Resource<LoginNameExistsModel.IsExistResponse>> {

        return object :
            NetworkBoundResource<LoginNameExistsModel.IsExistResponse, BaseResponse<LoginNameExistsModel.IsExistResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override fun processResponse(response: BaseResponse<LoginNameExistsModel.IsExistResponse>): LoginNameExistsModel.IsExistResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: LoginNameExistsModel.IsExistResponse) {

            }

            override fun shouldFetch(data: LoginNameExistsModel.IsExistResponse?): Boolean {
                return true
            }

            override suspend fun loadFromDb(): LoginNameExistsModel.IsExistResponse {
                return LoginNameExistsModel.IsExistResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<LoginNameExistsModel.IsExistResponse> {
                return datasource.fetchLoginNameExistResponse(data)
            }


        }.build().asLiveData()
    }

    override suspend fun saveUserInfo(data: Users) {
        vudao.deleteAllRecords()
        vudao.insert(data)
    }

    /*    override suspend fun updateOtpAuthentication(isOtpAuthenticated: Boolean) {
            vudao.updateOtpAuthentication(isOtpAuthenticated)
        }*/

    override suspend fun updatePassword(
        forceRefresh: Boolean,
        data: ChangePasswordModel
    ): LiveData<Resource<ChangePasswordModel.ChangePasswordResponse>> {

        return object :
            NetworkBoundResource<ChangePasswordModel.ChangePasswordResponse, BaseResponse<ChangePasswordModel.ChangePasswordResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override fun processResponse(response: BaseResponse<ChangePasswordModel.ChangePasswordResponse>): ChangePasswordModel.ChangePasswordResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: ChangePasswordModel.ChangePasswordResponse) {}

            override fun shouldFetch(data: ChangePasswordModel.ChangePasswordResponse?): Boolean {
                return true
            }

            override suspend fun loadFromDb(): ChangePasswordModel.ChangePasswordResponse {
                return ChangePasswordModel.ChangePasswordResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<ChangePasswordModel.ChangePasswordResponse> {
                return datasource.updatePasswordResponse(data)
            }

        }.build().asLiveData()
    }

    override suspend fun getGenerateOTPResponse(
        forceRefresh: Boolean,
        data: GenerateOtpModel
    ): LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> {

        return object :
            NetworkBoundResource<GenerateOtpModel.GenerateOTPResponse, BaseResponse<GenerateOtpModel.GenerateOTPResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override fun processResponse(response: BaseResponse<GenerateOtpModel.GenerateOTPResponse>): GenerateOtpModel.GenerateOTPResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: GenerateOtpModel.GenerateOTPResponse) {
            }

            override fun shouldFetch(data: GenerateOtpModel.GenerateOTPResponse?): Boolean {
                return true
            }

            override suspend fun loadFromDb(): GenerateOtpModel.GenerateOTPResponse {
                return GenerateOtpModel.GenerateOTPResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<GenerateOtpModel.GenerateOTPResponse> {
                return datasource.getGenerateOTPResponse(data)
            }

        }.build().asLiveData()
    }

    override suspend fun getValidateOTPResponse(
        forceRefresh: Boolean,
        data: GenerateOtpModel
    ): LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> {

        return object :
            NetworkBoundResource<GenerateOtpModel.GenerateOTPResponse, BaseResponse<GenerateOtpModel.GenerateOTPResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override fun processResponse(response: BaseResponse<GenerateOtpModel.GenerateOTPResponse>): GenerateOtpModel.GenerateOTPResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: GenerateOtpModel.GenerateOTPResponse) {
            }

            override fun shouldFetch(data: GenerateOtpModel.GenerateOTPResponse?): Boolean {
                return true
            }

            override suspend fun loadFromDb(): GenerateOtpModel.GenerateOTPResponse {
                return GenerateOtpModel.GenerateOTPResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<GenerateOtpModel.GenerateOTPResponse> {
                return datasource.getValidateOTPResponse(data)
            }

        }.build().asLiveData()
    }

    override suspend fun darwinBoxDataResponse(
        forceRefresh: Boolean,
        data: DarwinBoxDataModel
    ): LiveData<Resource<DarwinBoxDataModel.DarwinBoxDataResponse>> {

        return object :
            NetworkBoundResource<DarwinBoxDataModel.DarwinBoxDataResponse, BaseResponse<DarwinBoxDataModel.DarwinBoxDataResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override fun processResponse(response: BaseResponse<DarwinBoxDataModel.DarwinBoxDataResponse>): DarwinBoxDataModel.DarwinBoxDataResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: DarwinBoxDataModel.DarwinBoxDataResponse) {

            }

            override fun shouldFetch(data: DarwinBoxDataModel.DarwinBoxDataResponse?): Boolean {
                return true
            }

            override suspend fun loadFromDb(): DarwinBoxDataModel.DarwinBoxDataResponse {
                return DarwinBoxDataModel.DarwinBoxDataResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<DarwinBoxDataModel.DarwinBoxDataResponse> {
                return datasource.darwinBoxDataResponse(data)
            }

        }.build().asLiveData()

    }

    override suspend fun getTermsConditionsResponse(
        forceRefresh: Boolean,
        encryptedData: TermsConditionsModel
    ): LiveData<Resource<TermsConditionsModel.TermsConditionsResponse>> {

        return object :
            NetworkBoundResource<TermsConditionsModel.TermsConditionsResponse, BaseResponse<TermsConditionsModel.TermsConditionsResponse>>(
                context
            ) {

            override fun processResponse(response: BaseResponse<TermsConditionsModel.TermsConditionsResponse>): TermsConditionsModel.TermsConditionsResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: TermsConditionsModel.TermsConditionsResponse) {

            }

            override fun shouldFetch(data: TermsConditionsModel.TermsConditionsResponse?): Boolean {
                return true
            }

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): TermsConditionsModel.TermsConditionsResponse {
                return TermsConditionsModel.TermsConditionsResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<TermsConditionsModel.TermsConditionsResponse> {
                return datasource.getTermsConditionsResponce(encryptedData)
            }


        }.build().asLiveData()
    }

}