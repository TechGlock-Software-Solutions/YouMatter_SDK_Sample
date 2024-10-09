package com.techglock.health.app.remote

import com.techglock.health.app.di.DIModule
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
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Named

class HomeDatasource @Inject constructor(
    @Named(DIModule.DEFAULT) private val defaultService: ApiService,
    @Named(DIModule.ENCRYPTED) private val encryptedUserService: ApiService
) {


    suspend fun getUserDetailsResponse(data: UserDetailsModel) =
        encryptedUserService.getUserDetailsApi(data)

    suspend fun updateUserDetailsResponse(data: UpdateUserDetailsModel) =
        encryptedUserService.updateUserDetailsApi(data)

    suspend fun getProfileImageResponse(data: ProfileImageModel) =
        encryptedUserService.getProfileImageApi(data)

    suspend fun uploadProfileImageResponce(
        personID: RequestBody, fileName: RequestBody, documentTypeCode: RequestBody,
        byteArray: RequestBody, authTicket: RequestBody
    ) = defaultService.uploadProfileImage(
        personID,
        fileName,
        documentTypeCode,
        byteArray,
        authTicket
    )

    suspend fun removeProfileImageResponse(data: RemoveProfileImageModel) =
        encryptedUserService.removeProfileImageApi(data)

    suspend fun getRelativesListResponse(data: ListRelativesModel) =
        encryptedUserService.fetchRelativesListApi(data)

    suspend fun addRelativeResponse(data: AddRelativeModel) =
        encryptedUserService.addRelativeApi(data)

    suspend fun updateRelativeResponse(data: UpdateRelativeModel) =
        encryptedUserService.updateRelativeApi(data)

    suspend fun removeRelativeResponse(data: RemoveRelativeModel) =
        encryptedUserService.removeRelativeApi(data)

    suspend fun getDoctorsListResponse(data: FamilyDoctorsListModel) =
        encryptedUserService.getFamilyDoctorsListApi(data)

    suspend fun getSpecialityListResponse(data: ListDoctorSpecialityModel) =
        encryptedUserService.getSpecialityListApi(data)

    suspend fun addDoctorResponse(data: FamilyDoctorAddModel) =
        encryptedUserService.addDoctorApi(data)

    suspend fun updateDoctorResponse(data: FamilyDoctorUpdateModel) =
        encryptedUserService.updateDoctorApi(data)

    suspend fun removeDoctorResponse(data: RemoveDoctorModel) =
        encryptedUserService.removeDoctorApi(data)

    suspend fun contactUs(data: ContactUsModel) = encryptedUserService.contactUsApi(data)

    suspend fun saveFeedbackResponse(data: SaveFeedbackModel) =
        encryptedUserService.saveFeedbackApi(data)

    suspend fun passwordChangeResponse(data: PasswordChangeModel) =
        encryptedUserService.passwordChangeApi(data)

    suspend fun checkAppUpdateResponse(data: CheckAppUpdateModel) =
        encryptedUserService.checkAppUpdateApi(data)

    suspend fun saveCloudMessagingId(data: SaveCloudMessagingIdModel) =
        encryptedUserService.saveCloudMessagingId(data)

    suspend fun updateLanguagePreferences(data: UpdateLanguageProfileModel) =
        encryptedUserService.updateLanguagePreference(data)

    suspend fun fetchRefreshToken(data: RefreshTokenModel) =
        encryptedUserService.getRefreshToken(data)

    suspend fun addFeatureAccessLogResponse(data: AddFeatureAccessLog) =
        encryptedUserService.addFeatureAccessLogApi(data)

    suspend fun getSSOUrlResponse(data: GetSSOUrlModel) = encryptedUserService.getSSOUrlApi(data)

    suspend fun getNimeyaUrlResponse(data: NimeyaModel) = encryptedUserService.getNimeyaUrlApi(data)

    suspend fun getRiskoMeterResponse(data: GetRiskoMeterModel) =
        encryptedUserService.getRiskoMeterApi(data)

    suspend fun saveRiskoMeterResponse(data: SaveRiskoMeterModel) =
        encryptedUserService.saveRiskoMeterApi(data)

    suspend fun saveProtectoMeterResponse(data: SaveProtectoMeterModel) =
        encryptedUserService.getProtectoMeterApi(data)

    suspend fun saveRetiroMeterResponse(data: SaveRetiroMeterModel) =
        encryptedUserService.saveRetiroMeterApi(data)

    suspend fun getRiskoMeterHistoryResponse(data: GetRiskoMeterHistoryModel) =
        encryptedUserService.getRiskoMeterHistoryApi(data)

    suspend fun getProtectoMeterHistoryResponse(data: GetProtectoMeterHistoryModel) =
        encryptedUserService.getProtectoMeterHistoryApi(data)

    suspend fun getRetiroMeterHistoryResponse(data: GetRetiroMeterHistoryModel) =
        encryptedUserService.getRetiroMeterHistoryApi(data)

    suspend fun personDeleteResponse(data: PersonDeleteModel) =
        encryptedUserService.personDeleteApi(data)

    suspend fun wellfieSaveVitalsResponse(data: WellfieSaveVitalsModel) =
        encryptedUserService.wellfieSaveVitalsApi(data)

    suspend fun wellfieGetVitalsResponse(data: WellfieGetVitalsModel) =
        encryptedUserService.wellfieGetVitalsApi(data)

    suspend fun wellfieListVitalsResponse(data: WellfieListVitalsModel) =
        encryptedUserService.wellfieListVitalsApi(data)

    suspend fun wellfieGetSSOUrlResponse(data: WellfieGetSSOUrlModel) =
        encryptedUserService.wellfieGetSSOUrlApi(data)

    suspend fun eventsBannerResponse(data: EventsBannerModel) =
        encryptedUserService.eventsBannerApi(data)
}
