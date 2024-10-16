package com.techglock.health.app.remote

import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.model.ApiResponse
import com.techglock.health.app.model.BaseResponse
import com.techglock.health.app.model.aktivo.AktivoCheckUserModel
import com.techglock.health.app.model.aktivo.AktivoCreateUserModel
import com.techglock.health.app.model.aktivo.AktivoGetRefreshTokenModel
import com.techglock.health.app.model.aktivo.AktivoGetUserModel
import com.techglock.health.app.model.aktivo.AktivoGetUserTokenModel
import com.techglock.health.app.model.blogs.*
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.model.entity.Users
import com.techglock.health.app.model.fitness.*
import com.techglock.health.app.model.home.*
import com.techglock.health.app.model.hra.*
import com.techglock.health.app.model.medication.*
import com.techglock.health.app.model.nimeya.GetProtectoMeterHistoryModel
import com.techglock.health.app.model.nimeya.GetRetiroMeterHistoryModel
import com.techglock.health.app.model.nimeya.GetRiskoMeterHistoryModel
import com.techglock.health.app.model.nimeya.GetRiskoMeterModel
import com.techglock.health.app.model.nimeya.SaveProtectoMeterModel
import com.techglock.health.app.model.nimeya.SaveRetiroMeterModel
import com.techglock.health.app.model.nimeya.SaveRiskoMeterModel
import com.techglock.health.app.model.parameter.*
import com.techglock.health.app.model.security.*
import com.techglock.health.app.model.shr.*
import com.techglock.health.app.model.shr.ListRelativesModel
import com.techglock.health.app.model.sudLifePolicy.*
import com.techglock.health.app.model.toolscalculators.*
import com.techglock.health.app.model.waterTracker.*
import okhttp3.RequestBody
import retrofit2.http.*
import retrofit2.http.Body

interface ApiService {

    /**
     * Security Module Api Calls
     */
    @POST(Constants.strProxySsoUrl)
    suspend fun ssoProxyAPI(@Query("Data") loginEncryptedQuery: String): ApiResponse<Users>

    @POST(Constants.strProxyLoginUrl)
    suspend fun loginDocumentProxyAPI(@Query("Data") loginEncryptedQuery: String): ApiResponse<Users>

    @POST(Constants.strProxyRegistrationUrl)
    suspend fun registerDocumentProxyAPI(@Query("Data") registerEncryptedQuery: String): ApiResponse<Users>

    // **********New API**************
    @POST(Constants.strCheckIfLoginNameExists)
    suspend fun checkLoginNameExistsAPI(@Body loginNameExistsModel: LoginNameExistsModel): BaseResponse<LoginNameExistsModel.IsExistResponse>

    @POST(Constants.strUpdateProfileSetting)
    suspend fun updateLanguagePreference(@Body data: UpdateLanguageProfileModel): BaseResponse<UpdateLanguageProfileModel.UpdateLanguageProfileResponse>

    @POST(Constants.strRefreshToken)
    suspend fun getRefreshToken(@Body data: RefreshTokenModel): BaseResponse<RefreshTokenModel.RefreshTokenResponse>
    // **********New API**************

    @POST(Constants.strCheckIfEmailExists)
    suspend fun checkEmailExistsAPI(@Body emailOrPhoneExistsModel: EmailExistsModel): BaseResponse<EmailExistsModel.IsExistResponse>

    @POST(Constants.strCheckIfPrimaryPhoneExists)
    suspend fun checkPhoneExistsAPI(@Body phoneExistsModel: PhoneExistsModel): BaseResponse<PhoneExistsModel.IsExistResponse>

    @POST(Constants.strGetTermsAndConditionsForPartner)
    suspend fun getTermsAndConditionsForPartnerAPI(@Body termsConditionsModel: TermsConditionsModel): BaseResponse<TermsConditionsModel.TermsConditionsResponse>

    @POST(Constants.strOtpGenerate)
    suspend fun generateOTPforUserAPI(@Body generateOtpModel: GenerateOtpModel): BaseResponse<GenerateOtpModel.GenerateOTPResponse>

