package com.techglock.health.app.remote

import com.techglock.health.app.di.DIModule.DEFAULT_NEW
import com.techglock.health.app.di.DIModule.ENCRYPTED
import com.techglock.health.app.di.DIModule.SUD
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
import javax.inject.Inject
import javax.inject.Named

class SudLifePolicyDatasource @Inject constructor(
    @Named(DEFAULT_NEW) private val defaultService: ApiService,
    @Named(ENCRYPTED) private val encryptedUserService: ApiService,
    @Named(SUD) private val sudLifePolicyService: ApiService
) {

    suspend fun policyProductsResponse(data: PolicyProductsModel) =
        encryptedUserService.policyProductsApi(data)

    suspend fun sudPolicyByMobileNumberResponse(data: SudPolicyByMobileNumberModel) =
        defaultService.getSudPolicyByMobileNumber(data)

    suspend fun sudPolicyDetailsByPolicyNumber(data: SudPolicyDetailsByPolicyNumberModel) =
        defaultService.getPolicyDetailsByPolicyNumber(data)

    suspend fun sudGetFundDetail(data: SudFundDetailsModel) = defaultService.getSudFundDetail(data)

    suspend fun sudGetReceiptDetails(data: SudReceiptDetailsModel) =
        defaultService.getReceiptDetails(data)

    suspend fun sudGetPMJJBYCoiBase(data: SudPMJJBYCoiBaseModel) =
        defaultService.getSudPMJJBYCoiBase(data)

    suspend fun sudGetKypTemplate(data: SudKypTemplateModel) = defaultService.getKypTemplate(data)

    suspend fun sudKypPdf(data: SudKypPdfModel) = defaultService.getSudKypPdf(data)

    suspend fun sudGetPayPremium(data: SudPayPremiumModel) = defaultService.getPayPremium(data)

    suspend fun sudKYP(data: SudKYPModel) = defaultService.getSudKYP(data)

    suspend fun sudGroupCoiApi(data: SudGroupCOIModel) = encryptedUserService.groupCoiApi(data)

    suspend fun sudGetRenewalPremium(data: SudRenewalPremiumModel) =
        defaultService.getRenewalPremium(data)

    suspend fun sudGetRenewalPremiumReceipt(data: SudRenewalPremiumReceiptModel) =
        defaultService.getRenewalPremiumReceipt(data)

}