package com.techglock.health.app.common.constants

import android.os.Environment
import com.techglock.health.app.config_sdk.ConfigurationUtils

object Constants {

    //for PRODUCTION
/*  const val environment: String = "PROD"
    const val strAPIUrl: String = "https://prodapi.youmatterhealth.com/"
    private const val staticBaseUrl: String = "https://youmatterhealth.com/"
    const val SALT_MPT_API = "https://app.salt.one/external/sud/personality/v1/sud"
    const val PartnerCode = "YOUMATTER"
    val IVkey = byteArrayOf(0x53, 0x33, 0x44, 0x53, 0x38, 0x43, 0x30, 0x52, 0x31, 0x54, 0x59, 0x40, 0x32, 0x30, 0x32, 0x32)*/

    //for UAT
/*    const val environment: String = "UAT"
    const val strAPIUrl: String = "https://coreuat.youmatterhealth.com/"
    private const val staticBaseUrl: String = "https://webuat.youmatterhealth.com/"
    const val SALT_MPT_API = "https://app.mysaltapp.net/external/sud/personality/v1/sud"
    const val PartnerCode = "SUDLIFE"
    val IVkey = byteArrayOf(0x41, 0x31, 0x48, 0x53, 0x38, 0x43, 0x55, 0x52, 0x31, 0x54, 0x59, 0x40, 0x39, 0x38, 0x31, 0x32)*/

    val config = ConfigurationUtils.getInstance().getConfigData()
    val environment: String = config.environment
    val strAPIUrl: String = config.apiBaseUrl
    private val staticBaseUrl: String = config.staticBaseUrl
    val SALT_MPT_API = config.saltUrl
    val PartnerCode = config.partnerCode
    val IVkey = config.ivKey

    val TERMS_CONDITIONS_API = staticBaseUrl + "static/terms-and-conditions.html"
    val PRIVACY_POLICY_API = staticBaseUrl + "static/privacy-policy.html"
    val LEADERSHIP_EXPERINCES_URL = staticBaseUrl + "static/campaign/leadership-experiences.html"
    val SALT_VIDEOS_URL = staticBaseUrl + "static/campaign/youmatter-salt.html"
    val strProxyUrl = "Proxy/"
    const val strProxyLoginUrl: String = "Proxy/Registration/Login?"
    const val strProxyRegistrationUrl: String = "Proxy/Registration/Registration?"

    const val versionCode: Int = 1
    const val versionName: String = "1.0.0"
    const val IS_BYPASS_OTP = true

    //Banner Centurion Url's
    const val EMPLOYEE_CENTURION_URL = "https://bol.sudlife.in/PreLanding/PreLandingPage?id=63E9B461-A5BF-44CE-8DF6-687484325723&prd_cd=1041"
    const val CUSTOMER_CENTURION_URL = "https://bol.sudlife.in/Landing/LandingPage?id=9E4B40BB-358F-42F3-958C-E7A62CAA6B7E&prd_cd=1041"
    const val CENTURION_SHARE_URL = "https://bol.sudlife.in/Landing/LandingPage?id=9E4B40BB-358F-42F3-958C-E7A62CAA6B7E&prd_cd=1041"
    const val CENTURION_DISCLAIMER_URL = "https://youmatterhealth.com/static/campaign/centurion.html"

    //Banner Related URI's
    const val CENTURY_ROYALE_URL = "https://bol.sudlife.in/Landing/LandingPage?prd_cd=1032"
    const val CENTURY_GOLD_URL = "https://bol.sudlife.in/?prd_cd=1036"
    const val PROTECT_SHIELD_PLUS_URL = "https://www.sudlife.in/products/life-insurance/term-plans/sud-life-protect-shield-plus"

    const val strSudPolicyBaseURL = "https://bianintguat.sudlife.in/"
    //const val strSudPolicyBaseURL = "https://bianintgprod.sudlife.in/"

/*    const val BOI_SMART_HEALTH_BANNER_API = "https://webuat.youmatterhealth.com/static/campaign/smarthealth.html?url=BOI"
    const val UBI_SMART_HEALTH_BANNER_API = "https://webuat.youmatterhealth.com/static/campaign/smarthealth.html?url=UBI"
    const val SUD_SMART_HEALTH_BANNER_API = "https://webuat.youmatterhealth.com/static/campaign/smarthealth.html?url=SUD"
    const val CUSTOMER_SMART_HEALTH_BANNER_API = "https://webuat.youmatterhealth.com/static/campaign/smarthealth.html?url=CUSTOMER"

    const val BOI_SMART_HEALTH_BANNER_API = "https://youmatterhealth.com/static/campaign/smarthealth.html?url=BOI"
    const val UBI_SMART_HEALTH_BANNER_API = "https://youmatterhealth.com/static/campaign/smarthealth.html?url=UBI"
    const val SUD_SMART_HEALTH_BANNER_API = "https://youmatterhealth.com/static/campaign/smarthealth.html?url=SUD"
    const val CUSTOMER_SMART_HEALTH_BANNER_API = "https://youmatterhealth.com/static/campaign/smarthealth.html?url=CUSTOMER"*/

    const val strPolicyProductsList = "PHR/api/Partner/PolicyProductsList"
    const val strEventsBannerList = "PHR/api/Partner/EventsBannerList"

    //Language Related API's
    const val strUpdateProfileSetting = "phr/api/Person/UpdateProfileSetting"
    const val strRefreshToken = "PHR/api/Person/RefreshToken"

    //Security API's
    const val strProxySsoUrl: String = "Proxy/Registration/SSOLogin?"
    const val strCheckIfLoginNameExists = "Security/api/Account/CheckIfLoginNameExists/"
    const val strCheckIfEmailExists = "security/api/account/CheckIfEmailExists/"
    const val strCheckIfPrimaryPhoneExists = "security/api/account/CheckIfPrimaryPhoneExists/"
    const val strGetTermsAndConditionsForPartner = "Security/api/Account/GetTermsAndConditionsForPartner"
    const val strOtpGenerate = "security/api/OTP/Generate"
    const val strOtpValidate = "security/api/OTP/Validate"
    const val strUpdateNewPassword = "Security/api/Security/UpdateNewPassword"
    const val strDarwinBoxDataUrl = "PHR/api/Partner/GetLoginInfoWithDarwinBox"

    //Blogs API's
    const val strSearchBlogsProxyURL = "wp-json/wp/v2/posts?search="
    const val strBlogsListAll = "Proxy/api/Blogs/ListAll"
    const val strBlogsListBySearchTerm = "Proxy/api/Blogs/ListBySearchTerm"
    const val strBlogsCategoryList = "Proxy/api/Blogs/ListCategory"
    const val strBlogsListByCategory = "Proxy/api/Blogs/ListByCategory"
    const val strBlogsListSuggestion = "Proxy/api/Blogs/ListSuggestions"
    const val strBlogsRelatedTo = "Proxy/api/Blogs/ListRelatedArticles"

    const val SAVE_CLOUD_MESSAGING_ID_API = "Security/api/Account/SaveCloudMessagingID"
    const val CHECK_APP_UPDATE_API = "PHR/api/App/GetVersion"
    const val SAVE_FEEDBACK_API = "PHR/api/App/SaveFeedback"
    const val PASSWORD_CHANGE_API = "Security/api/Security/ChangePassword"
    const val CONTACT_US_API = "messaging/api/message/ContactUs"
    const val ADD_FEATURE_ACCESS_LOG_API = "PHR/api/Partner/AddFeatureAccessLog"
    const val PERSON_DELETE_API = "PHR/api/Person/Delete"

    const val INNER_HOUR_WEBVIEW_API = "https://amaha-youmatter.netlify.app/dashboard"
    const val AMAHA_SSO_URL_API = "PHR/api/Amaha/GetSSOUrl"

    const val NIMEYA_URL_API = "PHR/api/Partner/GetNimeyaURL"
    const val NIMEYA_GET_RISKO_METER_API = "PHR/api/Partner/GetRiskoMeter"
    const val NIMEYA_SAVE_RISKO_METER_API = "PHR/api/Partner/SaveRiskoMeter"
    const val NIMEYA_GET_RISKO_METER_HISTORY_API = "PHR/api/Partner/GetRiskoMeterHistory"
    const val NIMEYA_SAVE_PROTECTO_METER_API = "PHR/api/Partner/SaveProtectoMeter"
    const val NIMEYA_GET_PROTECTO_METER_HISTORY_API = "PHR/api/Partner/GetProtectoMeterHistory"
    const val NIMEYA_SAVE_RETIRO_METER_API = "PHR/api/Partner/SaveRetiroMeter"
    const val NIMEYA_GET_RETIRO_METER_HISTORY_API = "PHR/api/Partner/GetRetiroMeterHistory"