    @POST(Constants.strOtpValidate)
    suspend fun validateOTPforUserAPI(@Body generateOtpModel: GenerateOtpModel): BaseResponse<GenerateOtpModel.GenerateOTPResponse>

    @POST(Constants.strUpdateNewPassword)
    suspend fun updatePasswordAPI(@Body changePasswordModel: ChangePasswordModel): BaseResponse<ChangePasswordModel.ChangePasswordResponse>

    @POST(Constants.strDarwinBoxDataUrl)
    suspend fun darwinBoxDataAPI(@Body darwinBoxDataModel: DarwinBoxDataModel): BaseResponse<DarwinBoxDataModel.DarwinBoxDataResponse>

    @POST(Constants.SAVE_CLOUD_MESSAGING_ID_API)
    suspend fun saveCloudMessagingId(@Body saveCloudMessagingIdModel: SaveCloudMessagingIdModel): BaseResponse<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>


    /**
     * Medication Module Api Calls
     */
    @POST(Constants.MEDICATION_GET_API)
    suspend fun getMedicine(@Body getMedicine: GetMedicineModel): BaseResponse<GetMedicineModel.GetMedicineResponse>

    @POST(Constants.MEDICATION_DRUGS_LIST_API)
    suspend fun fetchDrugsList(@Body drugsModel: DrugsModel): BaseResponse<DrugsModel.DrugsResponse>

    @POST(Constants.MEDICATION_LIST_BY_DATE_API)
    suspend fun fetchMedicationList(@Body medicationList: MedicationListModel): BaseResponse<MedicationListModel.Response>

    @POST(Constants.MEDICATION_LIST_BY_DAY_API)
    suspend fun fetchMedicationListByDay(@Body request: MedicineListByDayModel): BaseResponse<MedicineListByDayModel.MedicineListByDayResponse>

    @POST(Constants.MEDICATION_SAVE_API)
    suspend fun saveMedicine(@Body request: AddMedicineModel): BaseResponse<AddMedicineModel.AddMedicineResponse>

    @POST(Constants.MEDICATION_UPDATE_API)
    suspend fun UpdateMedicine(@Body request: UpdateMedicineModel): BaseResponse<UpdateMedicineModel.UpdateMedicineResponse>

    @POST(Constants.MEDICATION_SET_ALERT_API)
    suspend fun setAlert(@Body request: SetAlertModel): BaseResponse<SetAlertModel.SetAlertResponse>

    @POST(Constants.MEDICATION_ADD_IN_TAKE_API)
    suspend fun addInTake(@Body request: AddInTakeModel): BaseResponse<AddInTakeModel.AddInTakeResponse>

    @POST(Constants.MEDICATION_DELETE_TAKE_API)
    suspend fun deleteMedicine(@Body updateMedicine: DeleteMedicineModel): BaseResponse<DeleteMedicineModel.DeleteMedicineResponse>

    @POST(Constants.MEDICATION_GET_MEDICINE_IN_TAKE_API)
    suspend fun fetchMedicationInTakeByScheduleID(@Body medicineDetails: MedicineInTakeModel): BaseResponse<MedicineInTakeModel.MedicineDetailsResponse>

    /**
     * Track Parameter Module Api Calls
     */
    @POST(Constants.TRACK_PARAM_LIST_MASTER)
    suspend fun fetchParamList(@Body paramList: ParameterListModel): BaseResponse<ParameterListModel.Response>

    @POST(Constants.TRACK_PARAM_LABRECORD_LIST_HISTORY_BY_PROFILE_CODES)
    suspend fun getLabRecordList(@Body recordList: LabRecordsListModel): BaseResponse<TrackParameterMaster.HistoryResponse>

    @POST(Constants.TRACK_PARAM_BMI_LIST_VITALS_HISTORY)
    suspend fun getLabRecordListVitals(@Body recordList: VitalsHistoryModel): BaseResponse<VitalsHistoryModel.Response>

