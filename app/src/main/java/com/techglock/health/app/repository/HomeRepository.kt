package com.techglock.health.app.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.local.dao.DataSyncMasterDao
import com.techglock.health.app.local.dao.HRADao
import com.techglock.health.app.local.dao.MedicationDao
import com.techglock.health.app.local.dao.StoreRecordsDao
import com.techglock.health.app.local.dao.TrackParameterDao
import com.techglock.health.app.local.dao.VivantUserDao
import com.techglock.health.app.model.BaseResponse
import com.techglock.health.app.model.entity.AppVersion
import com.techglock.health.app.model.entity.DataSyncMaster
import com.techglock.health.app.model.entity.UserRelatives
import com.techglock.health.app.model.entity.Users
import com.techglock.health.app.model.home.AddFeatureAccessLog
import com.techglock.health.app.model.home.AddRelativeModel
import com.techglock.health.app.model.home.CheckAppUpdateModel
import com.techglock.health.app.model.home.ContactUsModel
import com.techglock.health.app.model.home.EventsBannerModel
import com.techglock.health.app.model.home.FamilyDoctorAddModel
import com.techglock.health.app.model.home.FamilyDoctorUpdateModel
import com.techglock.health.app.model.home.FamilyDoctorsListModel
import com.techglock.health.app.model.home.GetSSOUrlModel
import com.techglock.health.app.model.home.ListDoctorSpecialityModel
import com.techglock.health.app.model.home.NimeyaModel
import com.techglock.health.app.model.home.PasswordChangeModel
import com.techglock.health.app.model.home.PersonDeleteModel
import com.techglock.health.app.model.home.ProfileImageModel
import com.techglock.health.app.model.home.RefreshTokenModel
import com.techglock.health.app.model.home.RemoveDoctorModel
import com.techglock.health.app.model.home.RemoveProfileImageModel
import com.techglock.health.app.model.home.RemoveRelativeModel
import com.techglock.health.app.model.home.SaveCloudMessagingIdModel
import com.techglock.health.app.model.home.SaveFeedbackModel
import com.techglock.health.app.model.home.UpdateLanguageProfileModel
import com.techglock.health.app.model.home.UpdateRelativeModel
import com.techglock.health.app.model.home.UpdateUserDetailsModel
import com.techglock.health.app.model.home.UploadProfileImageResponce
import com.techglock.health.app.model.home.UserDetailsModel
import com.techglock.health.app.model.home.WellfieGetSSOUrlModel
import com.techglock.health.app.model.home.WellfieGetVitalsModel
import com.techglock.health.app.model.home.WellfieListVitalsModel
import com.techglock.health.app.model.home.WellfieSaveVitalsModel
import com.techglock.health.app.model.nimeya.GetProtectoMeterHistoryModel
import com.techglock.health.app.model.nimeya.GetRetiroMeterHistoryModel
import com.techglock.health.app.model.nimeya.GetRiskoMeterHistoryModel
import com.techglock.health.app.model.nimeya.GetRiskoMeterModel
import com.techglock.health.app.model.nimeya.SaveProtectoMeterModel
import com.techglock.health.app.model.nimeya.SaveRetiroMeterModel
import com.techglock.health.app.model.nimeya.SaveRiskoMeterModel
import com.techglock.health.app.model.shr.ListRelativesModel
import com.techglock.health.app.remote.HomeDatasource
import com.techglock.health.app.repository.utils.NetworkBoundResource
import com.techglock.health.app.repository.utils.NetworkDataBoundResource
import com.techglock.health.app.repository.utils.Resource
import okhttp3.RequestBody


interface HomeRepository {

    suspend fun eventsBanner(
        forceRefresh: Boolean = false,
        data: EventsBannerModel
    ): LiveData<Resource<EventsBannerModel.EventsBannerResponse>>

    suspend fun saveCloudMessagingId(
        forceRefresh: Boolean = false,
        data: SaveCloudMessagingIdModel
    ): LiveData<Resource<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>>

    suspend fun contactUs(
        forceRefresh: Boolean = false,
        data: ContactUsModel
    ): LiveData<Resource<ContactUsModel.ContactUsResponse>>

    suspend fun addFeatureAccessLog(
        forceRefresh: Boolean = false,
        data: AddFeatureAccessLog
    ): LiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>>

    suspend fun getSSOUrl(
        forceRefresh: Boolean = false,
        data: GetSSOUrlModel
    ): LiveData<Resource<GetSSOUrlModel.GetSSOUrlResponse>>

    suspend fun getNimeyaUrl(
        forceRefresh: Boolean = false,
        data: NimeyaModel
    ): LiveData<Resource<NimeyaModel.NimeyaResponse>>

    suspend fun getRiskoMeter(
        forceRefresh: Boolean = false,
        data: GetRiskoMeterModel
    ): LiveData<Resource<GetRiskoMeterModel.RiskoMeterQuesResponse>>

    suspend fun saveRiskoMeter(
        forceRefresh: Boolean = false,
        data: SaveRiskoMeterModel
    ): LiveData<Resource<SaveRiskoMeterModel.RiskoMeterSaveResponse>>

    suspend fun saveProtectoMeter(
        forceRefresh: Boolean = false,
        data: SaveProtectoMeterModel
    ): LiveData<Resource<SaveProtectoMeterModel.ProtectoMeterSaveResponse>>

    suspend fun saveRetiroMeter(
        forceRefresh: Boolean = false,
        data: SaveRetiroMeterModel
    ): LiveData<Resource<SaveRetiroMeterModel.SaveRetiroMeterResponse>>

    suspend fun getRiskoMeterHistory(
        forceRefresh: Boolean = false,
        data: GetRiskoMeterHistoryModel
    ): LiveData<Resource<GetRiskoMeterHistoryModel.RiskoMeterHistoryResponse>>

    suspend fun getProtectoMeterHistory(
        forceRefresh: Boolean = false,
        data: GetProtectoMeterHistoryModel
    ): LiveData<Resource<GetProtectoMeterHistoryModel.ProtectoMeterHistoryResponse>>

    suspend fun getRetiroMeterHistory(
        forceRefresh: Boolean = false,
        data: GetRetiroMeterHistoryModel
    ): LiveData<Resource<GetRetiroMeterHistoryModel.RetiroMeterHistoryResponse>>

    suspend fun wellfieSaveVitals(
        forceRefresh: Boolean = false,
        data: WellfieSaveVitalsModel
    ): LiveData<Resource<WellfieSaveVitalsModel.WellfieSaveVitalsResponse>>

    suspend fun wellfieGetVitals(
        forceRefresh: Boolean = false,
        data: WellfieGetVitalsModel
    ): LiveData<Resource<WellfieGetVitalsModel.WellfieGetVitalsResponse>>

    suspend fun wellfieListVitals(
        forceRefresh: Boolean = false,
        data: WellfieListVitalsModel
    ): LiveData<Resource<WellfieListVitalsModel.WellfieListVitalsResponse>>

    suspend fun wellfieGetSSOUrl(
        forceRefresh: Boolean = false,
        data: WellfieGetSSOUrlModel
    ): LiveData<Resource<WellfieGetSSOUrlModel.WellfieGetSSOUrlResponse>>