    //Wellfie API's
    const val WELLFIE_SAVE_VITALS_API = "PHR/api/Wellfie/SaveVitals"
    const val WELLFIE_GET_VITALS_API = "PHR/api/Wellfie/GetVitals"
    const val WELLFIE_LIST_VITALS_API = "PHR/api/Wellfie/ListVitals"
    const val WELLFIE_GET_SSO_URL_API = "PHR/api/Wellfie/GetSSOUrl"

    const val MAIN_DATABASE_NAME = Configuration.strAppIdentifier + ".db"

    const val DEFAULT_STEP_GOAL = 3000
    const val DEFAULT_WATER_GOAL = 2500
    const val strEnableFamilyProfile: Boolean = true
    const val strApolloHtml = "file:///android_asset/apollo/apollo_SOP.html"

    val primaryStorage = Environment.getExternalStorageDirectory().toString()

    //User API's
    const val GET_USER_DETAIL_API = "PHR/api/Person/Get"
    const val UPDATE_PERSONAL_DETAIL_API = "PHR/api/Person/Update"

    //const val SAVE_PROFILE_IMAGE_API = "PHR/api/Person/SaveProfileImage"
    const val UPLOAD_PROFILE_IMAGE_API = "Proxy/Document/UploadProfileImage"
    const val REMOVE_PROFILE_IMAGE_API = "PHR/api/Person/DeleteProfileImage"

    //Fitness Tracker API's
    const val FITNESS_GET_LATEST_GOAL_API = "PHR/api/Steps/GetLatestGoal"
    const val FITNESS_LIST_HISTORY_API = "PHR/api/Steps/ListHistory"
    const val FITNESS_SET_GOAL_API = "PHR/api/Steps/SetGoal"
    const val FITNESS_STEP_SAVE_LIST_API = "PHR/api/Steps/SaveList"
    const val FITNESS_STEP_SAVE_API = "PHR/api/Steps/Save"

    //Family Member APIs
    const val RELATIVES_LIST_API = "PHR/api/Person/ListRelatives"
    const val ADD_RELATIVE_API = "PHR/api/Person/Add"
    const val UPDATE_RELATIVE_API = "PHR/api/Person/Update"
    const val REMOVE_RELATIVE_API = "PHR/api/Person/RemoveRelative"

    //Family Doctors API's
    const val DOCTORS_LIST_API = "PHR/api/FamilyDoctor/List"
    const val DOCTORS_LIST_SPECIALITY_API = "PHR/api/FamilyDoctor/Listspeciality"
    const val ADD_DOCTOR_API = "PHR/api/FamilyDoctor/Add"
    const val UPDATE_DOCTOR_API = "PHR/api/FamilyDoctor/Update"
    const val REMOVE_DOCTOR_API = "PHR/api/FamilyDoctor/Delete"

    //HRA API's
    const val HRAurl = "Document/DownloadBookReport?"
    const val HRA_START_API = "PHR/api/PrimaryHRA/Start"
    const val BMI_EXIST_API = "PHR/api/BMI/GetCurrent"
    const val BP_EXIST_API = "PHR/api/BloodPressure/GetCurrent"
    const val LAB_RECORDS_API = "PHR/api/LabRecord/ListCurrent"
    const val SAVE_AND_SUBMIT_HRA_API = "PHR/api/PrimaryHRA/SaveAndSubmitHRA"
    const val MEDICAL_PROFILE_SUMMARY_API = "phr/api/Report/GetMedicalProfileSummary"
    const val GET_HRA_SUMMARY_API = "PHR/api/PrimaryHRA/GetLastHRAHistory"
    const val ASSESSMENT_SUMMARY_API = "PHR/api/MedicalProfile/GetAssessmentSummary"
    const val LIST_RECOMMENDED_TESTS_API = "PHR/api/MedicalProfile/ListRecommendedTests"

    //HealthRecords API's
    const val SHR_DOCUMENT_TYPE_API = "PHR/api/Document/ListDocumentTypes"
    const val SHR_DOCUMENT_LIST_API = "PHR/api/Document/List"
    const val SHR_DOCUMENT_SAVE_API = "PHR/api/Document/Save "
    const val SHR_DOCUMENT_DELETE_API = "PHR/api/Document/Delete"
    const val SHR_DOCUMENT_DOWNLOAD_API = "PHR/api/Document/GetByHealthDocumentID"
    const val SHR_OCR_EXTRACT_DOCUMENT = "OCR/OCR/ExtractDocument"
    const val SHR_OCR_DOCUMENT_SAVE = "PHR/api/LabRecord/Save"
    const val SHR_OCR_UNIT_EXIST = "PHR/api/LabRecord/CheckIfUnitExist"

    //Track Parameter API's
    const val TRACK_PARAM_LIST_MASTER = "phr/api/labrecord/listmaster"
    const val TRACK_PARAM_LABRECORD_LIST_HISTORY_BY_PROFILE_CODES = "phr/api/LabRecord/ListHistoryByProfileCodes"
    const val TRACK_PARAM_BMI_LIST_VITALS_HISTORY = "phr/api/BMI/ListVitalsHistory"
    const val TRACK_PARAM_BMI_LIST_HISTORY = "phr/api/BMI/ListHistory"
    const val TRACK_PARAM_WHR_LIST_HISTORY = "phr/api/WHR/ListHistory"
    const val TRACK_PARAM_BLOOD_PRESSURE_LISTHISTORY = "phr/api/BloodPressure/ListHistory"
    const val TRACK_PARAM_LAB_RECORD_SYNCHRONIZE = "phr/api/LabRecord/Synchronize"
    const val TRACK_PARAM_WHR_SYNCHRONIZE = "phr/api/WHR/Synchronize"
    const val TRACK_PARAM_BMI_SYNCHRONIZE = "phr/api/BMI/Synchronize"
    const val TRACK_PARAM_LIST_PARAMETER_TRACKING_PREFERENCES = "PHR/api/LabRecord/ListParameterTrackingPreferences"
    const val TRACK_PARAM_SAVE_PARAMETER_TRACKING_PREFERENCE = "PHR/api/LabRecord/SaveParameterTrackingPreference"

    // Medication Tracker New API's
    const val MEDICATION_GET_API = "PHR/api/Medication/Get/"
    const val MEDICATION_DRUGS_LIST_API = "PHR/api/Drug/List/"
    const val MEDICATION_LIST_BY_DATE_API = "PHR/api/Medication/ListByDate/"
    const val MEDICATION_LIST_BY_DAY_API = "PHR/api/Medication/ListByDay"
    const val MEDICATION_SAVE_API = "PHR/api/Medication/Save"
    const val MEDICATION_UPDATE_API = "PHR/api/Medication/UpdateMedication"
    const val MEDICATION_SET_ALERT_API = "PHR/api/Medication/SetAlert"
    const val MEDICATION_ADD_IN_TAKE_API = "PHR/api/Medication/AddInTake"
    const val MEDICATION_DELETE_TAKE_API = "PHR/api/Medication/Delete/"
    const val MEDICATION_GET_MEDICINE_IN_TAKE_API = "PHR/api/Medication/ListMedicationInTakeByScheduleID/"

    //ToolsTrackers API's
    const val TOOLS_START_QUIZ_API = "Quiz/api/Quiz/StartQuiz"
    const val TOOLS_HEART_AGE_API = "Quiz/api/Quiz/GetHeartAge"
    const val TOOLS_DIABETES_API = "Quiz/api/Quiz/GetDiabeticRisk"
    const val TOOLS_HYPERTENSION_API = "Quiz/api/Quiz/GetHypertensionRisk"
    const val TOOLS_STRESS_ANXIETY_API = "Quiz/api/Quiz/GetStressAndAnxietyRisk"
    const val TOOLS_SMART_PHONE_ADDICTION_API = "Quiz/api/Quiz/GetSmartPhoneRisk"
    //const val TOOLS_SMART_PHONE_ADDICTION_API = "Quiz/api/Quiz/SaveResponse"

    //ToolsTrackers Reference Links
    const val LINK_CARDIOVASCULAR = "https://ccs.ca/"
    const val LINK_HEART_AGE = "https://www.framinghamheartstudy.org/"
    const val LINK_WOMEN_HEALTH = "https://www.fogsi.org/category/good-clnical-practice-recommendations/"
    const val LINK_DASS = "https://www.karger.com/Article/Fulltext/485182"