    @POST(Constants.TRACK_PARAM_BMI_LIST_HISTORY)
    suspend fun getBMIHistory(@Body request: BMIHistoryModel): BaseResponse<BMIHistoryModel.Response>

    @POST(Constants.TRACK_PARAM_WHR_LIST_HISTORY)
    suspend fun getWHRHistory(@Body request: WHRHistoryModel): BaseResponse<WHRHistoryModel.Response>

    @POST(Constants.TRACK_PARAM_BLOOD_PRESSURE_LISTHISTORY)
    suspend fun getBloodPressureHistory(@Body request: BloodPressureHistoryModel): BaseResponse<BloodPressureHistoryModel.Response>

    @POST(Constants.TRACK_PARAM_LAB_RECORD_SYNCHRONIZE)
    suspend fun saveLabRecords(@Body data: SaveParameterModel): BaseResponse<SaveParameterModel.Response>

    @POST(Constants.TRACK_PARAM_WHR_SYNCHRONIZE)
    suspend fun saveWhrRecord()

    @POST(Constants.TRACK_PARAM_BMI_SYNCHRONIZE)
    suspend fun saveBmiRecord()

    @POST(Constants.TRACK_PARAM_LIST_PARAMETER_TRACKING_PREFERENCES)
    suspend fun getParameterPreferences(@Body request: ParameterPreferenceModel): BaseResponse<ParameterPreferenceModel.Response>

    @POST(Constants.TRACK_PARAM_SAVE_PARAMETER_TRACKING_PREFERENCE)
    suspend fun saveParameterTrackingPreferences()

    /**
     * Fitness Lib Api Calls
     */
    @POST(Constants.FITNESS_LIST_HISTORY_API)
    suspend fun fetchStepsListHistory(@Body request: StepsHistoryModel): BaseResponse<StepsHistoryModel.Response>

    @POST(Constants.FITNESS_GET_LATEST_GOAL_API)
    suspend fun fetchLatestGoal(@Body request: GetStepsGoalModel): BaseResponse<GetStepsGoalModel.Response>

    @POST(Constants.FITNESS_SET_GOAL_API)
    suspend fun saveStepsGoal(@Body request: SetGoalModel): BaseResponse<SetGoalModel.Response>

    @POST(Constants.FITNESS_STEP_SAVE_LIST_API)
    suspend fun saveStepsList(@Body request: StepsSaveListModel): BaseResponse<StepsSaveListModel.StepsSaveListResponse>

    @POST(Constants.FITNESS_STEP_SAVE_API)
    suspend fun saveStepsData(@Body request: FitnessModel): BaseResponse<FitnessModel.Response>


    /**
     * HRA Module Api Calls
     */
    @POST(Constants.HRA_START_API)
    suspend fun hraStartAPI(@Body hraStartModel: HraStartModel): BaseResponse<HraStartModel.HraStartResponse>

    @POST(Constants.BMI_EXIST_API)
    suspend fun checkBMIExistAPI(@Body bmiExistModel: BMIExistModel): BaseResponse<BMIExistModel.BMIExistResponse>

    @POST(Constants.BP_EXIST_API)
    suspend fun checkBPExistAPI(@Body bpExistModel: BPExistModel): BaseResponse<BPExistModel.BPExistResponse>

    @POST(Constants.LAB_RECORDS_API)
    suspend fun fetchLabRecordsAPI(@Body labRecordsModel: LabRecordsModel): BaseResponse<LabRecordsModel.LabRecordsExistResponse>

    @POST(Constants.SAVE_AND_SUBMIT_HRA_API)
    suspend fun saveAndSubmitHraAPI(@Body saveAndSubmitHraModel: SaveAndSubmitHraModel): BaseResponse<SaveAndSubmitHraModel.SaveAndSubmitHraResponse>

    @POST(Constants.MEDICAL_PROFILE_SUMMARY_API)
    suspend fun getMedicalProfileSummaryAPI(@Body bmiExistModel: HraMedicalProfileSummaryModel): BaseResponse<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>