    suspend fun personDelete(
        forceRefresh: Boolean = false,
        data: PersonDeleteModel
    ): LiveData<Resource<PersonDeleteModel.PersonDeleteResponse>>

    suspend fun saveFeedback(
        forceRefresh: Boolean = false,
        data: SaveFeedbackModel
    ): LiveData<Resource<SaveFeedbackModel.SaveFeedbackResponse>>

    suspend fun passwordChange(
        forceRefresh: Boolean = false,
        data: PasswordChangeModel
    ): LiveData<Resource<PasswordChangeModel.ChangePasswordResponse>>

    suspend fun checkAppUpdate(
        forceRefresh: Boolean = false,
        data: CheckAppUpdateModel
    ): LiveData<Resource<CheckAppUpdateModel.CheckAppUpdateResponse>>

    suspend fun getUserDetails(
        forceRefresh: Boolean = false,
        data: UserDetailsModel
    ): LiveData<Resource<UserDetailsModel.UserDetailsResponse>>

    suspend fun updateUserDetails(
        forceRefresh: Boolean = false,
        data: UpdateUserDetailsModel
    ): LiveData<Resource<UpdateUserDetailsModel.UpdateUserDetailsResponse>>

    suspend fun getProfileImage(
        forceRefresh: Boolean = false,
        data: ProfileImageModel
    ): LiveData<Resource<ProfileImageModel.ProfileImageResponse>>

    suspend fun uploadProfileImage(
        personID: RequestBody,
        fileName: RequestBody,
        documentTypeCode: RequestBody,
        byteArray: RequestBody,
        authTicket: RequestBody
    ): LiveData<Resource<UploadProfileImageResponce>>

    suspend fun removeProfileImage(
        forceRefresh: Boolean = false,
        data: RemoveProfileImageModel
    ): LiveData<Resource<RemoveProfileImageModel.RemoveProfileImageResponse>>

    suspend fun fetchRelativesList(
        forceRefresh: Boolean = false,
        data: ListRelativesModel
    ): LiveData<Resource<ListRelativesModel.ListRelativesResponse>>

    suspend fun addNewRelative(
        forceRefresh: Boolean = false,
        data: AddRelativeModel
    ): LiveData<Resource<AddRelativeModel.AddRelativeResponse>>

    suspend fun updateRelative(
        forceRefresh: Boolean = false,
        data: UpdateRelativeModel,
        relativeId: String
    ): LiveData<Resource<UpdateRelativeModel.UpdateRelativeResponse>>

    suspend fun removeRelative(
        forceRefresh: Boolean = false,
        data: RemoveRelativeModel,
        relativeId: String
    ): LiveData<Resource<RemoveRelativeModel.RemoveRelativeResponse>>

    suspend fun fetchDoctorsList(
        forceRefresh: Boolean = false,
        data: FamilyDoctorsListModel
    ): LiveData<Resource<FamilyDoctorsListModel.FamilyDoctorsResponse>>

    suspend fun fetchSpecialityList(
        forceRefresh: Boolean = false,
        data: ListDoctorSpecialityModel
    ): LiveData<Resource<ListDoctorSpecialityModel.ListDoctorSpecialityResponse>>

    suspend fun addDoctor(
        forceRefresh: Boolean = false,
        data: FamilyDoctorAddModel
    ): LiveData<Resource<FamilyDoctorAddModel.FamilyDoctorAddResponse>>

    suspend fun updateDoctor(
        forceRefresh: Boolean = false,
        data: FamilyDoctorUpdateModel
    ): LiveData<Resource<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse>>

    suspend fun removeDoctor(
        forceRefresh: Boolean = false,
        data: RemoveDoctorModel
    ): LiveData<Resource<RemoveDoctorModel.RemoveDoctorResponse>>

    suspend fun getLoggedInPersonDetails(): Users
    suspend fun getAppVersionDetails(): AppVersion
    suspend fun updateUserDetails(name: String, dob: String, personId: Int)
    suspend fun updateUserProfileImgPath(name: String, path: String)
    suspend fun getUserRelatives(): List<UserRelatives>
    suspend fun getUserRelativesExceptSelf(): List<UserRelatives>
    suspend fun getUserRelativeSpecific(relationShipCode: String): List<UserRelatives>
    suspend fun getUserRelativeForRelativeId(relativeId: String): List<UserRelatives>
    suspend fun getUserRelativeDetailsByRelativeId(relativeId: String): UserRelatives
    suspend fun getSyncMasterData(personId: String): List<DataSyncMaster>
    suspend fun saveSyncDetails(data: DataSyncMaster)
    suspend fun clearTablesForSwitchProfile()
    suspend fun logoutUser()
    suspend fun fetchUpdateProfileResponse(data: UpdateLanguageProfileModel): LiveData<Resource<UpdateLanguageProfileModel.UpdateLanguageProfileResponse>>
    suspend fun fetchRefreshTokenResponse(data: RefreshTokenModel): LiveData<Resource<RefreshTokenModel.RefreshTokenResponse>>
}