    // Water Tracker API's
    const val SAVE_WATER_INTAKE_GOAL_API = "PHR/api/WaterTracker/SaveWaterIntakeGoal"
    const val SAVE_DAILY_WATER_INTAKE_API = "PHR/api/WaterTracker/SaveDailyWaterIntake"
    const val GET_DAILY_WATER_INTAKE_API = "PHR/api/WaterTracker/GetDailyWaterIntake"
    const val GET_WATER_INTAKE_HISTORY_API = "PHR/api/WaterTracker/GetWaterIntakeHistory"
    const val GET_WATER_INTAKE_SUMMARY_API = "PHR/api/WaterTracker/GetWaterIntakeSummary"

    //Aktivo API's
    const val AKTIVO_CHECK_USER_API = "Proxy/api/Aktivolabs/CheckUser"
    const val AKTIVO_CREATE_USER_API = "Proxy/api/Aktivolabs/CreateUser"
    const val AKTIVO_GET_USER_TOKEN_USER_API = "Proxy/api/Aktivolabs/RetriveUserToken"
    const val AKTIVO_GET_REFRESH_TOKEN_API = "Proxy/api/Aktivolabs/RefreshToken"
    const val AKTIVO_GET_USER_API = "Proxy/api/Aktivolabs/GetUser"

    //SUD Life Policy API's
    const val SUD_CUSTOMER_POLICY_SERVICE_API = "Proxy/api/SUDCustomer/CallCustomerAPIV2"
    //const val SUD_CUSTOMER_POLICY_SERVICE_API = "Proxy/api/SUDCustomer/CallCustomerAPI"
    const val SUD_GROUP_COI_API = "PHR/api/Partner/GroupCOIWrapper"
    const val SUD_MOBILE_VERIFICATION_API = "api/MobNumberUat"

    const val DEEP_LINK = "DeepLink"
    const val SCREEN_FEATURE_CAMPAIGN = "DeepLinkFeatureCampaign"

    const val DEEP_LINK_REFERRAL = "referral"
    const val DEEP_LINK_APP_FEATURE_CAMPAIGN = "app_feature_campaign"
    const val DEEP_LINK_DARWINBOX = "darwinbox"
    const val DEEP_LINK_VALUE = "deep_link_value"
    const val DEEP_LINK_SUB1 = "deep_link_sub1"
    const val DEEP_LINK_SUB2 = "deep_link_sub2"
    const val AF_DP = "af_dp"
    const val AF_DP_VALUE = "youmatterapp://mainactivity"

    const val FEATURE_CODE_AKTIVO_DASHBOARD = "AKTIVO_DASHBOARD"
    const val FEATURE_CODE_AKTIVO_BADGES = "AKTIVO_BADGES"
    const val FEATURE_CODE_AKTIVO_SCORE = "AKTIVO_SCORE"
    const val FEATURE_CODE_AKTIVO_MIND_SCORE = "AKTIVO_MIND_SCORE"
    const val FEATURE_CODE_AKTIVO_CHALLENGES = "AKTIVO_CHALLENGES"

    const val FEATURE_CODE_HRA = "HRA"
    const val FEATURE_CODE_MPT = "MPT"
    const val FEATURE_CODE_NIMEYA = "NIMEYA"

    const val FEATURE_CODE_MEDITATION = "MEDITATION"
    const val FEATURE_CODE_YOGA = "YOGA"
    const val FEATURE_CODE_EXERCISE = "EXERCISE"

    const val FEATURE_CODE_RISKO_METER = "RISKO_METER"
    const val FEATURE_CODE_PROTECTO_METER = "PROTECTO_METER"
    const val FEATURE_CODE_RETIRO_METER = "RETIRO_METER"

    const val FEATURE_CODE_NVEST_EDUCATION = "NVEST_EDUCATION"
    const val FEATURE_CODE_NVEST_MARRIAGE = "NVEST_MARRIAGE"
    const val FEATURE_CODE_NVEST_WEALTH = "NVEST_WEALTH"
    const val FEATURE_CODE_NVEST_HOME = "NVEST_HOME"
    const val FEATURE_CODE_NVEST_RETIREMENT = "NVEST_RETIREMENT"
    const val FEATURE_CODE_NVEST_HUMAN_VALUE = "NVEST_HUMAN_VALUE"

    const val FEATURE_CODE_SMITFIT = "SMITFIT"
    const val FEATURE_CODE_LIVE_SESSIONS = "LIVE_SESSION"
    const val FEATURE_CODE_INVESTOMETER = "INVESTOMETER"
    const val FEATURE_CODE_NVEST = "NVEST"

    const val FEATURE_CODE_HYDRATION_TRACKER = "HYDRATION_TRACKER"
    const val FEATURE_CODE_TRACK_VITAL_PARAMETERS = "TRACK_VITAL_PARAMETERS"
    const val FEATURE_CODE_MEDICATION_TRACKER = "MEDICATION_TRACKER"
    const val FEATURE_CODE_MY_HEALTH_RECORDS = "MY_HEALTH_RECORDS"
    const val FEATURE_CODE_TOOLS_CALCULATORS = "TOOLS_CALCULATORS"
    const val FEATURE_CODE_YOUR_POLICY = "YOUR_POLICY"
    const val FEATURE_CODE_YOUR_POLICY_DOWNLOADS = "YOUR_POLICY_DOWNLOADS"

    const val FEATURE_CODE_CALCULATOR_HEART_AGE = "CALCULATOR_HEART_AGE"
    const val FEATURE_CODE_CALCULATOR_DIABETES = "CALCULATOR_DIABETES"
    const val FEATURE_CODE_CALCULATOR_HYPERTENSION = "CALCULATOR_HYPERTENSION"
    const val FEATURE_CODE_CALCULATOR_STRESS_ANXIETY = "CALCULATOR_STRESS_ANXIETY"
    const val FEATURE_CODE_CALCULATOR_SMART_PHONE_ADDICTION = "CALCULATOR_SMART_PHONE_ADDICTION"
    const val FEATURE_CODE_CALCULATOR_DUE_DATE = "CALCULATOR_DUE_DATE"

    const val FEATURE_CODE_REFER_FRIEND = "REFER_FRIEND"
    const val FEATURE_CODE_HOW_IT_WORKS = "HOW_IT_WORKS"
    const val FEATURE_CODE_SETTINGS = "SETTINGS"
    const val FEATURE_CODE_LANGUAGE = "LANGUAGE"
    const val FEATURE_CODE_PRIVACY_POLICY = "PRIVACY_POLICY"
    const val FEATURE_CODE_TERMS_CONDITIONS = "TERMS_CONDITIONS"

    const val SUD_MOBILE_NUMBER_DETAILS = "MOBILE_NUMBER_DETAILS"
    const val SUD_POLICY_DETAILS = "POLICY_DETAILS"
    const val SUD_KYP = "KYP_API"
    const val SUD_FUND_DETAILS = "FUND_DETAILS"
    const val SUD_INSTANT_ISSUANCE_WA = "INSTANT_ISSUANCE_WA"
    const val SUD_PMJJBY_COI_BASE = "PMJJBY_COI_BASE"
    const val SUD_KYP_TEMPLATE = "KYP_TEMPLATE"
    const val SUD_KYP_PDF = "KYP_PDF_API"
    const val SUD_SHORT_URL_API = "SHORT_URL_API"
    const val SUD_PPS_RPR_PDF = "PPS_RPR_PDF"
    const val SUD_RENEWAL_PREMIUM = "RENEWAL_PREMIUM"

    const val PLAY_STORE_BEETLY_LINK = "https://onelink.to/6av3f8"
    const val SUD_LIFE_TOLL_FREE_NUMBER = "18002668833"
    const val SUD_LIFE_CUSTOMER_CARE_EMAIL = "customercare@sudlife.in"
    const val SUD_LIFE_WHATS_APP_BOT_URL = "https://wa.me/917208867122"
    const val SUD_LIFE_CUSTOMER_PORTAL_URL = "https://customer.sudlife.in"

    const val PREMIUM_RECEIPT = "premium_receipt"
    const val POLICY_DOCUMENT = "policy_document"
    const val KNOW_YOUR_POLICY = "know_your_policy"
    const val CHANGE_POLICY_MOBILE_NUMBER = "change_policy_mobile_number"
    const val CALL_CUSTOMER_CARE = "call_customer_care"
    const val WHATS_APP_BOT = "whats_app_bot"

