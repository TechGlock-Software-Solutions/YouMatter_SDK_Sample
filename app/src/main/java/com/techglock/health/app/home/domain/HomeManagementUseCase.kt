package com.techglock.health.app.home.domain

import androidx.lifecycle.LiveData
import com.techglock.health.app.model.blogs.BlogRecommendationListModel
import com.techglock.health.app.model.blogs.BlogsListAllModel
import com.techglock.health.app.model.blogs.BlogsListByCategoryModel
import com.techglock.health.app.model.entity.HRASummary
import com.techglock.health.app.model.entity.HealthDocument
import com.techglock.health.app.model.entity.UserRelatives
import com.techglock.health.app.model.entity.Users
import com.techglock.health.app.model.fitness.GetStepsGoalModel
import com.techglock.health.app.model.home.AddFeatureAccessLog
import com.techglock.health.app.model.home.AddRelativeModel
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
import com.techglock.health.app.model.parameter.SaveParameterModel
import com.techglock.health.app.model.security.PhoneExistsModel
import com.techglock.health.app.model.security.TermsConditionsModel
import com.techglock.health.app.model.shr.ListDocumentsModel
import com.techglock.health.app.model.shr.ListRelativesModel
import com.techglock.health.app.model.waterTracker.GetDailyWaterIntakeModel
import com.techglock.health.app.repository.BlogsRepository
import com.techglock.health.app.repository.FitnessRepository
import com.techglock.health.app.repository.HomeRepository
import com.techglock.health.app.repository.HraRepository
import com.techglock.health.app.repository.ParameterRepository
import com.techglock.health.app.repository.StoreRecordRepository
import com.techglock.health.app.repository.UserRepository
import com.techglock.health.app.repository.WaterTrackerRepository
import com.techglock.health.app.repository.utils.Resource
import okhttp3.RequestBody
import javax.inject.Inject

class HomeManagementUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
    private val hraRepository: HraRepository,
    private val userRepository: UserRepository,
    private val blogRepository: BlogsRepository,
    private val parameterRepository: ParameterRepository,
    private val healthRecordRepository: StoreRecordRepository,
    private val fitnessRepository: FitnessRepository,
    private val waterTrackerRepository: WaterTrackerRepository
) {

    suspend fun invokeEventsBanner(data: EventsBannerModel): LiveData<Resource<EventsBannerModel.EventsBannerResponse>> {
        return homeRepository.eventsBanner(data = data)
    }

    suspend fun invokeSaveParameter(data: SaveParameterModel): LiveData<Resource<SaveParameterModel.Response>> {
        return parameterRepository.saveLabParameter(data)
    }

    suspend fun invokeTermsCondition(
        isForceRefresh: Boolean,
        data: TermsConditionsModel
    ): LiveData<Resource<TermsConditionsModel.TermsConditionsResponse>> {
        return userRepository.getTermsConditionsResponse(isForceRefresh, data)
    }

    suspend fun invokeSaveFeedback(
        isForceRefresh: Boolean,
        data: SaveFeedbackModel
    ): LiveData<Resource<SaveFeedbackModel.SaveFeedbackResponse>> {
        return homeRepository.saveFeedback(isForceRefresh, data)
    }

    suspend fun invokePasswordChange(
        isForceRefresh: Boolean,
        data: PasswordChangeModel
    ): LiveData<Resource<PasswordChangeModel.ChangePasswordResponse>> {
        return homeRepository.passwordChange(isForceRefresh, data)
    }

    suspend fun invokeContactUs(
        isForceRefresh: Boolean,
        data: ContactUsModel
    ): LiveData<Resource<ContactUsModel.ContactUsResponse>> {
        return homeRepository.contactUs(isForceRefresh, data)
    }

    suspend fun invokeAddFeatureAccessLog(
        isForceRefresh: Boolean,
        data: AddFeatureAccessLog
    ): LiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>> {
        return homeRepository.addFeatureAccessLog(isForceRefresh, data)
    }

    suspend fun invokeGetSSOUrl(
        isForceRefresh: Boolean,
        data: GetSSOUrlModel
    ): LiveData<Resource<GetSSOUrlModel.GetSSOUrlResponse>> {
        return homeRepository.getSSOUrl(isForceRefresh, data)
    }

    suspend fun invokeGetNimeyaUrl(
        isForceRefresh: Boolean,
        data: NimeyaModel
    ): LiveData<Resource<NimeyaModel.NimeyaResponse>> {
        return homeRepository.getNimeyaUrl(isForceRefresh, data)
    }

    suspend fun invokeGetRiskoMeter(
        isForceRefresh: Boolean,
        data: GetRiskoMeterModel
    ): LiveData<Resource<GetRiskoMeterModel.RiskoMeterQuesResponse>> {
        return homeRepository.getRiskoMeter(isForceRefresh, data)
    }

    suspend fun invokeSaveRiskoMeter(
        isForceRefresh: Boolean,
        data: SaveRiskoMeterModel
    ): LiveData<Resource<SaveRiskoMeterModel.RiskoMeterSaveResponse>> {
        return homeRepository.saveRiskoMeter(isForceRefresh, data)
    }

    suspend fun invokeSaveProtectooMeter(
        isForceRefresh: Boolean,
        data: SaveProtectoMeterModel
    ): LiveData<Resource<SaveProtectoMeterModel.ProtectoMeterSaveResponse>> {
        return homeRepository.saveProtectoMeter(isForceRefresh, data)
    }

    suspend fun invokeSaveRetiroMeterApi(
        isForceRefresh: Boolean,
        data: SaveRetiroMeterModel
    ): LiveData<Resource<SaveRetiroMeterModel.SaveRetiroMeterResponse>> {
        return homeRepository.saveRetiroMeter(isForceRefresh, data)
    }

    suspend fun invokeGetRiskoMeterHistory(
        isForceRefresh: Boolean,
        data: GetRiskoMeterHistoryModel
    ): LiveData<Resource<GetRiskoMeterHistoryModel.RiskoMeterHistoryResponse>> {
        return homeRepository.getRiskoMeterHistory(isForceRefresh, data)
    }

    suspend fun invokeGetProtectoMeterHistory(
        isForceRefresh: Boolean,
        data: GetProtectoMeterHistoryModel
    ): LiveData<Resource<GetProtectoMeterHistoryModel.ProtectoMeterHistoryResponse>> {
        return homeRepository.getProtectoMeterHistory(isForceRefresh, data)
    }

    suspend fun invokeGetRetiroMeterHistory(
        isForceRefresh: Boolean,
        data: GetRetiroMeterHistoryModel
    ): LiveData<Resource<GetRetiroMeterHistoryModel.RetiroMeterHistoryResponse>> {
        return homeRepository.getRetiroMeterHistory(isForceRefresh, data)
    }

    suspend fun invokeWellfieSaveVitals(
        isForceRefresh: Boolean,
        data: WellfieSaveVitalsModel
    ): LiveData<Resource<WellfieSaveVitalsModel.WellfieSaveVitalsResponse>> {
        return homeRepository.wellfieSaveVitals(isForceRefresh, data)
    }

    suspend fun invokeWellfieGetVitals(
        isForceRefresh: Boolean,
        data: WellfieGetVitalsModel
    ): LiveData<Resource<WellfieGetVitalsModel.WellfieGetVitalsResponse>> {
        return homeRepository.wellfieGetVitals(isForceRefresh, data)
    }

    suspend fun invokeWellfieListVitals(
        isForceRefresh: Boolean,
        data: WellfieListVitalsModel
    ): LiveData<Resource<WellfieListVitalsModel.WellfieListVitalsResponse>> {
        return homeRepository.wellfieListVitals(isForceRefresh, data)
    }

    suspend fun invokeWellfieGetSSOUrl(
        isForceRefresh: Boolean,
        data: WellfieGetSSOUrlModel
    ): LiveData<Resource<WellfieGetSSOUrlModel.WellfieGetSSOUrlResponse>> {
        return homeRepository.wellfieGetSSOUrl(isForceRefresh, data)
    }

    suspend fun invokePersonDelete(
        isForceRefresh: Boolean,
        data: PersonDeleteModel
    ): LiveData<Resource<PersonDeleteModel.PersonDeleteResponse>> {
        return homeRepository.personDelete(isForceRefresh, data)
    }

    suspend fun invokeGetUserDetails(
        isForceRefresh: Boolean,
        data: UserDetailsModel
    ): LiveData<Resource<UserDetailsModel.UserDetailsResponse>> {
        return homeRepository.getUserDetails(isForceRefresh, data)
    }

    suspend fun invokeUpdateUserDetails(
        isForceRefresh: Boolean,
        data: UpdateUserDetailsModel
    ): LiveData<Resource<UpdateUserDetailsModel.UpdateUserDetailsResponse>> {
        return homeRepository.updateUserDetails(isForceRefresh, data)
    }

    suspend fun invokeGetProfileImage(
        isForceRefresh: Boolean,
        data: ProfileImageModel
    ): LiveData<Resource<ProfileImageModel.ProfileImageResponse>> {
        return homeRepository.getProfileImage(isForceRefresh, data)
    }

    suspend fun invokeUploadProfileImage(
        personID: RequestBody, fileName: RequestBody, documentTypeCode: RequestBody,
        byteArray: RequestBody, authTicket: RequestBody
    ): LiveData<Resource<UploadProfileImageResponce>> {
        return homeRepository.uploadProfileImage(
            personID,
            fileName,
            documentTypeCode,
            byteArray,
            authTicket
        )
    }


    suspend fun invokeRemoveProfileImage(
        isForceRefresh: Boolean,
        data: RemoveProfileImageModel
    ): LiveData<Resource<RemoveProfileImageModel.RemoveProfileImageResponse>> {
        return homeRepository.removeProfileImage(isForceRefresh, data)
    }

    suspend fun invokeRelativesList(
        isForceRefresh: Boolean,
        data: ListRelativesModel
    ): LiveData<Resource<ListRelativesModel.ListRelativesResponse>> {
        return homeRepository.fetchRelativesList(isForceRefresh, data)
    }

    suspend fun invokeaddNewRelative(
        isForceRefresh: Boolean,
        data: AddRelativeModel
    ): LiveData<Resource<AddRelativeModel.AddRelativeResponse>> {
        return homeRepository.addNewRelative(isForceRefresh, data)
    }

    suspend fun invokeupdateRelative(
        isForceRefresh: Boolean,
        data: UpdateRelativeModel,
        relativeId: String
    ): LiveData<Resource<UpdateRelativeModel.UpdateRelativeResponse>> {
        return homeRepository.updateRelative(isForceRefresh, data, relativeId)
    }

    suspend fun invokeRemoveRelative(
        isForceRefresh: Boolean,
        data: RemoveRelativeModel,
        relativeId: String
    ): LiveData<Resource<RemoveRelativeModel.RemoveRelativeResponse>> {
        return homeRepository.removeRelative(isForceRefresh, data, relativeId)
    }

    suspend fun invokeDoctorsList(
        isForceRefresh: Boolean,
        data: FamilyDoctorsListModel
    ): LiveData<Resource<FamilyDoctorsListModel.FamilyDoctorsResponse>> {
        return homeRepository.fetchDoctorsList(isForceRefresh, data)
    }

    suspend fun invokeSpecialitiesList(
        isForceRefresh: Boolean,
        data: ListDoctorSpecialityModel
    ): LiveData<Resource<ListDoctorSpecialityModel.ListDoctorSpecialityResponse>> {
        return homeRepository.fetchSpecialityList(isForceRefresh, data)
    }

    suspend fun invokeAddDoctor(
        isForceRefresh: Boolean,
        data: FamilyDoctorAddModel
    ): LiveData<Resource<FamilyDoctorAddModel.FamilyDoctorAddResponse>> {
        return homeRepository.addDoctor(isForceRefresh, data)
    }

    suspend fun invokeUpdateDoctor(
        isForceRefresh: Boolean,
        data: FamilyDoctorUpdateModel
    ): LiveData<Resource<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse>> {
        return homeRepository.updateDoctor(isForceRefresh, data)
    }

    suspend fun invokeRemoveDoctor(
        isForceRefresh: Boolean,
        data: RemoveDoctorModel
    ): LiveData<Resource<RemoveDoctorModel.RemoveDoctorResponse>> {
        return homeRepository.removeDoctor(isForceRefresh, data)
    }

    suspend fun invokePhoneExist(
        isForceRefresh: Boolean,
        data: PhoneExistsModel
    ): LiveData<Resource<PhoneExistsModel.IsExistResponse>> {
        return userRepository.isPhoneExist(isForceRefresh, data)
    }

    suspend fun invokeGetLoggedInPersonDetails(): Users {
        return homeRepository.getLoggedInPersonDetails()
    }

    suspend fun invokeUpdateUserDetails(name: String, dob: String, personId: Int) {
        return homeRepository.updateUserDetails(name, dob, personId)
    }

    suspend fun invokeUpdateUserProfileImgPath(name: String, path: String) {
        return homeRepository.updateUserProfileImgPath(name, path)
    }

    suspend fun invokeGetUserRelatives(): List<UserRelatives> {
        return homeRepository.getUserRelatives()
    }

    suspend fun invokeGetAllHealthDocuments(): List<HealthDocument> {
        return healthRecordRepository.getAllHealthDocuments()
    }

    suspend fun invokeGetUserRelativesExceptSelf(): List<UserRelatives> {
        return homeRepository.getUserRelativesExceptSelf()
    }

    suspend fun invokeGetUserRelativeSpecific(relationShipCode: String): List<UserRelatives> {
        return homeRepository.getUserRelativeSpecific(relationShipCode)
    }

    suspend fun invokeGetUserRelativeForRelativeId(relativeId: String): List<UserRelatives> {
        return homeRepository.getUserRelativeForRelativeId(relativeId)
    }

    suspend fun invokeGetHraSummaryDetails(): HRASummary {
        return hraRepository.getHraSummaryDetails()
    }

    suspend fun invokeClearTablesForSwitchProfile() {
        homeRepository.clearTablesForSwitchProfile()
    }

    suspend fun invokeGetUserRelativeDetailsByRelativeId(relativeId: String): UserRelatives {
        return homeRepository.getUserRelativeDetailsByRelativeId(relativeId)
    }

    suspend fun invokeDownloadBlog(
        isForceRefresh: Boolean,
        data: BlogsListAllModel
    ): LiveData<Resource<BlogsListAllModel.BlogsListAllResponse>> {

        return blogRepository.downloadBlogs(isForceRefresh, data)
    }

    suspend fun invokeDocumentList(
        isForceRefresh: Boolean,
        data: ListDocumentsModel
    ): LiveData<Resource<ListDocumentsModel.ListDocumentsResponse>> {
        return healthRecordRepository.fetchDocumentList(isForceRefresh, data)
    }

    suspend fun invokeUpdateLanguageSettings(
        isForceRefresh: Boolean,
        data: UpdateLanguageProfileModel
    ): LiveData<Resource<UpdateLanguageProfileModel.UpdateLanguageProfileResponse>> {
        return homeRepository.fetchUpdateProfileResponse(data)
    }

    suspend fun invokeRefreshTokenResponse(
        isForceRefresh: Boolean,
        data: RefreshTokenModel
    ): LiveData<Resource<RefreshTokenModel.RefreshTokenResponse>> {
        return homeRepository.fetchRefreshTokenResponse(data)
    }

    suspend fun invokeBlogsListByCategory(
        isForceRefresh: Boolean,
        data: BlogsListByCategoryModel
    ): LiveData<Resource<BlogsListByCategoryModel.BlogsCategoryResponse>> {
        return blogRepository.blogsListByCategory(isForceRefresh, data)
    }

    suspend fun invokeBlogsListRelatedTo(
        isForceRefresh: Boolean,
        data: BlogRecommendationListModel
    ): LiveData<Resource<BlogRecommendationListModel.BlogsResponse>> {

        return blogRepository.blogsRelatedTo(isForceRefresh, data)
    }

    suspend fun invokeFetchStepsGoal(data: GetStepsGoalModel): LiveData<Resource<GetStepsGoalModel.Response>> {
        return fitnessRepository.fetchLatestGoal(data = data)
    }

    suspend fun invokeGetDailyWaterIntake(
        isForceRefresh: Boolean,
        data: GetDailyWaterIntakeModel
    ): LiveData<Resource<GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse>> {

        return waterTrackerRepository.getDailyWaterIntake(data)
    }

}