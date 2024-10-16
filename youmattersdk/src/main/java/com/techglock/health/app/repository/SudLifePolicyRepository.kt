package com.techglock.health.app.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.techglock.health.app.model.BaseResponse
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
import com.techglock.health.app.remote.SudLifePolicyDatasource
import com.techglock.health.app.repository.utils.NetworkBoundResource
import com.techglock.health.app.repository.utils.NetworkDataBoundResource
import com.techglock.health.app.repository.utils.Resource
import javax.inject.Inject

interface SudLifePolicyRepository {

    suspend fun policyProducts(data: PolicyProductsModel): LiveData<Resource<PolicyProductsModel.PolicyProductsResponse>>

    suspend fun sudPolicyByMobileNumber(data: SudPolicyByMobileNumberModel): LiveData<Resource<SudPolicyByMobileNumberModel.SudPolicyByMobileNumberResponse>>

    suspend fun sudPolicyDetailsByPolicyNumber(data: SudPolicyDetailsByPolicyNumberModel): LiveData<Resource<SudPolicyDetailsByPolicyNumberModel.SudPolicyByMobileNumberResponse>>

    suspend fun sudKYP(data: SudKYPModel): LiveData<Resource<SudKYPModel.SudKYPResponse>>

    suspend fun sudGroupCoiApi(data: SudGroupCOIModel): LiveData<Resource<SudGroupCOIModel.SudGroupCOIResponse>>

    suspend fun sudKypPdf(data: SudKypPdfModel): LiveData<Resource<SudKypPdfModel.SudKypPdfResponse>>

    suspend fun sudGetFundDetail(data: SudFundDetailsModel): LiveData<Resource<SudFundDetailsModel.SudFundDetailsResponse>>

    suspend fun sudGetReceiptDetails(data: SudReceiptDetailsModel): LiveData<Resource<SudReceiptDetailsModel.SudReceiptDetailsResponse>>

    suspend fun sudGetPMJJBYCoiBase(data: SudPMJJBYCoiBaseModel): LiveData<Resource<SudPMJJBYCoiBaseModel.SudPMJJBYCoiBaseResponse>>

    suspend fun sudGetKypTemplate(data: SudKypTemplateModel): LiveData<Resource<SudKypTemplateModel.SudKypTemplateResponse>>

    suspend fun sudGetPayPremium(data: SudPayPremiumModel): LiveData<Resource<SudPayPremiumModel.SudPayPremiumResponse>>

    suspend fun sudGetRenewalPremium(data: SudRenewalPremiumModel): LiveData<Resource<SudRenewalPremiumModel.SudRenewalPremiumResponse>>

    suspend fun sudGetRenewalPremiumReceipt(data: SudRenewalPremiumReceiptModel): LiveData<Resource<SudRenewalPremiumReceiptModel.SudRenewalPremiumReceiptResponse>>

}