    // Application Folder Name
    const val APPLICATION_SUBFOLDER_PROFILE_IMAGES = "Profile Images"
    const val APPLICATION_SUBFOLDER_RECORDS = "Records"

    //Template Constants
    const val PRIMARY_COLOR = "primaryColor"
    const val SECONDARY_COLOR = "secondaryColor"
    const val TEXT_COLOR = "textColor"
    const val ICON_TINT_COLOR = "iconTintColor"
    const val LEFT_BUTTON_COLOR = "leftButtonColor"
    const val RIGHT_BUTTON_COLOR = "rightButtonColor"
    const val LEFT_BUTTON_TEXT_COLOR = "leftButtonTextColor"
    const val RIGHT_BUTTON_TEXT_COLOR = "rightButtonTextColor"
    const val SELECTION_COLOR = "selectionColor"
    const val DESELECTION_COLOR = "deselectionColor"

    //Registration Source
    const val SSO_SOURCE = "SUD_SSO_REG"
    const val LOGIN_SOURCE = "SUDLIFE_REG"
    const val DARWINBOX_SOURCE = "SUDLIFE_DARWINBOX_REG"
    const val GOOGLE_SOURCE = "SUDLIFE_GOOGLE_REG"
    const val FACEBOOK_SOURCE = "SUDLIFE_FACEBOOK_REG"

    //RELATIONSHIP CODE
    const val SELF_RELATIONSHIP_CODE = "SELF"
    const val FATHER_RELATIONSHIP_CODE = "FAT"
    const val MOTHER_RELATIONSHIP_CODE = "MOT"
    const val SON_RELATIONSHIP_CODE = "SON"
    const val DAUGHTER_RELATIONSHIP_CODE = "DAU"
    const val BROTHER_RELATIONSHIP_CODE = "BRO"
    const val SISTER_RELATIONSHIP_CODE = "SIS"
    const val GRANDFATHER_RELATIONSHIP_CODE = "GRF"
    const val GRANDMOTHER_RELATIONSHIP_CODE = "GRM"
    const val WIFE_RELATIONSHIP_CODE = "WIF"
    const val HUSBAND_RELATIONSHIP_CODE = "HUS"
    const val OTHER_RELATIONSHIP_CODE = "OTH"

    const val QUIZ_CODE_HEART_AGE = "HRTAGECAL"
    const val QUIZ_CODE_DIABETES = "DIAB_RISK_ASMT"
    const val QUIZ_CODE_HYPERTENSION = "HYPERTENSIONCAL"
    const val QUIZ_CODE_STRESS_ANXIETY = "DASS-21"
    const val QUIZ_CODE_SMART_PHONE = "SMARTPH"

    const val NIMEYA_RISKO_METER = "RISKO_METER"
    const val NIMEYA_RISKO_METER_RESULT = "RISKO_METER_RESULT"
    const val NIMEYA_PROTECTO_METER = "PROTECTO_METER"
    const val NIMEYA_PROTECTO_METER_RESULT = "PROTECTO_METER_RESULT"
    const val NIMEYA_RETIRO_METER = "RETIRO_METER"
    const val NIMEYA_RETIRO_METER_RESULT = "RETIRO_METER_RESULT"

    //Medication Constants
    const val MEDICINE_ID = "MedicineID"
    const val MEDICATION_ID = "MedicationID"
    const val Drug_ID = "DrugID"
    const val SCHEDULE_ID = "IdSchedule"
    const val SERVER_SCHEDULE_ID = "ScheduleID"
    const val MEDICINE_NAME = "MedicineName"
    const val DRUG_TYPE_CODE = "DrugTypeCode"
    const val SCHEDULE_TIME = "ScheduleTime"
    const val MED_DATE = "MedDate"
    const val MEDICATION_UNIT = "Unit"
    const val DESCRIPTION = "Description"
    const val DOSAGE = "Dosage"
    const val INSTRUCTION = "Instruction"
    const val DOSAGE_REMAINING = "DosageRemaining"

    //const val MEDICINE_IN_TAKE_ID = "IdMedicineInTake"
    const val MEDICINE_IN_TAKE_ID = "MedicationInTakeID"
    const val TAKEN = "Taken"
    const val MISSED = "Missed"
    const val SKIPPED = "Skipped"
    const val BEFORE_MEAL = "Before Meal"
    const val WITH_MEAL = "With Meal"
    const val AFTER_MEAL = "After Meal"
    const val ANYTIME = "Anytime"
    const val DAILY = "Daily"
    const val FOR_X_DAYS = "For X Days"
    const val MEDICATION = "Medication"
    const val IN_TAKE = "InTake"
    const val MEDICINE_DETAILS = "MedicineDetails"
    const val TAKE_STATUS = "Status"
    const val BETTER = "Better"
    const val SAME = "Same"
    const val WORSE = "Worse"

    const val ADD = "Add"
    const val UPDATE = "Update"
    const val CANCEL = "Cancel"
    const val OK = "Ok"
    const val DATA = "Data"
    const val CLEAR_FITNESS_DATA = "ClearFitnessData"
    const val TRACK_PARAMETER = "TrackParameter"
    const val URI = "uri"
    const val CODE = "code"
    const val UPLOAD = "Upload"
    const val VIEW = "View"
    const val SHARE = "Share"
    const val DIGITIZE = "Digitize"
    const val DASHBOARD = "Dashboard"
    const val DOWNLOAD = "Download"
    const val DELETE = "Delete"
    const val EDIT = "Edit"
    const val RESTART = "Restart"
    const val SWITCH_PROFILE = "SwitchProfile"

    const val NOTIFICATION = "Notification"
    const val NOTIFICATION_ID = "NotificationId"
    const val NOTIFICATION_ACTION = "NotificationAction"
    const val NOTIFICATION_TITLE = "title"
    const val NOTIFICATION_MESSAGE = "message"
    const val NOTIFICATION_URL = "url"
    const val SCREEN = "screen"
    const val DATA_ID = "dataId"
    const val NOTIFICATION_TYPE = "NotificationType"
    const val REDIRECT_LINK = "redirect_link"
    const val REDIRECT_TYPE = "redirect_type"
    const val TIME = "Time"
    const val TITLE = "Title"
    const val SUB_TITLE = "SubTitle"
    const val MODULE_CODE = "ModuleCode"

    const val TRUE = "true"
    const val FALSE = "false"
    const val SUCCESS = "Success"
    const val FAILURE = "Failure"
    const val TAB = "tab"
    const val FROM = "from"
    const val TO = "to"
    const val RECORD = "record"
    const val PROFILE = "profile"
    const val SSO = "SSO"
    const val NSSO = "NSSO"
    const val WEB_URL = "WebUrl"
    const val HAS_COOKIES = "HasCookies"
    const val RELATION = "Relation"
    const val RELATION_CODE = "RelationShipCode"
    const val RELATION_SHIP_ID = "RelationShipID"
    const val RELATIVE_ID = "RelativeID"
    const val PERSON_ID = "PersonID"
    const val HRA_TEMPLATE_ID = "TemplateId"
    const val GENDER = "GENDER"
    const val BODY = "body"
    const val BLOG_ID = "blog_id"
    const val LINK = "Link"
    const val ENROLL = "Enroll"
    const val SEARCH = "Search"
    const val CATEGORY = "Category"
    const val CATEGORY_ID = "CategoryId"
    const val ALL = "All"
    const val FRESH = "Fresh"
    const val BACK = "Back"
    const val HOME = "Home"
    const val FORCEUPDATE = "ForceUpdate"
    const val LOGOUT = "Logout"
    const val DEFAULT = "DEFAULT"
    const val CUSTOM = "CUSTOM"
    const val GOAL = "goal"
    const val IS_DATA = "IsData"
    const val CAMPAIGN_FEATURE_NAME = "CampaignFeatureName"
    const val POLICY_VIEW = "PolicyView"

    const val MEDITATION = "MEDITATION"
    const val YOGA = "YOGA"
    const val EXERCISE = "EXERCISE"

    const val SMART_HEALTH_PRODUCT = "SMART_HEALTH_PRODUCT"
    const val CENTURY_ROYALE = "CENTURY_ROYALE"
    const val CENTURY_GOLD = "CENTURY_GOLD"
    const val PROTECT_SHIELD_PLUS = "PROTECT_SHIELD_PLUS"
    const val CENTURION = "CENTURION"


    const val POLICY_PMJJBY = "POLICY_PMJJBY"
    const val POLICY_GROUP_COI = "POLICY_GROUP_COI"
    const val POLICY_CREDIT_LIFE = "POLICY_CREDIT_LIFE"