    @POST(Constants.ASSESSMENT_SUMMARY_API)
    suspend fun getAssessmentSummaryAPI(@Body hraAssessmentSummaryModel: HraAssessmentSummaryModel): BaseResponse<HraAssessmentSummaryModel.AssessmentSummaryResponce>

    @POST(Constants.GET_HRA_SUMMARY_API)
    suspend fun getHRAHistory(@Body hraHistoryModel: HraHistoryModel): BaseResponse<HraHistoryModel.HRAHistoryResponse>

    @POST(Constants.LIST_RECOMMENDED_TESTS_API)
    suspend fun getListRecommendedTestsAPI(@Body listRecommendedTestsModel: HraListRecommendedTestsModel): BaseResponse<HraListRecommendedTestsModel.ListRecommendedTestsResponce>


    /**
     * SHR Module Api Calls
     */
    @POST(Constants.SHR_DOCUMENT_TYPE_API)
    suspend fun fetchDocumentTypeApi(@Body request: ListDocumentTypesModel): BaseResponse<ListDocumentTypesModel.ListDocumentTypesResponse>

    @POST(Constants.SHR_DOCUMENT_LIST_API)
    suspend fun fetchDocumentListApi(@Body request: ListDocumentsModel): BaseResponse<ListDocumentsModel.ListDocumentsResponse>

    @POST(Constants.SHR_DOCUMENT_SAVE_API)
    suspend fun saveRecordToServerApi(@Body request: SaveDocumentModel): BaseResponse<SaveDocumentModel.SaveDocumentsResponse>

    @POST(Constants.SHR_DOCUMENT_DELETE_API)
    suspend fun deleteRecordsFromServerApi(@Body request: DeleteDocumentModel): BaseResponse<DeleteDocumentModel.DeleteDocumentResponse>

    @POST(Constants.SHR_DOCUMENT_DOWNLOAD_API)
    suspend fun downloadDocumentFromServerApi(@Body request: DownloadDocumentModel): BaseResponse<DownloadDocumentModel.DownloadDocumentResponse>

    @POST(Constants.SHR_OCR_UNIT_EXIST)
    suspend fun checkUnitExist(@Body request: OCRUnitExistModel): BaseResponse<OCRUnitExistModel.OCRUnitExistResponse>

    @POST(Constants.SHR_OCR_DOCUMENT_SAVE)
    suspend fun ocrSaveDocument(@Body request: OCRSaveModel): BaseResponse<OCRSaveModel.OCRSaveResponse>

    @Multipart
    @POST(Constants.SHR_OCR_EXTRACT_DOCUMENT)
    suspend fun ocrDigitizeDocument(
        @Part("FileBytes") fileBytes: RequestBody,
        @Part("PartnerCode") partnerCode: RequestBody,
        @Part("FileExtension") fileExtension: RequestBody
    ): BaseResponse<OcrResponce>


    /**
     * Tools Calculators Module Api Calls
     */
    @POST(Constants.TOOLS_START_QUIZ_API)
    suspend fun toolsStartQuizApi(@Body request: StartQuizModel): BaseResponse<StartQuizModel.StartQuizResponse>

    @POST(Constants.TOOLS_HEART_AGE_API)
    suspend fun toolsHeartAgeSaveResponceApi(@Body request: HeartAgeSaveResponceModel): BaseResponse<HeartAgeSaveResponceModel.HeartAgeSaveResponce>

    @POST(Constants.TOOLS_DIABETES_API)
    suspend fun toolsDiabetesSaveResponceApi(@Body request: DiabetesSaveResponceModel): BaseResponse<DiabetesSaveResponceModel.DiabetesSaveResponce>

    @POST(Constants.TOOLS_HYPERTENSION_API)
    suspend fun toolsHypertensionSaveResponceApi(@Body request: HypertensionSaveResponceModel): BaseResponse<HypertensionSaveResponceModel.HypertensionSaveResponce>