class SudLifePolicyRepositoryImpl @Inject constructor(
    private val datasource: SudLifePolicyDatasource,
    val context: Context
) : SudLifePolicyRepository {

    override suspend fun policyProducts(data: PolicyProductsModel): LiveData<Resource<PolicyProductsModel.PolicyProductsResponse>> {

        return object :
            NetworkBoundResource<PolicyProductsModel.PolicyProductsResponse, BaseResponse<PolicyProductsModel.PolicyProductsResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): PolicyProductsModel.PolicyProductsResponse {
                return PolicyProductsModel.PolicyProductsResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<PolicyProductsModel.PolicyProductsResponse> {
                return datasource.policyProductsResponse(data)
            }

            override fun processResponse(response: BaseResponse<PolicyProductsModel.PolicyProductsResponse>): PolicyProductsModel.PolicyProductsResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: PolicyProductsModel.PolicyProductsResponse) {

            }

            override fun shouldFetch(data: PolicyProductsModel.PolicyProductsResponse?): Boolean =
                true

        }.build().asLiveData()

    }

    override suspend fun sudPolicyByMobileNumber(data: SudPolicyByMobileNumberModel): LiveData<Resource<SudPolicyByMobileNumberModel.SudPolicyByMobileNumberResponse>> {

        return object :
            NetworkDataBoundResource<SudPolicyByMobileNumberModel.SudPolicyByMobileNumberResponse, SudPolicyByMobileNumberModel.SudPolicyByMobileNumberResponse>(
                context
            ) {

            override fun processResponse(response: SudPolicyByMobileNumberModel.SudPolicyByMobileNumberResponse): SudPolicyByMobileNumberModel.SudPolicyByMobileNumberResponse {
                return response
            }

            override suspend fun createCallAsync(): SudPolicyByMobileNumberModel.SudPolicyByMobileNumberResponse {
                return datasource.sudPolicyByMobileNumberResponse(data)
            }

        }.build().asLiveData()

    }

    override suspend fun sudPolicyDetailsByPolicyNumber(data: SudPolicyDetailsByPolicyNumberModel): LiveData<Resource<SudPolicyDetailsByPolicyNumberModel.SudPolicyByMobileNumberResponse>> {

        return object :
            NetworkDataBoundResource<SudPolicyDetailsByPolicyNumberModel.SudPolicyByMobileNumberResponse, SudPolicyDetailsByPolicyNumberModel.SudPolicyByMobileNumberResponse>(
                context
            ) {

            override fun processResponse(response: SudPolicyDetailsByPolicyNumberModel.SudPolicyByMobileNumberResponse): SudPolicyDetailsByPolicyNumberModel.SudPolicyByMobileNumberResponse {
                return response
            }

            override suspend fun createCallAsync(): SudPolicyDetailsByPolicyNumberModel.SudPolicyByMobileNumberResponse {
                return datasource.sudPolicyDetailsByPolicyNumber(data)
            }

        }.build().asLiveData()

    }

    override suspend fun sudKYP(data: SudKYPModel): LiveData<Resource<SudKYPModel.SudKYPResponse>> {

        return object :
            NetworkDataBoundResource<SudKYPModel.SudKYPResponse, SudKYPModel.SudKYPResponse>(context) {

            override fun processResponse(response: SudKYPModel.SudKYPResponse): SudKYPModel.SudKYPResponse {
                return response
            }

            override suspend fun createCallAsync(): SudKYPModel.SudKYPResponse {
                return datasource.sudKYP(data)
            }

        }.build().asLiveData()

    }

    override suspend fun sudGroupCoiApi(data: SudGroupCOIModel): LiveData<Resource<SudGroupCOIModel.SudGroupCOIResponse>> {

        return object :
            NetworkBoundResource<SudGroupCOIModel.SudGroupCOIResponse, BaseResponse<SudGroupCOIModel.SudGroupCOIResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): SudGroupCOIModel.SudGroupCOIResponse {
                return SudGroupCOIModel.SudGroupCOIResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<SudGroupCOIModel.SudGroupCOIResponse> {
                return datasource.sudGroupCoiApi(data)
            }

            override fun processResponse(response: BaseResponse<SudGroupCOIModel.SudGroupCOIResponse>): SudGroupCOIModel.SudGroupCOIResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: SudGroupCOIModel.SudGroupCOIResponse) {

            }

            override fun shouldFetch(data: SudGroupCOIModel.SudGroupCOIResponse?): Boolean = true

        }.build().asLiveData()

    }

    override suspend fun sudGetFundDetail(data: SudFundDetailsModel): LiveData<Resource<SudFundDetailsModel.SudFundDetailsResponse>> {

        return object :
            NetworkDataBoundResource<SudFundDetailsModel.SudFundDetailsResponse, SudFundDetailsModel.SudFundDetailsResponse>(
                context
            ) {

            override fun processResponse(response: SudFundDetailsModel.SudFundDetailsResponse): SudFundDetailsModel.SudFundDetailsResponse {
                return response
            }

            override suspend fun createCallAsync(): SudFundDetailsModel.SudFundDetailsResponse {
                return datasource.sudGetFundDetail(data)
            }

        }.build().asLiveData()

    }

    override suspend fun sudGetReceiptDetails(data: SudReceiptDetailsModel): LiveData<Resource<SudReceiptDetailsModel.SudReceiptDetailsResponse>> {

        return object :
            NetworkDataBoundResource<SudReceiptDetailsModel.SudReceiptDetailsResponse, SudReceiptDetailsModel.SudReceiptDetailsResponse>(
                context
            ) {

            override fun processResponse(response: SudReceiptDetailsModel.SudReceiptDetailsResponse): SudReceiptDetailsModel.SudReceiptDetailsResponse {
                return response
            }

            override suspend fun createCallAsync(): SudReceiptDetailsModel.SudReceiptDetailsResponse {
                return datasource.sudGetReceiptDetails(data)
            }


        }.build().asLiveData()

    }

    override suspend fun sudGetPMJJBYCoiBase(data: SudPMJJBYCoiBaseModel): LiveData<Resource<SudPMJJBYCoiBaseModel.SudPMJJBYCoiBaseResponse>> {

        return object :
            NetworkDataBoundResource<SudPMJJBYCoiBaseModel.SudPMJJBYCoiBaseResponse, SudPMJJBYCoiBaseModel.SudPMJJBYCoiBaseResponse>(
                context
            ) {

            override fun processResponse(response: SudPMJJBYCoiBaseModel.SudPMJJBYCoiBaseResponse): SudPMJJBYCoiBaseModel.SudPMJJBYCoiBaseResponse {
                return response
            }

            override suspend fun createCallAsync(): SudPMJJBYCoiBaseModel.SudPMJJBYCoiBaseResponse {
                return datasource.sudGetPMJJBYCoiBase(data)
            }

        }.build().asLiveData()

    }

    override suspend fun sudGetKypTemplate(data: SudKypTemplateModel): LiveData<Resource<SudKypTemplateModel.SudKypTemplateResponse>> {

        return object :
            NetworkDataBoundResource<SudKypTemplateModel.SudKypTemplateResponse, SudKypTemplateModel.SudKypTemplateResponse>(
                context
            ) {

            override fun processResponse(response: SudKypTemplateModel.SudKypTemplateResponse): SudKypTemplateModel.SudKypTemplateResponse {
                return response
            }

            override suspend fun createCallAsync(): SudKypTemplateModel.SudKypTemplateResponse {
                return datasource.sudGetKypTemplate(data)
            }

        }.build().asLiveData()

    }

    override suspend fun sudKypPdf(data: SudKypPdfModel): LiveData<Resource<SudKypPdfModel.SudKypPdfResponse>> {

        return object :
            NetworkDataBoundResource<SudKypPdfModel.SudKypPdfResponse, SudKypPdfModel.SudKypPdfResponse>(
                context
            ) {

            override fun processResponse(response: SudKypPdfModel.SudKypPdfResponse): SudKypPdfModel.SudKypPdfResponse {
                return response
            }

            override suspend fun createCallAsync(): SudKypPdfModel.SudKypPdfResponse {
                return datasource.sudKypPdf(data)
            }

        }.build().asLiveData()

    }

    override suspend fun sudGetPayPremium(data: SudPayPremiumModel): LiveData<Resource<SudPayPremiumModel.SudPayPremiumResponse>> {

        return object :
            NetworkDataBoundResource<SudPayPremiumModel.SudPayPremiumResponse, SudPayPremiumModel.SudPayPremiumResponse>(
                context
            ) {

            override fun processResponse(response: SudPayPremiumModel.SudPayPremiumResponse): SudPayPremiumModel.SudPayPremiumResponse {
                return response
            }

            override suspend fun createCallAsync(): SudPayPremiumModel.SudPayPremiumResponse {
                return datasource.sudGetPayPremium(data)
            }

        }.build().asLiveData()

    }

    override suspend fun sudGetRenewalPremium(data: SudRenewalPremiumModel): LiveData<Resource<SudRenewalPremiumModel.SudRenewalPremiumResponse>> {

        return object :
            NetworkDataBoundResource<SudRenewalPremiumModel.SudRenewalPremiumResponse, SudRenewalPremiumModel.SudRenewalPremiumResponse>(
                context
            ) {

            override fun processResponse(response: SudRenewalPremiumModel.SudRenewalPremiumResponse): SudRenewalPremiumModel.SudRenewalPremiumResponse {
                return response
            }

            override suspend fun createCallAsync(): SudRenewalPremiumModel.SudRenewalPremiumResponse {
                return datasource.sudGetRenewalPremium(data)
            }


        }.build().asLiveData()

    }

    override suspend fun sudGetRenewalPremiumReceipt(data: SudRenewalPremiumReceiptModel): LiveData<Resource<SudRenewalPremiumReceiptModel.SudRenewalPremiumReceiptResponse>> {

        return object :
            NetworkDataBoundResource<SudRenewalPremiumReceiptModel.SudRenewalPremiumReceiptResponse, SudRenewalPremiumReceiptModel.SudRenewalPremiumReceiptResponse>(
                context
            ) {

            override fun processResponse(response: SudRenewalPremiumReceiptModel.SudRenewalPremiumReceiptResponse): SudRenewalPremiumReceiptModel.SudRenewalPremiumReceiptResponse {
                return response
            }

            override suspend fun createCallAsync(): SudRenewalPremiumReceiptModel.SudRenewalPremiumReceiptResponse {
                return datasource.sudGetRenewalPremiumReceipt(data)
            }

        }.build().asLiveData()

    }

}