    const val EDUCATION = "Education"
    const val MARRIAGE = "Marriage"
    const val WEALTH = "Wealth"
    const val HOUSE = "House"
    const val RETIREMENT = "Retirement"
    const val HUMAN_VALUE = "Human"

    const val CODE_HEART_AGE_CALCULATOR = "HAC"
    const val CODE_DIABETES_CALCULATOR = "DC"
    const val CODE_HYPERTENSION_CALCULATOR = "HC"
    const val CODE_STRESS_ANXIETY_CALCULATOR = "SAC"
    const val CODE_SMART_PHONE_ADDICTION_CALCULATOR = "SPC"
    const val CODE_DUE_DATE_CALCULATOR = "DDC"

    const val TERMS_CONDITIONS = "TERMS_CONDITIONS"
    const val PRIVACY_POLICY = "PRIVACY_POLICY"

    const val PHYSICAL_ACTIVITY_PERMISSION = "PhysicalActivityPermission"
    const val SYNC_FITNESS_DATA = "SyncFitnessData"
    const val DENIED = "Denied"

    const val LOGIN = "Login"
    const val SIGN_UP_NEW = "SignUpNew"
    const val LOGIN_WITH_OTP = "LoginWithOtp"

    const val LANGUAGE_CODE_ENGLISH = "en"
    const val LANGUAGE_CODE_HINDI = "hi"

    const val LANGUAGE_ENGLISH = "English"
    const val LANGUAGE_HINDI = "Hindi"

    const val LANGUAGE = "LANGUAGE"
    const val CHANGE_PASSWORD = "CHANGE_PASSWORD"
    const val DELETE_ACCOUNT = "DELETE_ACCOUNT"

    const val CL_CODE_LOAN_ACCOUNT_NO = "LOAN_ACCOUNT_NUMBER"
    const val CL_CODE_MEMBERSHIP_NO = "MEMBERSHIP_NUMBER"
    const val CL_CODE_APPLICATION_NO = "APPLICATION_NUMBER"
    const val CL_CODE_COI_NO = "COI_NUMBER"

    const val CLIENT_ID = "ClientID"
    const val PROCESS_DATA_API_URL = "ProcessDataAPIUrl"
    const val PROCESS_DATA_API_TOKEN = "ProcessDataAPIToken"
    const val SOCKET_URL = "SocketUrl"
    const val SOCKET_TOKEN = "SocketToken"

    const val HEIGHT = "Height"
    const val WEIGHT = "Weight"
    const val SYSTOLIC = "Systolic"
    const val DIASTOLIC = "Diastolic"

    const val RANDOM_SUGAR = "Random Sugar"
    const val FASTING_SUGAR = "Fasting Sugar"
    const val POST_MEAL_SUGAR = "Post Meal Blood Sugar"
    const val HBA1C = "HbA1c"

    // Fitness
    const val SYNC: String = "sync"
    const val STEP_ID: String = "StepId"
    const val GOAL_ID: String = "GoalId"
    const val DISTANCE = "Distance"
    const val ACTIVE_TIME = "ActiveTime"
    const val STEPS_COUNT = "StepsCount"
    const val TOTAL_GOAL = "TotalGoal"
    const val GOAL_PERCENTILE = "GoalPercentile"
    const val CALORIES = "Calories"
    const val RECORD_DATE = "RecordDate"
    const val STEP_NOTIFICATION = "StepNotification"
    const val LAST_UPDATED_TIME = "LastUpdatedTime"
    const val LAST_SYNC_DATE = "LastSyncDate"

    const val USER = "User"
    const val RELATIVE = "Relative"
    const val LOGIN_TYPE = "LoginType"
    const val NAME = "name"
    const val EMAIL = "email"
    const val EMAIL_ADDRESS = "EmailAddress"
    const val PHONE_NUMBER = "PhoneNumber"
    const val PHONE = "phone"
    const val PASSWORD = "password"
    const val IS_PASSWORD_UPDATE = "IsPasswordUpdate"
    const val PROFILE_IMAGE_ID = "ProfileImageID"
    const val Toobar_Title = "ToolbarTitle"
    const val TOOLBAR_TOOLS_TRACKERS = "Tools and Trackers"
    const val TOOLBAR_HEALTH_PACKAGES = "Health Packages"
    const val TOOLBAR_CHAT_WITH_DOCTOR = "Chat With Doctor"
    const val TOOLBAR_APOLLO = "Apollo"
    const val TOOLBAR_MEDLIFE = "Med Life"
    const val TOOLBAR_CHAT_WITH_DIETICIAN = "Chat With Dietician"
    const val UNITS_MGDL = "mg/dL"
    const val WEEK = "Week"
    const val MONTH = "Month"
    const val YEAR = "Year"
    const val DAYOFWEEK = "DayOfWeek"
    const val DAYOFMONTH = "DayOfMonth"
    const val MONTHOFYEAR = "MonthOfYear"
    const val IS_TODAY = "IsToday"
    const val DATE = "date"
    const val FIRST_NAME = "FIRSTNAME"
    const val LAST_NAME = "LASTTNAME"
    const val PRIMARY_PHONE = "PrimaryPhone"
    const val AGE = "Age"
    const val DATE_OF_BIRTH = "DateOfBirth"
    const val DIALING_CODE = "DialingCode"
    const val LOGIN_NAME = "LoginName"
    const val REGISTER_PHR: String = "REGISTER"
    const val USER_INFO: String = "PREF_USER_INFO"
    const val TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss"
    const val GOTO: String = "GOTO"

    // BMI CALCULATION VALUES
    const val HEIGHT_MIN = 119
    const val HEIGHT_MAX = 245
    const val WEIGHT_MIN_METRIC = 29
    const val WEIGHT_MAX_METRIC = 251

    const val GALLERY_SELECT_CODE = 2291
    const val FILE_SELECT_CODE = 2292
    const val CAMERA_SELECT_CODE = 2293
    const val REQ_CODE_SMS_CONSENT = 2294

    const val REQ_CODE_GOOGLE_FIT_PERMISSIONS = 1212
    const val REQ_CODE_AKTIVO_GOOGLE_FIT_PERMISSIONS = 40001
    const val REQ_CODE_AKTIVO_GOOGLE_FIT_ACTIVITY_PERMISSIONS = 40002
    const val REQ_CODE_AKTIVO_GOOGLE_FIT_SLEEP_PERMISSIONS = 40003
    const val REQ_PHYSICAL_ACTIVITY_PERMISSIONS = 40004

    const val PROGRESS_DURATION = 1000
    const val ANIMATION_DURATION = 1500
    const val DELETE_LOADER_ANIM_DELAY_IN_MS = 2000
    const val LOADER_ANIM_DELAY_IN_MS = 1500
    const val SPLASH_ANIM_DELAY_IN_MS = 2500
    const val OTP_COUNT_DOWN_TIME = 30

    const val LOADER_DEFAULT = "Loader_Default"
    const val LOADER_BLAST = "Loader_Blast"
    const val LOADER_DOWNLOAD = "Loader_Download"
    const val LOADER_UPLOAD = "Loader_Upload"
    const val LOADER_DIGITIZE = "Loader_Digitize"
    const val LOADER_DELETE = "Loader_Delete"

    const val HRA = "HRA"
    const val FITNESS_TRACKER = "FITNESS_TRACKER"
    const val HYDRATION_TRACKER = "HYDRATION_TRACKER"
    const val HEALTH_LIBRARY = "HEALTH_LIBRARY"
    const val TRACK_PARAMETERS = "TRACK_PARAMETERS"
    const val MEDICATION_TRACKER = "MEDICATION_TRACKER"
    const val STORE_HEALTH_RECORDS = "STORE_HEALTH_RECORDS"
    const val TOOLS_AND_CALCULATORS = "TOOLS_AND_CALCULATORS"

    const val AKTIVO_DASHBOARD_CODE = "dashboard"
    const val AKTIVO_SCORE_CODE = "physicalLifeStyleScore"
    const val AKTIVO_MIND_SCORE_CODE = "mind"
    const val AKTIVO_BADGES_CODE = "badges"
    const val AKTIVO_CHALLENGES_CODE = "challenges"