    @POST(Constants.TOOLS_STRESS_ANXIETY_API)
    suspend fun toolsStressAndAnxietySaveResponceApi(@Body request: StressAndAnxietySaveResponceModel): BaseResponse<StressAndAnxietySaveResponceModel.StressAndAnxietySaveResponse>

    @POST(Constants.TOOLS_SMART_PHONE_ADDICTION_API)
    suspend fun toolsSmartPhoneSaveResponceApi(@Body request: SmartPhoneSaveResponceModel): BaseResponse<SmartPhoneSaveResponceModel.SmartPhoneSaveResponce>


    /**
     * Family Member Api Calls
     */
    @POST(Constants.RELATIVES_LIST_API)
    suspend fun fetchRelativesListApi(@Body request: ListRelativesModel): BaseResponse<ListRelativesModel.ListRelativesResponse>

    @POST(Constants.ADD_RELATIVE_API)
    suspend fun addRelativeApi(@Body request: AddRelativeModel): BaseResponse<AddRelativeModel.AddRelativeResponse>

    @POST(Constants.UPDATE_RELATIVE_API)
    suspend fun updateRelativeApi(@Body request: UpdateRelativeModel): BaseResponse<UpdateRelativeModel.UpdateRelativeResponse>

    @POST(Constants.REMOVE_RELATIVE_API)
    suspend fun removeRelativeApi(@Body request: RemoveRelativeModel): BaseResponse<RemoveRelativeModel.RemoveRelativeResponse>


    /**
     * User Api Calls
     */
    @POST(Constants.GET_USER_DETAIL_API)
    suspend fun getUserDetailsApi(@Body request: UserDetailsModel): BaseResponse<UserDetailsModel.UserDetailsResponse>

    @POST(Constants.UPDATE_PERSONAL_DETAIL_API)
    suspend fun updateUserDetailsApi(@Body request: UpdateUserDetailsModel): BaseResponse<UpdateUserDetailsModel.UpdateUserDetailsResponse>

    @POST(Constants.SHR_DOCUMENT_DOWNLOAD_API)
    suspend fun getProfileImageApi(@Body request: ProfileImageModel): BaseResponse<ProfileImageModel.ProfileImageResponse>

    @Multipart
    @POST(Constants.UPLOAD_PROFILE_IMAGE_API)
    suspend fun uploadProfileImage(
        @Part("PersonID") personID: RequestBody,
        @Part("FileName") fileName: RequestBody,
        @Part("DocumentTypeCode") documentTypeCode: RequestBody,
        @Part("ByteArray") byteArray: RequestBody,
        @Part("AuthTicket") authTicket: RequestBody
    ): BaseResponse<UploadProfileImageResponce>

    @POST(Constants.REMOVE_PROFILE_IMAGE_API)
    suspend fun removeProfileImageApi(@Body request: RemoveProfileImageModel): BaseResponse<RemoveProfileImageModel.RemoveProfileImageResponse>


    /**
     * Family Member Api Calls
     */
    @POST(Constants.DOCTORS_LIST_API)
    suspend fun getFamilyDoctorsListApi(@Body request: FamilyDoctorsListModel): BaseResponse<FamilyDoctorsListModel.FamilyDoctorsResponse>

    @POST(Constants.DOCTORS_LIST_SPECIALITY_API)
    suspend fun getSpecialityListApi(@Body request: ListDoctorSpecialityModel): BaseResponse<ListDoctorSpecialityModel.ListDoctorSpecialityResponse>

    @POST(Constants.ADD_DOCTOR_API)
    suspend fun addDoctorApi(@Body request: FamilyDoctorAddModel): BaseResponse<FamilyDoctorAddModel.FamilyDoctorAddResponse>

    @POST(Constants.UPDATE_DOCTOR_API)
    suspend fun updateDoctorApi(@Body request: FamilyDoctorUpdateModel): BaseResponse<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse>

    @POST(Constants.REMOVE_DOCTOR_API)
    suspend fun removeDoctorApi(@Body request: RemoveDoctorModel): BaseResponse<RemoveDoctorModel.RemoveDoctorResponse>


