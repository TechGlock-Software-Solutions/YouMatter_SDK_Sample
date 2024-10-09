package com.techglock.health.app.home.domain

//import androidx.lifecycle.Transformations
import androidx.lifecycle.LiveData
import com.techglock.health.app.model.security.GenerateOtpModel
import com.techglock.health.app.model.sudLifePolicy.PolicyProductsModel
import com.techglock.health.app.model.sudLifePolicy.SudFundDetailsModel
import com.techglock.health.app.model.sudLifePolicy.SudGroupCOIModel
import com.techglock.health.app.model.sudLifePolicy.SudKYPModel
import com.techglock.health.app.model.sudLifePolicy.SudKypPdfModel
import com.techglock.health.app.model.sudLifePolicy.SudKypTemplateModel
import com.techglock.health.app.model.sudLifePolicy.SudPMJJBYCoiBaseModel
import com.techglock.health.app.model.sudLifePolicy.SudPayPremiumModel
import com.techglock.health.app.model.sudLifePolicy.SudPolicyByMobileNumberModel
import com.techglock.health.app.model.sudLifePolicy.SudPolicyDetailsByPolicyNumberModel
import com.techglock.health.app.model.sudLifePolicy.SudReceiptDetailsModel
import com.techglock.health.app.model.sudLifePolicy.SudRenewalPremiumModel
import com.techglock.health.app.model.sudLifePolicy.SudRenewalPremiumReceiptModel
import com.techglock.health.app.repository.SudLifePolicyRepository
import com.techglock.health.app.repository.UserRepository
import com.techglock.health.app.repository.utils.Resource
import javax.inject.Inject


class SudLifePolicyManagementUseCase @Inject constructor(
    private val sudLifePolicyRepository: SudLifePolicyRepository,
    private val userRepository: UserRepository
) {

    suspend fun invokePolicyProducts(data: PolicyProductsModel): LiveData<Resource<PolicyProductsModel.PolicyProductsResponse>> {

        return sudLifePolicyRepository.policyProducts(data)
    }

    suspend fun invokeSudPolicyByMobileNumber(data: SudPolicyByMobileNumberModel): LiveData<Resource<SudPolicyByMobileNumberModel.SudPolicyByMobileNumberResponse>> {

        return sudLifePolicyRepository.sudPolicyByMobileNumber(data)
    }

    suspend fun invokeSudPolicyDetailsByPolicyNumber(data: SudPolicyDetailsByPolicyNumberModel): LiveData<Resource<SudPolicyDetailsByPolicyNumberModel.SudPolicyByMobileNumberResponse>> {

        return sudLifePolicyRepository.sudPolicyDetailsByPolicyNumber(data)
    }

    suspend fun invokeSudKYP(data: SudKYPModel): LiveData<Resource<SudKYPModel.SudKYPResponse>> {

        return sudLifePolicyRepository.sudKYP(data)
    }

    suspend fun invokeSudGroupCoi(data: SudGroupCOIModel): LiveData<Resource<SudGroupCOIModel.SudGroupCOIResponse>> {

        return sudLifePolicyRepository.sudGroupCoiApi(data)
    }

    suspend fun invokeSudGetFundDetail(data: SudFundDetailsModel): LiveData<Resource<SudFundDetailsModel.SudFundDetailsResponse>> {

        return sudLifePolicyRepository.sudGetFundDetail(data)
    }

    suspend fun invokeSudGetReceiptDetails(data: SudReceiptDetailsModel): LiveData<Resource<SudReceiptDetailsModel.SudReceiptDetailsResponse>> {

        return sudLifePolicyRepository.sudGetReceiptDetails(data)
    }

    suspend fun invokeSudGetPMJJBYCoiBase(data: SudPMJJBYCoiBaseModel): LiveData<Resource<SudPMJJBYCoiBaseModel.SudPMJJBYCoiBaseResponse>> {

        return sudLifePolicyRepository.sudGetPMJJBYCoiBase(data)
    }

    suspend fun invokeSudGetKypTemplate(data: SudKypTemplateModel): LiveData<Resource<SudKypTemplateModel.SudKypTemplateResponse>> {

        return sudLifePolicyRepository.sudGetKypTemplate(data)
    }

    suspend fun invokeSudKypPdf(data: SudKypPdfModel): LiveData<Resource<SudKypPdfModel.SudKypPdfResponse>> {

        return sudLifePolicyRepository.sudKypPdf(data)
    }

    suspend fun invokeSudGetPayPremium(data: SudPayPremiumModel): LiveData<Resource<SudPayPremiumModel.SudPayPremiumResponse>> {

        return sudLifePolicyRepository.sudGetPayPremium(data)
    }

    suspend fun invokeSudGetRenewalPremium(data: SudRenewalPremiumModel): LiveData<Resource<SudRenewalPremiumModel.SudRenewalPremiumResponse>> {

        return sudLifePolicyRepository.sudGetRenewalPremium(data)
    }

    suspend fun invokeSudGetRenewalPremiumReceipt(data: SudRenewalPremiumReceiptModel): LiveData<Resource<SudRenewalPremiumReceiptModel.SudRenewalPremiumReceiptResponse>> {

        return sudLifePolicyRepository.sudGetRenewalPremiumReceipt(data)
    }

    suspend fun invokeGenerateOTP(
        isForceRefresh: Boolean,
        data: GenerateOtpModel
    ): LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> {
        return userRepository.getGenerateOTPResponse(isForceRefresh, data)
    }

    suspend fun invokeValidateOTP(
        isForceRefresh: Boolean,
        data: GenerateOtpModel
    ): LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> {
        return userRepository.getValidateOTPResponse(isForceRefresh, data)
    }

    /*    suspend fun invokeUpdateOtpAuthentication(isOtpAuthenticated:Boolean) {
            userRepository.updateOtpAuthentication(isOtpAuthenticated)
        }*/

}