    const val INSTALLATION_BY_REFERRAL = "INSTALLATION_BY_REFERRAL"
    const val LOGIN_BY_REFERRAL = "LOGIN_BY_REFERRAL"
    const val REGISTRATION_BY_REFERRAL = "REGISTRATION_BY_REFERRAL"
    const val ALREADY_LOGGED_IN_BY_REFERRAL = "ALREADY_LOGGED_IN_BY_REFERRAL"
    const val LOGIN_BY_DARWINBOX = "LOGIN_BY_DARWINBOX"
    const val REGISTRATION_BY_DARWINBOX = "REGISTRATION_BY_DARWINBOX"
    const val REFERRAL = "Referral"
    const val DARWINBOX = "Darwinbox"
    const val DARWINBOX_URL = "DarwinboxUrl"

    const val BOI = "BOI"
    const val UBI = "UBI"
    const val SUD_LIFE = "SUD_LIFE"
    const val CUSTOMER = "CUSTOMER"
    const val USER_TYPE = "USER_TYPE"
    const val SUD_ORG_NAME = "SUDLIFE"

    object UserConstants {
        const val EMAIL_ADDRESS = "EmailAddress"
        const val PASSWORD = "Password"

        //const val GENDER = "GENDER"
        const val GENDER = "Gender"
        const val AUTH_TYPE = "AuthenticationType"
        const val PHONE_NUMBER = "PhoneNumber"
        const val PARTNER_CODE = "PartnerCode"
        const val OTP = "OTP"
        const val CLUSTER_CODE = "ClusterCode"
        const val NAME = "Name"
        const val FIRST_NAME = "FirstName"
        const val LAST_NAME = "LastName"
        const val DOB = "DOB"
        const val EMPLOYEE_ID = "EmployeeID"
        const val ORG_NAME = "OrgName"
        const val SOURCE = "Source"
        const val MEDIUM = "Medium"
        const val HANDSHAKE = "HandShake"
        const val CLIENT_KEY = "ClientKey"
        const val CLIENT_USER_ID = "ClientUserId"
        const val CLIENT_APP_BUNDLE_ID = "ClientAppBundleId"
    }

    const val HOME_DRAWER = 1511
    const val TRACK_DRAWER = 1512
    const val YOUR_POLICY_DRAWER = 1513
    const val PROFILE_DRAWER = 1514
    const val REFER_A_FRIEND_DRAWER = 1515
    const val HOW_IT_WORKS_DRAWER = 1516
    const val SETTINGS_DRAWER = 1517
    const val LANGUAGE_DRAWER = 1518
    const val PRIVACY_POLICY_DRAWER = 1519
    const val TERMS_AND_CONDITIONS_DRAWER = 1520
    const val LOGOUT_DRAWER = 1521

    const val SHARE_BANNER = 8461
    const val CLICK_TO_KNOW_MORE = 8462
    const val CLOSE_DIALOG = 8463

    const val SPAN_ONE = 1250
    const val SPAN_TWO = 1251
    const val SPAN_THREE = 1252

    object Drawable {
        const val END = 2
        const val TOP = 1
        const val START = 0
        const val BOTTOM = 3
    }

    const val BANNER_AD = "BANNER_AD"

}

object ApiConstants {
    const val TRACK_PARAM_LIST_MASTER = "trackParamListMaster"
    const val DOC_TYPE_MASTER = "docTypeMaster"
    const val PARAMETER_HISTORY = "paramHistory"

    const val BMI_HISTORY = "bmiHistory"
    const val BLOOD_PRESSURE_HISTORY = "bloodPressureHistory"
    const val WHR_HISTORY = "whrHistory"
    const val VITALS_HISTORY = "vitalsHistory"

    const val RELATIVE_LIST = "relativeList"
    const val STEPS_GOAL = "stepsGoal"
    const val MEDICAL_PROFILE_SUMMERY = "medicalProfileSummery"
    const val MEDICATION_LIST = "medicationList"
}

object FirebaseConstants {
    const val SEND_OTP_SUCCESSFUL_EVENT = "send_otp_successful"
    const val SEND_OTP_FAIL_EVENT = "send_otp_fail"
    const val SEND_OTP_VERIFICATION_SUCCESSFUL_EVENT = "otp_verification_successful"
    const val SEND_OTP_VERIFICATION_FAIL_EVENT = "otp_verification_fail"
    const val RESEND_OTP_EVENT = "resend_otp"

    const val HRA_INITIATED_EVENT = "hra_initiated"
    const val HRA_COMPLETED_EVENT = "hra_completed"
    const val FAMILY_MEMBER_ADD_EVENT = "family_member_add"
    const val HEALTH_RECORDS_UPLOAD_EVENT = "health_records_uploaded"
    const val MEDICINE_UPLOAD_EVENT = "medicine_uploaded"
    const val HEALTH_PARAM_UPLOAD_EVENT = "health_parameter_uploaded"

    const val SPREAD_THE_WORD_CLICK = "spread_the_word_click"
    const val RATE_US_CLICK = "rate_us_click"
    const val LOGOUT_CLICK = "logout_click"

    // Screen event
    const val ACTIVITY_TRACKER_SCREEN = "activity_tracker_screen"
    const val MEDICINE_TRACKER_SCREEN = "medicine_tracker_screen"
    const val HEALTH_PARAMETERS_TRACKER_SCREEN = "health_parameters_tracker_screen"
    const val HEALTH_RECORDS_SCREEN = "health_records_screen"
    const val TOOLS_CALCULATORS_SCREEN = "tools_calculators_screen"
    const val HEART_AGE_BMI_CALCULATOR_SCREEN = "heart_age_bmi_calculator_screen"
    const val HEART_AGE_LIPID_CALCULATOR_SCREEN = "heart_age_lipid_calculator_screen"
    const val HEART_AGE_SUMMERY_SCREEN = "heart_age_summery_screen"
    const val HEART_AGE_DETAIL_SUMMERY_SCREEN = "heart_age_detail_summery_screen"
    const val HEART_AGE_RECALCULATE_SCREEN = "heart_age_recalculate_screen"
    const val DIABETES_CALCULATOR_SCREEN = "diabetes_calculator_screen"
    const val HYPERTENSION_CALCULATOR_SCREEN = "hypertension_calculator_screen"
    const val STRESS_CALCULATOR_SCREEN = "stress_anxiety_calculator_screen"
    const val BLOGS_SCREEN = "blogs_screen"
    const val DUE_DATE_CALCULATOR_SCREEN = "due_date_calculator_screen"
    const val SMART_PHONE_CALCULATOR_SCREEN = "smart_phone_calculator_screen"
    const val AMAHA_DASHBOARD_SCREEN = "amaha_dashboard_screen"
    const val AMAHA_CHATBOT_SCREEN = "amaha_chatbot_screen"
    const val AMAHA_CONSULT_THERAPIST_SCREEN = "amaha_consult_therapist_screen"
    const val AMAHA_ASSESSMENT_SCREEN = "amaha_assessment_screen"
    const val AMAHA_AUDIOS_SCREEN = "amaha_audios_screen"
    const val AMAHA_VIDEOS_SCREEN = "amaha_videos_screen"
    const val AMAHA_BLOGS_SCREEN = "amaha_blogs_screen"
    const val WELLFIE_VITAL_SCAN_SCREEN = "wellfie_vital_scan_screen"
    const val WELLFIE_RESULT_SCREEN = "wellfie_result_screen"

    const val SETTINGS_SCREEN = "settings_screen"

    // New Events
    const val MY_PROFILE_SCREEN = "my_profile_screen"
    const val EDIT_PROFILE_SCREEN = "edit_profile_screen"
    const val START_HRA_SCREEN = "start_hra_screen"
    const val HRA_SUMMERY_SCREEN = "hra_summery_screen"
    const val GRAPHICAL_HISTORY_SCREEN = "graphical_history_screen"
    const val MONTHLY_HISTORY_SCREEN = "monthly_history_screen"
    const val PARAMETER_DASHBOARD_SCREEN = "parameter_dashboard_screen"
    const val RECORD_TYPE_SELECTION_SCREEN = "record_type_selection_screen"
    const val FILE_TYPE_SELECTION_SCREEN = "file_type_selection_screen"
    const val UPLOAD_RECORD_TO_FAMILY_MEMEBER_SCREEN = "upload_record_to_family_member_screen"
    const val VIEW_AND_SHARE_RECORDS_SCREEN = "view_and_share_record_screen"
    const val MEDICATION_DASHBOARD_SCREEN = "medication_dashboard_screen"
    const val MEDICATION_SEARCH_SCREEN = "medication_search_screen"
    const val ADD_MEDICATION_SCREEN = "add_medication_screen"
    const val MY_MEDICATION_SCREEN = "my_medication_screen"