    @POST(Constants.CONTACT_US_API)
    suspend fun contactUsApi(@Body request: ContactUsModel): BaseResponse<ContactUsModel.ContactUsResponse>

    @POST(Constants.SAVE_FEEDBACK_API)
    suspend fun saveFeedbackApi(@Body request: SaveFeedbackModel): BaseResponse<SaveFeedbackModel.SaveFeedbackResponse>

    @POST(Constants.PASSWORD_CHANGE_API)
    suspend fun passwordChangeApi(@Body request: PasswordChangeModel): BaseResponse<PasswordChangeModel.ChangePasswordResponse>

    @POST(Constants.CHECK_APP_UPDATE_API)
    suspend fun checkAppUpdateApi(@Body request: CheckAppUpdateModel): BaseResponse<CheckAppUpdateModel.CheckAppUpdateResponse>

    @POST(Constants.ADD_FEATURE_ACCESS_LOG_API)
    suspend fun addFeatureAccessLogApi(@Body request: AddFeatureAccessLog): BaseResponse<AddFeatureAccessLog.AddFeatureAccessLogResponse>

    @POST(Constants.AMAHA_SSO_URL_API)
    suspend fun getSSOUrlApi(@Body request: GetSSOUrlModel): BaseResponse<GetSSOUrlModel.GetSSOUrlResponse>

    @POST(Constants.NIMEYA_URL_API)
    suspend fun getNimeyaUrlApi(@Body request: NimeyaModel): BaseResponse<NimeyaModel.NimeyaResponse>

    @POST(Constants.NIMEYA_GET_RISKO_METER_API)
    suspend fun getRiskoMeterApi(@Body request: GetRiskoMeterModel): BaseResponse<GetRiskoMeterModel.RiskoMeterQuesResponse>

    @POST(Constants.NIMEYA_SAVE_RISKO_METER_API)
    suspend fun saveRiskoMeterApi(@Body request: SaveRiskoMeterModel): BaseResponse<SaveRiskoMeterModel.RiskoMeterSaveResponse>

    @POST(Constants.NIMEYA_SAVE_PROTECTO_METER_API)
    suspend fun getProtectoMeterApi(@Body request: SaveProtectoMeterModel): BaseResponse<SaveProtectoMeterModel.ProtectoMeterSaveResponse>

    @POST(Constants.NIMEYA_SAVE_RETIRO_METER_API)
    suspend fun saveRetiroMeterApi(@Body request: SaveRetiroMeterModel): BaseResponse<SaveRetiroMeterModel.SaveRetiroMeterResponse>

    @POST(Constants.NIMEYA_GET_RISKO_METER_HISTORY_API)
    suspend fun getRiskoMeterHistoryApi(@Body request: GetRiskoMeterHistoryModel): BaseResponse<GetRiskoMeterHistoryModel.RiskoMeterHistoryResponse>

    @POST(Constants.NIMEYA_GET_PROTECTO_METER_HISTORY_API)
    suspend fun getProtectoMeterHistoryApi(@Body request: GetProtectoMeterHistoryModel): BaseResponse<GetProtectoMeterHistoryModel.ProtectoMeterHistoryResponse>

    @POST(Constants.NIMEYA_GET_RETIRO_METER_HISTORY_API)
    suspend fun getRetiroMeterHistoryApi(@Body request: GetRetiroMeterHistoryModel): BaseResponse<GetRetiroMeterHistoryModel.RetiroMeterHistoryResponse>

    @POST(Constants.PERSON_DELETE_API)
    suspend fun personDeleteApi(@Body request: PersonDeleteModel): BaseResponse<PersonDeleteModel.PersonDeleteResponse>

    @POST(Constants.WELLFIE_SAVE_VITALS_API)
    suspend fun wellfieSaveVitalsApi(@Body request: WellfieSaveVitalsModel): BaseResponse<WellfieSaveVitalsModel.WellfieSaveVitalsResponse>