class HomeRepositoryImpl(
    private val datasource: HomeDatasource, private val dataSyncDao: DataSyncMasterDao,
    private val homeDao: VivantUserDao, private val medicationDao: MedicationDao,
    private val shrDao: StoreRecordsDao, private val hraDao: HRADao,
    private val trackParamDao: TrackParameterDao, val context: Context
) : HomeRepository {

    override suspend fun eventsBanner(
        forceRefresh: Boolean,
        data: EventsBannerModel
    ): LiveData<Resource<EventsBannerModel.EventsBannerResponse>> {

        return object :
            NetworkBoundResource<EventsBannerModel.EventsBannerResponse, BaseResponse<EventsBannerModel.EventsBannerResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): EventsBannerModel.EventsBannerResponse {
                return EventsBannerModel.EventsBannerResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<EventsBannerModel.EventsBannerResponse> {
                return datasource.eventsBannerResponse(data)
            }

            override fun processResponse(response: BaseResponse<EventsBannerModel.EventsBannerResponse>): EventsBannerModel.EventsBannerResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: EventsBannerModel.EventsBannerResponse) {

            }

            override fun shouldFetch(data: EventsBannerModel.EventsBannerResponse?): Boolean = true

        }.build().asLiveData()

    }

    override suspend fun saveCloudMessagingId(
        forceRefresh: Boolean,
        data: SaveCloudMessagingIdModel
    ): LiveData<Resource<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>> {

        return object :
            NetworkBoundResource<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse, BaseResponse<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse {
                return SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse> {
                return datasource.saveCloudMessagingId(data)
            }

            override fun processResponse(response: BaseResponse<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>): SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse) {

            }

            override fun shouldFetch(data: SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse?): Boolean {
                return true
            }

        }.build().asLiveData()

    }

    override suspend fun contactUs(
        forceRefresh: Boolean,
        data: ContactUsModel
    ): LiveData<Resource<ContactUsModel.ContactUsResponse>> {

        return object :
            NetworkBoundResource<ContactUsModel.ContactUsResponse, BaseResponse<ContactUsModel.ContactUsResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): ContactUsModel.ContactUsResponse {
                return ContactUsModel.ContactUsResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<ContactUsModel.ContactUsResponse> {
                return datasource.contactUs(data)
            }

            override fun processResponse(response: BaseResponse<ContactUsModel.ContactUsResponse>): ContactUsModel.ContactUsResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: ContactUsModel.ContactUsResponse) {}

            override fun shouldFetch(data: ContactUsModel.ContactUsResponse?): Boolean {
                return true
            }

        }.build().asLiveData()

    }

    override suspend fun addFeatureAccessLog(
        forceRefresh: Boolean,
        data: AddFeatureAccessLog
    ): LiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>> {

        return object :
            NetworkBoundResource<AddFeatureAccessLog.AddFeatureAccessLogResponse, BaseResponse<AddFeatureAccessLog.AddFeatureAccessLogResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): AddFeatureAccessLog.AddFeatureAccessLogResponse {
                return AddFeatureAccessLog.AddFeatureAccessLogResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<AddFeatureAccessLog.AddFeatureAccessLogResponse> {
                return datasource.addFeatureAccessLogResponse(data)
            }

            override fun processResponse(response: BaseResponse<AddFeatureAccessLog.AddFeatureAccessLogResponse>): AddFeatureAccessLog.AddFeatureAccessLogResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: AddFeatureAccessLog.AddFeatureAccessLogResponse) {

            }

            override fun shouldFetch(data: AddFeatureAccessLog.AddFeatureAccessLogResponse?): Boolean =
                true

        }.build().asLiveData()

    }

    override suspend fun getSSOUrl(
        forceRefresh: Boolean,
        data: GetSSOUrlModel
    ): LiveData<Resource<GetSSOUrlModel.GetSSOUrlResponse>> {

        return object :
            NetworkBoundResource<GetSSOUrlModel.GetSSOUrlResponse, BaseResponse<GetSSOUrlModel.GetSSOUrlResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): GetSSOUrlModel.GetSSOUrlResponse {
                return GetSSOUrlModel.GetSSOUrlResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<GetSSOUrlModel.GetSSOUrlResponse> {
                return datasource.getSSOUrlResponse(data)
            }

            override fun processResponse(response: BaseResponse<GetSSOUrlModel.GetSSOUrlResponse>): GetSSOUrlModel.GetSSOUrlResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: GetSSOUrlModel.GetSSOUrlResponse) {

            }

            override fun shouldFetch(data: GetSSOUrlModel.GetSSOUrlResponse?): Boolean = true

        }.build().asLiveData()

    }

    override suspend fun getNimeyaUrl(
        forceRefresh: Boolean,
        data: NimeyaModel
    ): LiveData<Resource<NimeyaModel.NimeyaResponse>> {

        return object :
            NetworkBoundResource<NimeyaModel.NimeyaResponse, BaseResponse<NimeyaModel.NimeyaResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): NimeyaModel.NimeyaResponse {
                return NimeyaModel.NimeyaResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<NimeyaModel.NimeyaResponse> {
                return datasource.getNimeyaUrlResponse(data)
            }

            override fun processResponse(response: BaseResponse<NimeyaModel.NimeyaResponse>): NimeyaModel.NimeyaResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: NimeyaModel.NimeyaResponse) {

            }

            override fun shouldFetch(data: NimeyaModel.NimeyaResponse?): Boolean = true

        }.build().asLiveData()

    }

    override suspend fun getRiskoMeter(
        forceRefresh: Boolean,
        data: GetRiskoMeterModel
    ): LiveData<Resource<GetRiskoMeterModel.RiskoMeterQuesResponse>> {

        return object :
            NetworkBoundResource<GetRiskoMeterModel.RiskoMeterQuesResponse, BaseResponse<GetRiskoMeterModel.RiskoMeterQuesResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): GetRiskoMeterModel.RiskoMeterQuesResponse {
                return GetRiskoMeterModel.RiskoMeterQuesResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<GetRiskoMeterModel.RiskoMeterQuesResponse> {
                return datasource.getRiskoMeterResponse(data)
            }

            override fun processResponse(response: BaseResponse<GetRiskoMeterModel.RiskoMeterQuesResponse>): GetRiskoMeterModel.RiskoMeterQuesResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: GetRiskoMeterModel.RiskoMeterQuesResponse) {

            }

            override fun shouldFetch(data: GetRiskoMeterModel.RiskoMeterQuesResponse?): Boolean =
                true

        }.build().asLiveData()

    }

    override suspend fun saveRiskoMeter(
        forceRefresh: Boolean,
        data: SaveRiskoMeterModel
    ): LiveData<Resource<SaveRiskoMeterModel.RiskoMeterSaveResponse>> {

        return object :
            NetworkBoundResource<SaveRiskoMeterModel.RiskoMeterSaveResponse, BaseResponse<SaveRiskoMeterModel.RiskoMeterSaveResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): SaveRiskoMeterModel.RiskoMeterSaveResponse {
                return SaveRiskoMeterModel.RiskoMeterSaveResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<SaveRiskoMeterModel.RiskoMeterSaveResponse> {
                return datasource.saveRiskoMeterResponse(data)
            }

            override fun processResponse(response: BaseResponse<SaveRiskoMeterModel.RiskoMeterSaveResponse>): SaveRiskoMeterModel.RiskoMeterSaveResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: SaveRiskoMeterModel.RiskoMeterSaveResponse) {

            }

            override fun shouldFetch(data: SaveRiskoMeterModel.RiskoMeterSaveResponse?): Boolean =
                true

        }.build().asLiveData()

    }

    override suspend fun saveProtectoMeter(
        forceRefresh: Boolean,
        data: SaveProtectoMeterModel
    ): LiveData<Resource<SaveProtectoMeterModel.ProtectoMeterSaveResponse>> {

        return object :
            NetworkBoundResource<SaveProtectoMeterModel.ProtectoMeterSaveResponse, BaseResponse<SaveProtectoMeterModel.ProtectoMeterSaveResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): SaveProtectoMeterModel.ProtectoMeterSaveResponse {
                return SaveProtectoMeterModel.ProtectoMeterSaveResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<SaveProtectoMeterModel.ProtectoMeterSaveResponse> {
                return datasource.saveProtectoMeterResponse(data)
            }

            override fun processResponse(response: BaseResponse<SaveProtectoMeterModel.ProtectoMeterSaveResponse>): SaveProtectoMeterModel.ProtectoMeterSaveResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: SaveProtectoMeterModel.ProtectoMeterSaveResponse) {

            }

            override fun shouldFetch(data: SaveProtectoMeterModel.ProtectoMeterSaveResponse?): Boolean =
                true

        }.build().asLiveData()

    }

    override suspend fun saveRetiroMeter(
        forceRefresh: Boolean,
        data: SaveRetiroMeterModel
    ): LiveData<Resource<SaveRetiroMeterModel.SaveRetiroMeterResponse>> {

        return object :
            NetworkBoundResource<SaveRetiroMeterModel.SaveRetiroMeterResponse, BaseResponse<SaveRetiroMeterModel.SaveRetiroMeterResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): SaveRetiroMeterModel.SaveRetiroMeterResponse {
                return SaveRetiroMeterModel.SaveRetiroMeterResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<SaveRetiroMeterModel.SaveRetiroMeterResponse> {
                return datasource.saveRetiroMeterResponse(data)
            }

            override fun processResponse(response: BaseResponse<SaveRetiroMeterModel.SaveRetiroMeterResponse>): SaveRetiroMeterModel.SaveRetiroMeterResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: SaveRetiroMeterModel.SaveRetiroMeterResponse) {

            }

            override fun shouldFetch(data: SaveRetiroMeterModel.SaveRetiroMeterResponse?): Boolean =
                true

        }.build().asLiveData()

    }

    override suspend fun getRiskoMeterHistory(
        forceRefresh: Boolean,
        data: GetRiskoMeterHistoryModel
    ): LiveData<Resource<GetRiskoMeterHistoryModel.RiskoMeterHistoryResponse>> {

        return object :
            NetworkBoundResource<GetRiskoMeterHistoryModel.RiskoMeterHistoryResponse, BaseResponse<GetRiskoMeterHistoryModel.RiskoMeterHistoryResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): GetRiskoMeterHistoryModel.RiskoMeterHistoryResponse {
                return GetRiskoMeterHistoryModel.RiskoMeterHistoryResponse()
            }

            override fun processResponse(response: BaseResponse<GetRiskoMeterHistoryModel.RiskoMeterHistoryResponse>): GetRiskoMeterHistoryModel.RiskoMeterHistoryResponse {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<GetRiskoMeterHistoryModel.RiskoMeterHistoryResponse> {
                return datasource.getRiskoMeterHistoryResponse(data)
            }

            override suspend fun saveCallResults(items: GetRiskoMeterHistoryModel.RiskoMeterHistoryResponse) {

            }

            override fun shouldFetch(data: GetRiskoMeterHistoryModel.RiskoMeterHistoryResponse?): Boolean =
                true

        }.build().asLiveData()

    }

    override suspend fun getProtectoMeterHistory(
        forceRefresh: Boolean,
        data: GetProtectoMeterHistoryModel
    ): LiveData<Resource<GetProtectoMeterHistoryModel.ProtectoMeterHistoryResponse>> {

        return object :
            NetworkBoundResource<GetProtectoMeterHistoryModel.ProtectoMeterHistoryResponse, BaseResponse<GetProtectoMeterHistoryModel.ProtectoMeterHistoryResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): GetProtectoMeterHistoryModel.ProtectoMeterHistoryResponse {
                return GetProtectoMeterHistoryModel.ProtectoMeterHistoryResponse()
            }

            override fun processResponse(response: BaseResponse<GetProtectoMeterHistoryModel.ProtectoMeterHistoryResponse>): GetProtectoMeterHistoryModel.ProtectoMeterHistoryResponse {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<GetProtectoMeterHistoryModel.ProtectoMeterHistoryResponse> {
                return datasource.getProtectoMeterHistoryResponse(data)
            }

            override suspend fun saveCallResults(items: GetProtectoMeterHistoryModel.ProtectoMeterHistoryResponse) {

            }

            override fun shouldFetch(data: GetProtectoMeterHistoryModel.ProtectoMeterHistoryResponse?): Boolean =
                true

        }.build().asLiveData()

    }

    override suspend fun getRetiroMeterHistory(
        forceRefresh: Boolean,
        data: GetRetiroMeterHistoryModel
    ): LiveData<Resource<GetRetiroMeterHistoryModel.RetiroMeterHistoryResponse>> {

        return object :
            NetworkBoundResource<GetRetiroMeterHistoryModel.RetiroMeterHistoryResponse, BaseResponse<GetRetiroMeterHistoryModel.RetiroMeterHistoryResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): GetRetiroMeterHistoryModel.RetiroMeterHistoryResponse {
                return GetRetiroMeterHistoryModel.RetiroMeterHistoryResponse()
            }

            override fun processResponse(response: BaseResponse<GetRetiroMeterHistoryModel.RetiroMeterHistoryResponse>): GetRetiroMeterHistoryModel.RetiroMeterHistoryResponse {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<GetRetiroMeterHistoryModel.RetiroMeterHistoryResponse> {
                return datasource.getRetiroMeterHistoryResponse(data)
            }

            override suspend fun saveCallResults(items: GetRetiroMeterHistoryModel.RetiroMeterHistoryResponse) {

            }

            override fun shouldFetch(data: GetRetiroMeterHistoryModel.RetiroMeterHistoryResponse?): Boolean =
                true

        }.build().asLiveData()

    }

    override suspend fun wellfieSaveVitals(
        forceRefresh: Boolean,
        data: WellfieSaveVitalsModel
    ): LiveData<Resource<WellfieSaveVitalsModel.WellfieSaveVitalsResponse>> {

        return object :
            NetworkBoundResource<WellfieSaveVitalsModel.WellfieSaveVitalsResponse, BaseResponse<WellfieSaveVitalsModel.WellfieSaveVitalsResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): WellfieSaveVitalsModel.WellfieSaveVitalsResponse {
                return WellfieSaveVitalsModel.WellfieSaveVitalsResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<WellfieSaveVitalsModel.WellfieSaveVitalsResponse> {
                return datasource.wellfieSaveVitalsResponse(data)
            }

            override fun processResponse(response: BaseResponse<WellfieSaveVitalsModel.WellfieSaveVitalsResponse>): WellfieSaveVitalsModel.WellfieSaveVitalsResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: WellfieSaveVitalsModel.WellfieSaveVitalsResponse) {

            }

            override fun shouldFetch(data: WellfieSaveVitalsModel.WellfieSaveVitalsResponse?): Boolean =
                true

        }.build().asLiveData()

    }

    override suspend fun wellfieGetVitals(
        forceRefresh: Boolean,
        data: WellfieGetVitalsModel
    ): LiveData<Resource<WellfieGetVitalsModel.WellfieGetVitalsResponse>> {

        return object :
            NetworkBoundResource<WellfieGetVitalsModel.WellfieGetVitalsResponse, BaseResponse<WellfieGetVitalsModel.WellfieGetVitalsResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): WellfieGetVitalsModel.WellfieGetVitalsResponse {
                return WellfieGetVitalsModel.WellfieGetVitalsResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<WellfieGetVitalsModel.WellfieGetVitalsResponse> {
                return datasource.wellfieGetVitalsResponse(data)
            }

            override fun processResponse(response: BaseResponse<WellfieGetVitalsModel.WellfieGetVitalsResponse>): WellfieGetVitalsModel.WellfieGetVitalsResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: WellfieGetVitalsModel.WellfieGetVitalsResponse) {

            }

            override fun shouldFetch(data: WellfieGetVitalsModel.WellfieGetVitalsResponse?): Boolean =
                true

        }.build().asLiveData()

    }

    override suspend fun wellfieListVitals(
        forceRefresh: Boolean,
        data: WellfieListVitalsModel
    ): LiveData<Resource<WellfieListVitalsModel.WellfieListVitalsResponse>> {

        return object :
            NetworkBoundResource<WellfieListVitalsModel.WellfieListVitalsResponse, BaseResponse<WellfieListVitalsModel.WellfieListVitalsResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): WellfieListVitalsModel.WellfieListVitalsResponse {
                return WellfieListVitalsModel.WellfieListVitalsResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<WellfieListVitalsModel.WellfieListVitalsResponse> {
                return datasource.wellfieListVitalsResponse(data)
            }

            override fun processResponse(response: BaseResponse<WellfieListVitalsModel.WellfieListVitalsResponse>): WellfieListVitalsModel.WellfieListVitalsResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: WellfieListVitalsModel.WellfieListVitalsResponse) {

            }

            override fun shouldFetch(data: WellfieListVitalsModel.WellfieListVitalsResponse?): Boolean =
                true

        }.build().asLiveData()

    }

    override suspend fun wellfieGetSSOUrl(
        forceRefresh: Boolean,
        data: WellfieGetSSOUrlModel
    ): LiveData<Resource<WellfieGetSSOUrlModel.WellfieGetSSOUrlResponse>> {

        return object :
            NetworkBoundResource<WellfieGetSSOUrlModel.WellfieGetSSOUrlResponse, BaseResponse<WellfieGetSSOUrlModel.WellfieGetSSOUrlResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): WellfieGetSSOUrlModel.WellfieGetSSOUrlResponse {
                return WellfieGetSSOUrlModel.WellfieGetSSOUrlResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<WellfieGetSSOUrlModel.WellfieGetSSOUrlResponse> {
                return datasource.wellfieGetSSOUrlResponse(data)
            }

            override fun processResponse(response: BaseResponse<WellfieGetSSOUrlModel.WellfieGetSSOUrlResponse>): WellfieGetSSOUrlModel.WellfieGetSSOUrlResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: WellfieGetSSOUrlModel.WellfieGetSSOUrlResponse) {

            }

            override fun shouldFetch(data: WellfieGetSSOUrlModel.WellfieGetSSOUrlResponse?): Boolean =
                true

        }.build().asLiveData()

    }

    override suspend fun personDelete(
        forceRefresh: Boolean,
        data: PersonDeleteModel
    ): LiveData<Resource<PersonDeleteModel.PersonDeleteResponse>> {

        return object :
            NetworkBoundResource<PersonDeleteModel.PersonDeleteResponse, BaseResponse<PersonDeleteModel.PersonDeleteResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): PersonDeleteModel.PersonDeleteResponse {
                return PersonDeleteModel.PersonDeleteResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<PersonDeleteModel.PersonDeleteResponse> {
                return datasource.personDeleteResponse(data)
            }

            override fun processResponse(response: BaseResponse<PersonDeleteModel.PersonDeleteResponse>): PersonDeleteModel.PersonDeleteResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: PersonDeleteModel.PersonDeleteResponse) {

            }

            override fun shouldFetch(data: PersonDeleteModel.PersonDeleteResponse?): Boolean = true
        }.build().asLiveData()

    }

    override suspend fun saveFeedback(
        forceRefresh: Boolean,
        data: SaveFeedbackModel
    ): LiveData<Resource<SaveFeedbackModel.SaveFeedbackResponse>> {

        return object :
            NetworkBoundResource<SaveFeedbackModel.SaveFeedbackResponse, BaseResponse<SaveFeedbackModel.SaveFeedbackResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): SaveFeedbackModel.SaveFeedbackResponse {
                return SaveFeedbackModel.SaveFeedbackResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<SaveFeedbackModel.SaveFeedbackResponse> {
                return datasource.saveFeedbackResponse(data)
            }

            override fun processResponse(response: BaseResponse<SaveFeedbackModel.SaveFeedbackResponse>): SaveFeedbackModel.SaveFeedbackResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: SaveFeedbackModel.SaveFeedbackResponse) {}

            override fun shouldFetch(data: SaveFeedbackModel.SaveFeedbackResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun passwordChange(
        forceRefresh: Boolean,
        data: PasswordChangeModel
    ): LiveData<Resource<PasswordChangeModel.ChangePasswordResponse>> {

        return object :
            NetworkBoundResource<PasswordChangeModel.ChangePasswordResponse, BaseResponse<PasswordChangeModel.ChangePasswordResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): PasswordChangeModel.ChangePasswordResponse {
                return PasswordChangeModel.ChangePasswordResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<PasswordChangeModel.ChangePasswordResponse> {
                return datasource.passwordChangeResponse(data)
            }

            override fun processResponse(response: BaseResponse<PasswordChangeModel.ChangePasswordResponse>): PasswordChangeModel.ChangePasswordResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: PasswordChangeModel.ChangePasswordResponse) {}

            override fun shouldFetch(data: PasswordChangeModel.ChangePasswordResponse?): Boolean {
                return true
            }

        }.build().asLiveData()

    }

    override suspend fun checkAppUpdate(
        forceRefresh: Boolean,
        data: CheckAppUpdateModel
    ): LiveData<Resource<CheckAppUpdateModel.CheckAppUpdateResponse>> {

        return object :
            NetworkBoundResource<CheckAppUpdateModel.CheckAppUpdateResponse, BaseResponse<CheckAppUpdateModel.CheckAppUpdateResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): CheckAppUpdateModel.CheckAppUpdateResponse {
                val appVersion: ArrayList<AppVersion> = arrayListOf()
                val versionDetails = homeDao.getAppVersionDetails()
                if (versionDetails != null) {
                    appVersion.add(homeDao.getAppVersionDetails())
                }
                val appUpdateResp = CheckAppUpdateModel.CheckAppUpdateResponse()
                appUpdateResp.result = CheckAppUpdateModel.Result(appVersion = appVersion)
                return appUpdateResp
            }

            override suspend fun createCallAsync(): BaseResponse<CheckAppUpdateModel.CheckAppUpdateResponse> {
                return datasource.checkAppUpdateResponse(data)
            }

            override fun processResponse(response: BaseResponse<CheckAppUpdateModel.CheckAppUpdateResponse>): CheckAppUpdateModel.CheckAppUpdateResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: CheckAppUpdateModel.CheckAppUpdateResponse) {
                if (!items.result.appVersion.isNullOrEmpty()) {
                    homeDao.insertAppVersionDetails(items.result.appVersion[0])
                }
            }

            override fun shouldFetch(data: CheckAppUpdateModel.CheckAppUpdateResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun getUserDetails(
        forceRefresh: Boolean,
        data: UserDetailsModel
    ): LiveData<Resource<UserDetailsModel.UserDetailsResponse>> {

        return object :
            NetworkBoundResource<UserDetailsModel.UserDetailsResponse, BaseResponse<UserDetailsModel.UserDetailsResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): UserDetailsModel.UserDetailsResponse {
                return UserDetailsModel.UserDetailsResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<UserDetailsModel.UserDetailsResponse> {
                return datasource.getUserDetailsResponse(data)
            }

            override fun processResponse(response: BaseResponse<UserDetailsModel.UserDetailsResponse>): UserDetailsModel.UserDetailsResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: UserDetailsModel.UserDetailsResponse) {

            }

            override fun shouldFetch(data: UserDetailsModel.UserDetailsResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun updateUserDetails(
        forceRefresh: Boolean,
        data: UpdateUserDetailsModel
    ): LiveData<Resource<UpdateUserDetailsModel.UpdateUserDetailsResponse>> {

        return object :
            NetworkBoundResource<UpdateUserDetailsModel.UpdateUserDetailsResponse, BaseResponse<UpdateUserDetailsModel.UpdateUserDetailsResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): UpdateUserDetailsModel.UpdateUserDetailsResponse {
                return UpdateUserDetailsModel.UpdateUserDetailsResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<UpdateUserDetailsModel.UpdateUserDetailsResponse> {
                return datasource.updateUserDetailsResponse(data)
            }

            override fun processResponse(response: BaseResponse<UpdateUserDetailsModel.UpdateUserDetailsResponse>): UpdateUserDetailsModel.UpdateUserDetailsResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: UpdateUserDetailsModel.UpdateUserDetailsResponse) {
                Utilities.printLog("saveCallResults=> $items")
            }

            override fun shouldFetch(data: UpdateUserDetailsModel.UpdateUserDetailsResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun getProfileImage(
        forceRefresh: Boolean,
        data: ProfileImageModel
    ): LiveData<Resource<ProfileImageModel.ProfileImageResponse>> {

        return object :
            NetworkBoundResource<ProfileImageModel.ProfileImageResponse, BaseResponse<ProfileImageModel.ProfileImageResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): ProfileImageModel.ProfileImageResponse {
                return ProfileImageModel.ProfileImageResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<ProfileImageModel.ProfileImageResponse> {
                return datasource.getProfileImageResponse(data)
            }

            override fun processResponse(response: BaseResponse<ProfileImageModel.ProfileImageResponse>): ProfileImageModel.ProfileImageResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: ProfileImageModel.ProfileImageResponse) {

            }

            override fun shouldFetch(data: ProfileImageModel.ProfileImageResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun uploadProfileImage(
        personID: RequestBody,
        fileName: RequestBody,
        documentTypeCode: RequestBody,
        byteArray: RequestBody,
        authTicket: RequestBody
    ): LiveData<Resource<UploadProfileImageResponce>> {

        return object :
            NetworkDataBoundResource<UploadProfileImageResponce, BaseResponse<UploadProfileImageResponce>>(
                context
            ) {

            override fun processResponse(response: BaseResponse<UploadProfileImageResponce>): UploadProfileImageResponce {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<UploadProfileImageResponce> {
                return datasource.uploadProfileImageResponce(
                    personID,
                    fileName,
                    documentTypeCode,
                    byteArray,
                    authTicket
                )
            }

        }.build().asLiveData()
    }

    override suspend fun removeProfileImage(
        forceRefresh: Boolean,
        data: RemoveProfileImageModel
    ): LiveData<Resource<RemoveProfileImageModel.RemoveProfileImageResponse>> {

        return object :
            NetworkBoundResource<RemoveProfileImageModel.RemoveProfileImageResponse, BaseResponse<RemoveProfileImageModel.RemoveProfileImageResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): RemoveProfileImageModel.RemoveProfileImageResponse {
                return RemoveProfileImageModel.RemoveProfileImageResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<RemoveProfileImageModel.RemoveProfileImageResponse> {
                return datasource.removeProfileImageResponse(data)
            }

            override fun processResponse(response: BaseResponse<RemoveProfileImageModel.RemoveProfileImageResponse>): RemoveProfileImageModel.RemoveProfileImageResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: RemoveProfileImageModel.RemoveProfileImageResponse) {}

            override fun shouldFetch(data: RemoveProfileImageModel.RemoveProfileImageResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun fetchRelativesList(
        forceRefresh: Boolean,
        data: ListRelativesModel
    ): LiveData<Resource<ListRelativesModel.ListRelativesResponse>> {

        return object :
            NetworkBoundResource<ListRelativesModel.ListRelativesResponse, BaseResponse<ListRelativesModel.ListRelativesResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): ListRelativesModel.ListRelativesResponse {
                return ListRelativesModel.ListRelativesResponse(relativeList = homeDao.geAllRelativeList())
            }

            override suspend fun saveCallResults(items: ListRelativesModel.ListRelativesResponse) {
                homeDao.deleteUserRelativesTable()
                val relativeList = items.persons
                val relativeInfoList: ArrayList<UserRelatives> = ArrayList()
                val userDetails = homeDao.getUser()
                //ToolsTrackerSingleton.instance.userDetails = userDetails
                var relationshipId = ""
                val user = UserRelatives(
                    relativeID = userDetails.personId.toDouble().toInt().toString(),
                    firstName = userDetails.firstName,
                    lastName = userDetails.lastName,
                    dateOfBirth = DateHelper.getDateTimeAs_ddMMMyyyy(userDetails.dateOfBirth),
                    age = userDetails.age.toDouble().toInt().toString(),
                    gender = userDetails.gender,
                    contactNo = userDetails.phoneNumber,
                    emailAddress = userDetails.emailAddress,
                    relationshipCode = "SELF",
                    relationship = "Self"
                )
                relativeInfoList.add(user)
                for (item in relativeList) {
                    if (item.relationships.isNotEmpty()) {
                        relationshipId = item.relationships[0].id.toString()
                    }
                    val userRelative = UserRelatives(
                        relativeID = item.id.toDouble().toInt().toString(),
                        firstName = item.firstName,
                        lastName = item.lastName,
                        dateOfBirth = DateHelper.getDateTimeAs_ddMMMyyyy(item.dateOfBirth),
                        age = item.age.toString(),
                        gender = item.gender.toString(),
                        contactNo = item.contact.primaryContactNo,
                        emailAddress = item.contact.emailAddress,
                        relationshipCode = item.relationships[0].relationshipCode,
                        relationship = item.relationships[0].relationship,
                        relationShipID = relationshipId
                    )
                    relativeInfoList.add(userRelative)
                }
                homeDao.insertUserRelative(relativeInfoList)

            }

            override suspend fun createCallAsync(): BaseResponse<ListRelativesModel.ListRelativesResponse> {
                return datasource.getRelativesListResponse(data)
            }

            override fun processResponse(response: BaseResponse<ListRelativesModel.ListRelativesResponse>): ListRelativesModel.ListRelativesResponse {
                return response.jSONData
            }

            override fun shouldFetch(data: ListRelativesModel.ListRelativesResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun removeRelative(
        forceRefresh: Boolean,
        data: RemoveRelativeModel,
        relativeId: String
    ): LiveData<Resource<RemoveRelativeModel.RemoveRelativeResponse>> {

        return object :
            NetworkBoundResource<RemoveRelativeModel.RemoveRelativeResponse, BaseResponse<RemoveRelativeModel.RemoveRelativeResponse>>(
                context
            ) {

            var removeRelative = RemoveRelativeModel.RemoveRelativeResponse()

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): RemoveRelativeModel.RemoveRelativeResponse {
                return RemoveRelativeModel.RemoveRelativeResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<RemoveRelativeModel.RemoveRelativeResponse> {
                return datasource.removeRelativeResponse(data)
            }

            override fun processResponse(response: BaseResponse<RemoveRelativeModel.RemoveRelativeResponse>): RemoveRelativeModel.RemoveRelativeResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: RemoveRelativeModel.RemoveRelativeResponse) {
                removeRelative = items
                homeDao.deleteUserRelativeById(relativeId)
            }

            override fun shouldFetch(data: RemoveRelativeModel.RemoveRelativeResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun addNewRelative(
        forceRefresh: Boolean,
        data: AddRelativeModel
    ): LiveData<Resource<AddRelativeModel.AddRelativeResponse>> {

        return object :
            NetworkBoundResource<AddRelativeModel.AddRelativeResponse, BaseResponse<AddRelativeModel.AddRelativeResponse>>(
                context
            ) {

            var person = AddRelativeModel.PersonResp()

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): AddRelativeModel.AddRelativeResponse {
                val resp = AddRelativeModel.AddRelativeResponse()
                resp.person = person
                return resp
            }

            override suspend fun createCallAsync(): BaseResponse<AddRelativeModel.AddRelativeResponse> {
                return datasource.addRelativeResponse(data)
            }

            override fun processResponse(response: BaseResponse<AddRelativeModel.AddRelativeResponse>): AddRelativeModel.AddRelativeResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: AddRelativeModel.AddRelativeResponse) {
                person = items.person
                var relationshipId = ""
                var relationshipCode = ""
                if (items.person.relationships.isNotEmpty()) {
                    relationshipCode = items.person.relationships[0].relationshipCode
                    relationshipId = items.person.relationships[0].id.toString()
                }
                val userRelative = UserRelatives(
                    relativeID = items.person.id.toString(),
                    firstName = items.person.firstName,
                    lastName = items.person.lastName,
                    dateOfBirth = DateHelper.getDateTimeAs_ddMMMyyyy(items.person.dateOfBirth),
                    age = items.person.age.toString(),
                    gender = items.person.gender.toString(),
                    contactNo = items.person.contact.primaryContactNo,
                    emailAddress = items.person.contact.emailAddress,
                    relationshipCode = relationshipCode,
                    relationship = Utilities.getRelationshipByRelationshipCode(
                        relationshipCode,
                        context
                    ),
                    relationShipID = relationshipId
                )
                homeDao.insertUserRelative(userRelative)
            }

            override fun shouldFetch(data: AddRelativeModel.AddRelativeResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun updateRelative(
        forceRefresh: Boolean,
        data: UpdateRelativeModel,
        relativeId: String
    ): LiveData<Resource<UpdateRelativeModel.UpdateRelativeResponse>> {

        return object :
            NetworkBoundResource<UpdateRelativeModel.UpdateRelativeResponse, BaseResponse<UpdateRelativeModel.UpdateRelativeResponse>>(
                context
            ) {

            var person = UpdateRelativeModel.PersonResp()

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): UpdateRelativeModel.UpdateRelativeResponse {
                //return UpdateRelativeModel.UpdateRelativeResponse()
                val resp = UpdateRelativeModel.UpdateRelativeResponse()
                resp.person = person
                return resp
            }

            override fun processResponse(response: BaseResponse<UpdateRelativeModel.UpdateRelativeResponse>): UpdateRelativeModel.UpdateRelativeResponse {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<UpdateRelativeModel.UpdateRelativeResponse> {
                return datasource.updateRelativeResponse(data)
            }

            override suspend fun saveCallResults(items: UpdateRelativeModel.UpdateRelativeResponse) {

                person = items.person
                val dob = DateHelper.getDateTimeAs_ddMMMyyyy(items.person.dateOfBirth)
                homeDao.updateUserRelativesDetails(
                    relativeId,
                    items.person.firstName,
                    items.person.contact.emailAddress,
                    items.person.contact.primaryContactNo,
                    dob
                )
            }

            override fun shouldFetch(data: UpdateRelativeModel.UpdateRelativeResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun fetchDoctorsList(
        forceRefresh: Boolean,
        data: FamilyDoctorsListModel
    ): LiveData<Resource<FamilyDoctorsListModel.FamilyDoctorsResponse>> {

        return object :
            NetworkBoundResource<FamilyDoctorsListModel.FamilyDoctorsResponse, BaseResponse<FamilyDoctorsListModel.FamilyDoctorsResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): FamilyDoctorsListModel.FamilyDoctorsResponse {
                return FamilyDoctorsListModel.FamilyDoctorsResponse()
            }

            override fun processResponse(response: BaseResponse<FamilyDoctorsListModel.FamilyDoctorsResponse>): FamilyDoctorsListModel.FamilyDoctorsResponse {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<FamilyDoctorsListModel.FamilyDoctorsResponse> {
                return datasource.getDoctorsListResponse(data)
            }

            override suspend fun saveCallResults(items: FamilyDoctorsListModel.FamilyDoctorsResponse) {}

            override fun shouldFetch(data: FamilyDoctorsListModel.FamilyDoctorsResponse?): Boolean {
                return true
            }

        }.build().asLiveData()

    }

    override suspend fun fetchSpecialityList(
        forceRefresh: Boolean,
        data: ListDoctorSpecialityModel
    ): LiveData<Resource<ListDoctorSpecialityModel.ListDoctorSpecialityResponse>> {

        return object :
            NetworkBoundResource<ListDoctorSpecialityModel.ListDoctorSpecialityResponse, BaseResponse<ListDoctorSpecialityModel.ListDoctorSpecialityResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): ListDoctorSpecialityModel.ListDoctorSpecialityResponse {
                return ListDoctorSpecialityModel.ListDoctorSpecialityResponse()
            }

            override fun processResponse(response: BaseResponse<ListDoctorSpecialityModel.ListDoctorSpecialityResponse>): ListDoctorSpecialityModel.ListDoctorSpecialityResponse {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<ListDoctorSpecialityModel.ListDoctorSpecialityResponse> {
                return datasource.getSpecialityListResponse(data)
            }

            override suspend fun saveCallResults(items: ListDoctorSpecialityModel.ListDoctorSpecialityResponse) {}

            override fun shouldFetch(data: ListDoctorSpecialityModel.ListDoctorSpecialityResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun addDoctor(
        forceRefresh: Boolean,
        data: FamilyDoctorAddModel
    ): LiveData<Resource<FamilyDoctorAddModel.FamilyDoctorAddResponse>> {

        return object :
            NetworkBoundResource<FamilyDoctorAddModel.FamilyDoctorAddResponse, BaseResponse<FamilyDoctorAddModel.FamilyDoctorAddResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): FamilyDoctorAddModel.FamilyDoctorAddResponse {
                return FamilyDoctorAddModel.FamilyDoctorAddResponse()
            }

            override fun processResponse(response: BaseResponse<FamilyDoctorAddModel.FamilyDoctorAddResponse>): FamilyDoctorAddModel.FamilyDoctorAddResponse {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<FamilyDoctorAddModel.FamilyDoctorAddResponse> {
                return datasource.addDoctorResponse(data)
            }

            override suspend fun saveCallResults(items: FamilyDoctorAddModel.FamilyDoctorAddResponse) {}

            override fun shouldFetch(data: FamilyDoctorAddModel.FamilyDoctorAddResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun updateDoctor(
        forceRefresh: Boolean,
        data: FamilyDoctorUpdateModel
    ): LiveData<Resource<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse>> {

        return object :
            NetworkBoundResource<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse, BaseResponse<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse {
                return FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse()
            }

            override fun processResponse(response: BaseResponse<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse>): FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse> {
                return datasource.updateDoctorResponse(data)
            }

            override suspend fun saveCallResults(items: FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse) {}

            override fun shouldFetch(data: FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun removeDoctor(
        forceRefresh: Boolean,
        data: RemoveDoctorModel
    ): LiveData<Resource<RemoveDoctorModel.RemoveDoctorResponse>> {

        return object :
            NetworkBoundResource<RemoveDoctorModel.RemoveDoctorResponse, BaseResponse<RemoveDoctorModel.RemoveDoctorResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): RemoveDoctorModel.RemoveDoctorResponse {
                return RemoveDoctorModel.RemoveDoctorResponse()
            }

            override fun processResponse(response: BaseResponse<RemoveDoctorModel.RemoveDoctorResponse>): RemoveDoctorModel.RemoveDoctorResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: RemoveDoctorModel.RemoveDoctorResponse) {}

            override suspend fun createCallAsync(): BaseResponse<RemoveDoctorModel.RemoveDoctorResponse> {
                return datasource.removeDoctorResponse(data)
            }

            override fun shouldFetch(data: RemoveDoctorModel.RemoveDoctorResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun getLoggedInPersonDetails(): Users {
        return homeDao.getUser()
    }

    override suspend fun getAppVersionDetails(): AppVersion {
        return homeDao.getAppVersionDetails()
    }

    override suspend fun updateUserDetails(name: String, dob: String, personId: Int) {
        try {
            var age = DateHelper.calculatePersonAge(dob, context)
            if (age.contains(" ", true)) {
                age = age.split(" ")[0]
            }
            Utilities.printLog("updateUserDetailsAGE=> $age")
            homeDao.updateName(name, dob, age.toInt(), personId)
            homeDao.updateUserInUserRelativesDetails(name, dob, age.toInt(), personId.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            homeDao.updateName(name, dob, 0, personId)
            homeDao.updateUserInUserRelativesDetails(name, dob, 0, personId.toString())
        }

    }

    override suspend fun updateUserProfileImgPath(name: String, path: String) {
        Utilities.printLogError("ProfileImagePath_Updated")
        return homeDao.updateUserProfileImgPath(name, path)
    }

    override suspend fun getUserRelatives(): List<UserRelatives> {
        return homeDao.geAllRelativeList()
    }

    override suspend fun getUserRelativesExceptSelf(): List<UserRelatives> {
        return homeDao.getUserRelatives()
    }

    override suspend fun getUserRelativeSpecific(relationShipCode: String): List<UserRelatives> {
        return homeDao.getUserRelativeSpecific(relationShipCode)
    }

    override suspend fun getUserRelativeForRelativeId(relativeId: String): List<UserRelatives> {
        return homeDao.getUserRelativeForRelativeId(relativeId)
    }

    override suspend fun getUserRelativeDetailsByRelativeId(relativeId: String): UserRelatives {
        return homeDao.getUserRelativeDetailsByRelativeId(relativeId)
    }

    override suspend fun getSyncMasterData(personId: String): List<DataSyncMaster> {
        return dataSyncDao.getLastSyncDataList(personId)
    }

    override suspend fun saveSyncDetails(data: DataSyncMaster) {
//        if(dataSyncDao.getLastSyncDataList().find { it.apiName == data.apiName } == null)
        dataSyncDao.insertApiSyncData(data)
//        else
//            dataSyncDao.updateRecord(data)
    }

    override suspend fun clearTablesForSwitchProfile() {
        //Medication
        medicationDao.deleteMedicationTable()
        //Health Records
        shrDao.deleteHealthDocumentTableTable()
        //Hra
        hraDao.deleteHraQuesTable()
        hraDao.deleteHraVitalDetailsTable()
        hraDao.deleteHraLabDetailsTable()
        // Track parameters
        //trackParamDao.deleteHistory()
    }

    override suspend fun logoutUser() {
        homeDao.deleteAllRecords()
        homeDao.deleteUserRelativesTable()
    }

    override suspend fun fetchUpdateProfileResponse(
        data: UpdateLanguageProfileModel
    ): LiveData<Resource<UpdateLanguageProfileModel.UpdateLanguageProfileResponse>> {
        return object :
            NetworkBoundResource<UpdateLanguageProfileModel.UpdateLanguageProfileResponse, BaseResponse<UpdateLanguageProfileModel.UpdateLanguageProfileResponse>>(
                context
            ) {
            override fun processResponse(response: BaseResponse<UpdateLanguageProfileModel.UpdateLanguageProfileResponse>): UpdateLanguageProfileModel.UpdateLanguageProfileResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: UpdateLanguageProfileModel.UpdateLanguageProfileResponse) {

            }

            override fun shouldFetch(data: UpdateLanguageProfileModel.UpdateLanguageProfileResponse?): Boolean {
                return true
            }

            override fun shouldStoreInDb(): Boolean {
                return false
            }

            override suspend fun loadFromDb(): UpdateLanguageProfileModel.UpdateLanguageProfileResponse {
                return UpdateLanguageProfileModel.UpdateLanguageProfileResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<UpdateLanguageProfileModel.UpdateLanguageProfileResponse> {
                return datasource.updateLanguagePreferences(data)
            }

        }.build().asLiveData()
    }

    override suspend fun fetchRefreshTokenResponse(data: RefreshTokenModel): LiveData<Resource<RefreshTokenModel.RefreshTokenResponse>> {
        return object :
            NetworkBoundResource<RefreshTokenModel.RefreshTokenResponse, BaseResponse<RefreshTokenModel.RefreshTokenResponse>>(
                context
            ) {
            override fun processResponse(response: BaseResponse<RefreshTokenModel.RefreshTokenResponse>): RefreshTokenModel.RefreshTokenResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: RefreshTokenModel.RefreshTokenResponse) {

            }

            override fun shouldFetch(data: RefreshTokenModel.RefreshTokenResponse?): Boolean {
                return true
            }

            override fun shouldStoreInDb(): Boolean {
                return false
            }

            override suspend fun loadFromDb(): RefreshTokenModel.RefreshTokenResponse {
                return RefreshTokenModel.RefreshTokenResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<RefreshTokenModel.RefreshTokenResponse> {
                return datasource.fetchRefreshToken(data)
            }

        }.build().asLiveData()
    }

}