    const val NOTIFICATION_RECEIVE = "notification_received"
    const val NOTIFICATION_CLICK = "notification_click"
}

object CleverTapConstants {
    const val SPLASH_SCREEN = "Splash_Screen"
    const val HOME_SCREEN = "Home_Screen"

    //Custom Properties
    const val NAME = "Name"
    const val IDENTITY = "Identity"
    const val EMAIL = "Email"
    const val PHONE = "Phone"
    const val GENDER = "Gender"
    const val DOB = "DOB"

    const val VALUE = "Value"
    const val FROM = "From"
    const val DASHBOARD = "Dashboard"
    const val FROM_LOGIN = "Login"
    const val FROM_SIGN_UP = "SignUp"
    const val BLOGS = "Blogs"
    const val PERSON_ID = "PersonID"
    const val CATEGORY_ID = "CategoryID"
    const val BLOG_ID = "BlogID"
    const val QUANTITY = "Quantity"
    const val TARGET = "Target"
    const val DRUG_ID = "DrugID"
    const val MEDICATION_ID = "MedicationID"
    const val MEDICINE_NAME = "MedicineName"
    const val POLICY_NUMBER = "PolicyNumber"
    const val SCAN = "Scan"
    const val RESCAN = "Rescan"
    const val SUBMIT = "Submit"
    const val HRA_SUBMIT = "HraSubmit"
    const val REFERRAL_NAME = "ReferralName"
    const val REFERRAL_PID = "ReferralPID"
    const val CAMPAIGN_NAME = "CampaignName"
    const val ADDITIONAL_PARAMETER_1 = "AdditionalParameter1"
    const val ADDITIONAL_PARAMETER_2 = "AdditionalParameter2"
    const val FROM_NOTIFICATION = "FromNotification"
    const val EMPLOYEE_ID = "EmployeeID"
    const val PRODUCT_CODE = "ProductCode"
    const val LANGUAGE = "Language"
    const val COMBINATION = "Combination"
    const val STATUS = "Status"

    const val PAGE_NUMBER = "PageNumber"
    const val DATA_AVAILABLE = "DataAvailable"
    const val USER_TYPE = "UserType"
    const val USER_CONSENT = "UserConsent"
    const val YES = "Yes"
    const val NO = "No"

    const val START = "Start"
    const val VIEW_RESULT = "ViewResult"

    const val DATE = "Date"
    const val SCORE = "Score"
    const val BADGE = "Badge"
    const val HRA_SCORE = "HraScore"
    const val AKTIVO_SCORE2 = "AktivoScore"
    const val AKTIVO_MIND_SCORE = "MindScore"
    const val AKTIVO_STEPS = "Steps"
    const val AKTIVO_SLEEP = "Sleep"
    const val AKTIVO_EXERCISE = "Exercise"
    const val AKTIVO_SEDENTARY = "Sedentary"
    const val AKTIVO_LIGHT_ACTIVITY = "LightActivity"

    const val SYSTOLIC_BP = "SystolicBP"
    const val DIASTOLIC_BP = "DiastolicBP"
    const val HEART_RATE = "HeartRate"
    const val BREATHING_RATE = "BreathingRate"
    const val OXYGEN = "Oxygen"
    const val STRESS = "Stress"
    const val BMI = "BMI"

    //Events
    const val LOGIN = "YM_Login"
    const val LOGIN_WITH_OTP = "YM_Login_with_OTP"
    const val LOGIN_WITH_GOOGLE = "YM_Login_with_Google"
    const val LOGIN_WITH_FACEBOOK = "YM_Login_with_Facebook"
    const val SIGN_UP = "YM_Sign_Up"
    const val LOGIN_WITH_DARWINBOX = "YM_Login_with_Darwinbox"
    const val SIGN_UP_WITH_DARWINBOX = "YM_Sign_Up_with_Darwinbox"
    const val FORGOT_PASSWORD = "YM_Forgot_Password"

    const val AKTIVO_HEALTH_DATA_INFO = "YM_Aktivo_Health_Data_Info"
    const val AKTIVO_DASHBOARD = "YM_Aktivo_Dashboard"
    const val AKTIVO_SCORE = "YM_Aktivo_Score"
    const val MIND_SCORE = "YM_Mind_Score"
    const val AKTIVO_BADGES = "YM_Aktivo_Badges"
    const val AKTIVO_CHALLENGES = "YM_Aktivo_Challenges"
    const val SALT_MPT_INFO = "YM_Salt_MPT_Info"
    const val SALT_MPT = "YM_Salt_MPT"
    const val SALT_MPT_SHARE = "YM_Salt_MPT_Share"
    const val SALT_VIDEOS = "YM_Salt_Videos"
    const val YM_SCAN_VITALS_DATA_INFO = "YM_Scan_Vitals_Data_Info"
    const val SCAN_YOUR_VITALS = "YM_Scan_Vitals"
    const val SCAN_VITALS_RESULT = "YM_Scan_Vitals_Result"
    const val CONSULT_THERAPIST = "YM_Consult_Therapist"
    const val HRA_DATA_INFO = "YM_HRA_Data_Info"
    const val HEALTH_RISK_ASSESSMENT = "YM_Health_Risk_Assessment"
    const val MEDITATION = "YM_Meditation"
    const val YOGA = "YM_Yoga"
    const val EXERCISE = "YM_Exercise"
    const val JOIN_LIVE_SESSION = "YM_Join_Live_Session"
    const val LEADERSHIP_EXPERIENCES = "YM_Leadership_Experiences"
    const val NIMEYA_FINTALK_FINSTAT = "YM_Nimeya_FinTalk_FinStat"
    const val YM_NIMEYA_RISKO_METER = "YM_Nimeya_Risko_Meter"
    const val YM_NIMEYA_PROTECTO_METER = "YM_Nimeya_Protecto_Meter"
    const val YM_NIMEYA_RETIRO_METER = "YM_Nimeya_Retiro_Meter"
    const val YM_NIMEYA_RISKO_METER_RESULT = "YM_Nimeya_Risko_Meter_Result"
    const val YM_NIMEYA_PROTECTO_METER_RESULT = "YM_Nimeya_Protecto_Meter_Result"
    const val YM_NIMEYA_RETIRO_METER_RESULT = "YM_Nimeya_Retiro_Meter_Result"

    const val EDUCATION = "YM_Education_Calculator"
    const val MARRIAGE = "YM_Marriage_Calculator"
    const val WEALTH = "YM_Wealth_Calculator"
    const val HOUSE = "YM_House_Calculator"
    const val RETIREMENT = "YM_Retirement_Calculator"
    const val HUMAN_VALUE = "YM_Human_value_Calculator"

    const val PHYSICAL_ACTIVITY_TRACKER = "YM_Physical_Activity_Tracker"
    const val HEALTH_LIBRARY = "YM_Health_Library"
    const val HYDRATION_TRACKER = "YM_Hydration_Tracker"
    const val TRACK_VITAL_PARAMETERS = "YM_Track_Vital_Parameters"
    const val MEDITATION_TRACKER = "YM_Meditation_Tracker"
    const val HEALTH_RECORDS = "YM_Health_Records"
    const val TOOLS_CALCULATORS = "YM_Tools_Calculators"
    const val SUD_POLICY = "YM_Sud_Policy"
    const val INVITE_APP = "YM_Invite_App"
    const val SHARE_CENTURION_BANNER = "YM_Share_Centurion_Banner"
    const val SHARE_SMART_HEALTH_PRODUCT_BANNER = "YM_Share_Smart_Health_Product_Banner"
    const val SETTINGS = "YM_Settings"
    const val LOGOUT = "YM_Logout"
    const val CHANGE_PASSWORD = "YM_Change_Password"
    const val DELETE_ACCOUNT = "YM_Delete_Account"

    const val MY_PROFILE_SCREEN = "YM_My_Profile_Screen"
    const val TERMS_CONDITIONS_SCREEN = "YM_Terms_Conditions_Screen"
    const val PRIVACY_POLICY_SCREEN = "YM_Privacy_Policy_Screen"

    const val TODAY_SCREEN = "YM_Today_Screen"
    const val WEEKLY_SCREEN = "YM_Weekly_Screen"
    const val MONTHLY_SCREEN = "YM_Monthly_Screen"
    const val YEARLY_SCREEN = "YM_Yearly_Screen"
    const val UPDATE_GOAL = "YM_Update_Goal"
    const val SWITCH_FITNESS_ACCOUNT = "YM_Switch_Fitness_Account"