    @POST(Constants.WELLFIE_GET_VITALS_API)
    suspend fun wellfieGetVitalsApi(@Body request: WellfieGetVitalsModel): BaseResponse<WellfieGetVitalsModel.WellfieGetVitalsResponse>

    @POST(Constants.WELLFIE_LIST_VITALS_API)
    suspend fun wellfieListVitalsApi(@Body request: WellfieListVitalsModel): BaseResponse<WellfieListVitalsModel.WellfieListVitalsResponse>

    @POST(Constants.WELLFIE_GET_SSO_URL_API)
    suspend fun wellfieGetSSOUrlApi(@Body request: WellfieGetSSOUrlModel): BaseResponse<WellfieGetSSOUrlModel.WellfieGetSSOUrlResponse>

    /**
     * Blogs Api Calls
     */
    /*    @GET
        suspend fun downloadBlogs( @Url url: String): BlogModel.BlogResponce>*/
    @POST(Constants.strBlogsListAll)
    suspend fun downloadBlogs(@Body request: BlogsListAllModel): BlogsListAllModel.BlogsListAllResponse

    @POST(Constants.strBlogsListBySearchTerm)
    suspend fun searchBlogs(@Body request: BlogsListBySearchModel): BlogsListBySearchModel.BlogsListBySearchResponse

    @POST(Constants.strBlogsCategoryList)
    suspend fun blogsCategoryList(@Body request: BlogsCategoryModel): BlogsCategoryModel.BlogsCategoryResponse

    @POST(Constants.strBlogsListSuggestion)
    suspend fun blogsListSuggestion(@Body request: BlogRecommendationListModel): BlogRecommendationListModel.BlogsResponse

    @POST(Constants.strBlogsRelatedTo)
    suspend fun blogsRelatedToList(@Body request: BlogRecommendationListModel): BlogRecommendationListModel.BlogsResponse

    @POST(Constants.strBlogsListByCategory)
    suspend fun blogsListByCategory(@Body request: BlogsListByCategoryModel): BlogsListByCategoryModel.BlogsCategoryResponse

    /**
     * Water Tracker Api Calls
     */
    @POST(Constants.SAVE_WATER_INTAKE_GOAL_API)
    suspend fun saveWaterIntakeGoalApi(@Body data: SaveWaterIntakeGoalModel): BaseResponse<SaveWaterIntakeGoalModel.SaveWaterIntakeGoalResponse>

    @POST(Constants.SAVE_DAILY_WATER_INTAKE_API)
    suspend fun saveDailyWaterIntakeApi(@Body data: SaveDailyWaterIntakeModel): BaseResponse<SaveDailyWaterIntakeModel.SaveDailyWaterIntakeResponse>

    @POST(Constants.GET_DAILY_WATER_INTAKE_API)
    suspend fun getDailyWaterIntakeApi(@Body data: GetDailyWaterIntakeModel): BaseResponse<GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse>

    @POST(Constants.GET_WATER_INTAKE_HISTORY_API)
    suspend fun getWaterIntakeHistoryByDate(@Body data: GetWaterIntakeHistoryByDateModel): BaseResponse<GetWaterIntakeHistoryByDateModel.GetWaterIntakeHistoryByDateResponse>

    @POST(Constants.GET_WATER_INTAKE_SUMMARY_API)
    suspend fun getWaterIntakeSummary(@Body data: GetWaterIntakeSummaryModel): BaseResponse<GetWaterIntakeSummaryModel.GetWaterIntakeSummaryResponse>

    /**
     * Customer Service Api
     */
    @POST(Constants.SUD_CUSTOMER_POLICY_SERVICE_API)
    suspend fun getSudPolicyByMobileNumber(@Body request: SudPolicyByMobileNumberModel): SudPolicyByMobileNumberModel.SudPolicyByMobileNumberResponse

    @POST(Constants.SUD_CUSTOMER_POLICY_SERVICE_API)
    suspend fun getPolicyDetailsByPolicyNumber(@Body request: SudPolicyDetailsByPolicyNumberModel): SudPolicyDetailsByPolicyNumberModel.SudPolicyByMobileNumberResponse

