package com.techglock.health.app.remote

import com.techglock.health.app.di.DIModule
import com.techglock.health.app.model.security.*
import javax.inject.Inject
import javax.inject.Named

/**
 * Implementation of [UserService] interface
 */
class SecurityDatasource @Inject constructor(
    @Named(DIModule.DEFAULT) private val defaultUserService: ApiService,
    @Named(DIModule.ENCRYPTED) private val encryptedUserService: ApiService
) {

    suspend fun fetchSsoResponse(data: String) = defaultUserService.ssoProxyAPI(data)

    suspend fun getLoginResponse(data: String) = defaultUserService.loginDocumentProxyAPI(data)

    suspend fun fetchRegistrationResponse(data: String) =
        defaultUserService.registerDocumentProxyAPI(data)

    suspend fun fetchEmailExistResponse(data: EmailExistsModel) =
        encryptedUserService.checkEmailExistsAPI(data)

    suspend fun fetchPhoneExistResponse(data: PhoneExistsModel) =
        encryptedUserService.checkPhoneExistsAPI(data)

    suspend fun getTermsConditionsResponce(data: TermsConditionsModel) =
        encryptedUserService.getTermsAndConditionsForPartnerAPI(data)

    suspend fun getGenerateOTPResponse(data: GenerateOtpModel) =
        encryptedUserService.generateOTPforUserAPI(data)

    suspend fun getValidateOTPResponse(data: GenerateOtpModel) =
        encryptedUserService.validateOTPforUserAPI(data)

    suspend fun updatePasswordResponse(data: ChangePasswordModel) =
        encryptedUserService.updatePasswordAPI(data)

    suspend fun darwinBoxDataResponse(data: DarwinBoxDataModel) =
        encryptedUserService.darwinBoxDataAPI(data)

    suspend fun fetchLoginNameExistResponse(data: LoginNameExistsModel) =
        encryptedUserService.checkLoginNameExistsAPI(data)

}