    const val HRA_START = "YM_HRA_Start"
    const val HRA_COMPLETE = "YM_HRA_Complete"
    const val DOWNLOAD_HRA_REPORT = "YM_Download_HRA_Report"
    const val HRA_SUMMARY_SCREEN = "YM_HRA_Summary_Screen"

    const val BLOG_DETAILS_SCREEN = "YM_Blog_Details_Screen"
    const val BLOG_FOR_YOU_SCREEN = "YM_Blog_For_You_Screen"
    const val BLOG_ALL_BLOGS_SCREEN = "YM_Blog_All_Blogs_Screen"
    const val BLOG_CANCER_CARE_SCREEN = "YM_Blog_Cancer_Care_Screen"
    const val BLOG_COVID_19_SCREEN = "YM_Blog_COVID_19_Screen"
    const val BLOG_DIABETES_SCREEN = "YM_Blog_Diabetes_Screen"
    const val BLOG_ELDER_CARE_SCREEN = "YM_Blog_Elder_Care_Screen"
    const val BLOG_EMOTIONAL_HEALTH_SCREEN = "YM_Blog_Emotional_Health_Screen"
    const val BLOG_FITNESS_SCREEN = "YM_Blog_Fitness_Screen"
    const val BLOG_FOOD_AND_HEALTH_SCREEN = "YM_Blog_Food_And_Health_Screen"
    const val BLOG_GENERAL_HEALTH_SCREEN = "YM_Blog_General_Health_Screen"
    const val BLOG_HAIR_AND_SKIN_SCREEN = "YM_Blog_Hair_And_Skin_Screen"
    const val BLOG_HEART_CARE_SCREEN = "YM_Blog_Heart_Care_Screen"
    const val BLOG_LIFESTYLE_SCREEN = "YM_Blog_Lifestyle_Screen"
    const val BLOG_OCCUPATIONAL_HEALTH_SCREEN = "YM_Blog_Occupational_Health_Screen"
    const val BLOG_PARENTING_SCREEN = "YM_Blog_Parenting_Screen"
    const val BLOG_PREGNANCY_CARE_SCREEN = "YM_Blog_Pregnancy_Care_Screen"
    const val BLOG_WOMEN_HEALTH_SCREEN = "YM_Blog_Women_Health_Screen"

    const val LOG_WATER_INTAKE = "YM_Log_Water_Intake"
    const val DEFAULT_WATER_TARGET = "YM_Default_Water_Target"
    const val CUSTOM_WATER_TARGET = "YM_Custom_Water_Target"
    const val HYDRATION_TRACKER_DASHBOARD_SCREEN = "YM_Hydration_Tracker_Dashboard_Screen"
    const val TRACK_WATER_INTAKE_SCREEN = "YM_Track_Water_Intake_Screen"

    const val ADD_MEDICATION = "YM_Add_Medication"
    const val UPDATE_MEDICATION = "YM_Update_Medication"
    const val DELETE_MEDICATION = "YM_Delete_Medication"
    const val MEDICATION_DASHBOARD_SCREEN = "YM_Medication_Dashboard_Screen"
    const val MEDICATION_HISTORY_SCREEN = "YM_Medication_History_Screen"

    const val ADD_HEALTH_RECORD = "YM_Add_Health_Record"
    const val DELETE_HEALTH_RECORD = "YM_Delete_Health_Record"
    const val HEALTH_RECORDS_DASHBOARD_SCREEN = "YM_Health_Records_Dashboard_Screen"

    const val HEART_AGE_CALCULATOR_SCREEN = "YM_Heart_Age_Calculator_Screen"
    const val HEART_AGE_CALCULATOR_SUMMARY_SCREEN = "YM_Heart_Age_Calculator_Summary_Screen"
    const val DIABETES_CALCULATOR_SCREEN = "YM_Diabetes_Calculator_Screen"
    const val DIABETES_CALCULATOR_SUMMARY_SCREEN = "YM_Diabetes_Calculator_Summary_Screen"
    const val HYPERTENSION_CALCULATOR_SCREEN = "YM_Hypertension_Calculator_Screen"
    const val HYPERTENSION_CALCULATOR_SUMMARY_SCREEN = "YM_Hypertension_Calculator_Summary_Screen"
    const val STRESS_AND_ANXIETY_CALCULATOR_SCREEN = "YM_Stress_And_Anxiety_Calculator_Screen"
    const val STRESS_AND_ANXIETY_CALCULATOR_SUMMARY_SCREEN =
        "YM_Stress_And_Anxiety_Calculator_Summary_Screen"
    const val SMART_PHONE_ADDICTION_CALCULATOR_SCREEN = "YM_Smart_Phone_Addiction_Calculator_Screen"
    const val SMART_PHONE_ADDICTION_CALCULATOR_SUMMARY_SCREEN =
        "YM_Smart_Phone_Addiction_Calculator_Summary_Screen"
    const val DUE_DATE_CALCULATOR_SCREEN = "YM_Due_Date_Calculator_Screen"
    const val DUE_DATE_CALCULATOR_SUMMARY_SCREEN = "YM_Due_Date_Calculator_Summary_Screen"

    const val TRACK_PARAMETER_DASHBOARD_SCREEN = "YM_Track_Parameter_Dashboard_Screen"
    const val TRACK_PARAMETER_HISTORY_SCREEN = "YM_Track_Parameter_History_Screen"
    const val TRACK_PARAMETER_COMPLETE_HISTORY_SCREEN = "YM_Track_Parameter_Complete_History_Screen"
    const val TRACK_PARAMETER_UPDATE_SCREEN = "YM_Track_Parameter_Update_Screen"

    const val POLICY_PMJJBY_POLICY_DOWNLOAD = "YM_Policy_PMJJBY_Policy_Download"
    const val POLICY_GROUP_COI_POLICY_DOWNLOAD = "YM_Policy_Group_COI_Download"
    const val POLICY_CENTURY_ROYALE_BANNER = "YM_Policy_Century_Royale_Banner"
    const val POLICY_CENTURY_GOLD_BANNER = "YM_Policy_Century_Gold_Banner"
    const val POLICY_PROTECT_SHIELD_PLUS_BANNER = "YM_Policy_Protect_Shield_Plus_Banner"
    const val POLICY_SMART_HEALTH_PRODUCT_BANNER = "YM_Policy_Smart_Health_Product_Banner"
    const val POLICY_BANNER = "YM_Policy_Banner"
    const val POLICY_CENTURION_BANNER = "YM_Policy_Centurion_Banner"
    const val POLICY_SUD_CUSTOMER_CARE = "YM_Policy_Sud_Customer_Care"
    const val POLICY_SUD_WHATS_APP_BOT = "YM_Policy_Sud_Whats_App_Bot"
    const val POLICY_PAY_YOUR_PREMIUM = "YM_Policy_Pay_Your_Premium"
    const val POLICY_KNOW_YOUR_POLICY = "YM_Policy_Know_Your_Policy"
    const val POLICY_DASHBAORD_SCREEN = "YM_Policy_Dashbaord_Screen"
    const val POLICY_DETAILS_SCREEN = "YM_Policy_Details_Screen"
    const val POLICY_PREMIUM_RECEIPT_SCREEN = "YM_Policy_Premium_Receipt_Screen"

    const val REGISTRATION_BY_REFERRAL = "YM_Registration_by_Referral"
    const val LOGIN_BY_REFERRAL = "YM_Login_by_Referral"
    const val ALREADY_LOGGED_IN_BY_REFERRAL = "YM_Already_Logged_In_by_Referral"
    const val INSTALLATION_BY_REFERRAL = "YM_Installation_by_Referral"
    const val AF_CAMPAIGN = "YM_Af_Campaign"

    const val BOI_LOGIN = "YM_BOI_Login"
    const val BOI_SIGN_UP = "YM_BOI_Sign_Up"
    const val UBI_LOGIN = "YM_UBI_Login"
    const val UBI_SIGN_UP = "YM_UBI_Sign_Up"
    const val SUD_LIFE_LOGIN = "YM_SUD_LIFE_Login"
    const val SUD_LIFE_SIGN_UP = "YM_SUD_LIFE_Sign_Up"

    const val DASHBOARD_SCREEN = "YM_Dashboard"
    const val APP_LANGUAGE = "YM_App_Language"

    const val CUSTOMER_PORTAL_LINK = "YM_Customer_Portal_Link"
    const val WATER_INTAKE_TARGET_SCREEN = "YM_Water_Intake_Target_Screen"
    const val TRACK_PARAMETER_UPDATE = "YM_Track_Parameter_Update"
}