    @POST(Constants.SUD_CUSTOMER_POLICY_SERVICE_API)
    suspend fun getSudFundDetail(@Body request: SudFundDetailsModel): SudFundDetailsModel.SudFundDetailsResponse

    @POST(Constants.SUD_CUSTOMER_POLICY_SERVICE_API)
    suspend fun getReceiptDetails(@Body request: SudReceiptDetailsModel): SudReceiptDetailsModel.SudReceiptDetailsResponse

    @POST(Constants.SUD_CUSTOMER_POLICY_SERVICE_API)
    suspend fun getSudPMJJBYCoiBase(@Body request: SudPMJJBYCoiBaseModel): SudPMJJBYCoiBaseModel.SudPMJJBYCoiBaseResponse

    @POST(Constants.SUD_CUSTOMER_POLICY_SERVICE_API)
    suspend fun getKypTemplate(@Body request: SudKypTemplateModel): SudKypTemplateModel.SudKypTemplateResponse

    @POST(Constants.SUD_CUSTOMER_POLICY_SERVICE_API)
    suspend fun getSudKypPdf(@Body request: SudKypPdfModel): SudKypPdfModel.SudKypPdfResponse

    @POST(Constants.SUD_CUSTOMER_POLICY_SERVICE_API)
    suspend fun getPayPremium(@Body request: SudPayPremiumModel): SudPayPremiumModel.SudPayPremiumResponse

    @POST(Constants.SUD_CUSTOMER_POLICY_SERVICE_API)
    suspend fun getRenewalPremium(@Body request: SudRenewalPremiumModel): SudRenewalPremiumModel.SudRenewalPremiumResponse

    @POST(Constants.SUD_CUSTOMER_POLICY_SERVICE_API)
    suspend fun getRenewalPremiumReceipt(@Body request: SudRenewalPremiumReceiptModel): SudRenewalPremiumReceiptModel.SudRenewalPremiumReceiptResponse

    @POST(Constants.SUD_CUSTOMER_POLICY_SERVICE_API)
    suspend fun getSudKYP(@Body request: SudKYPModel): SudKYPModel.SudKYPResponse

    @POST(Constants.SUD_GROUP_COI_API)
    suspend fun groupCoiApi(@Body request: SudGroupCOIModel): BaseResponse<SudGroupCOIModel.SudGroupCOIResponse>

    //Aktivo
    @POST(Constants.AKTIVO_CHECK_USER_API)
    suspend fun aktivoCheckUser(@Body request: AktivoCheckUserModel): AktivoCheckUserModel.AktivoCheckUserResponse

    @POST(Constants.AKTIVO_CREATE_USER_API)
    suspend fun aktivoCreateUser(@Body request: AktivoCreateUserModel): AktivoCreateUserModel.AktivoCreateUserResponse

    @POST(Constants.AKTIVO_GET_USER_TOKEN_USER_API)
    suspend fun aktivoGetUserToken(@Body request: AktivoGetUserTokenModel): AktivoGetUserTokenModel.AktivoGetUserTokenResponse

    @POST(Constants.AKTIVO_GET_REFRESH_TOKEN_API)
    suspend fun aktivoGetRefreshToken(@Body request: AktivoGetRefreshTokenModel): AktivoGetRefreshTokenModel.AktivoGetRefreshTokenResponse

    @POST(Constants.AKTIVO_GET_USER_API)
    suspend fun aktivoGetUser(@Body request: AktivoGetUserModel): AktivoGetUserModel.AktivoGetUserResponse

    //PolicyProductsList
    @POST(Constants.strPolicyProductsList)
    suspend fun policyProductsApi(@Body request: PolicyProductsModel): BaseResponse<PolicyProductsModel.PolicyProductsResponse>

    //EventsBannerList
    @POST(Constants.strEventsBannerList)
    suspend fun eventsBannerApi(@Body request: EventsBannerModel): BaseResponse<EventsBannerModel.EventsBannerResponse>
}