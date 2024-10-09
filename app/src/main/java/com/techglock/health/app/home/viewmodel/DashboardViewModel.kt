package com.techglock.health.app.home.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.*
import android.net.Uri
import android.os.Build
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.appsflyer.share.LinkGenerator
import com.appsflyer.share.ShareInviteHelper
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Configuration
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.utils.CalculateParameters
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Event
import com.techglock.health.app.common.utils.FileUtils
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.PreferenceUtils
import com.techglock.health.app.common.utils.UserSingleton
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.home.common.CalculatorModel
import com.techglock.health.app.home.common.DataHandler
import com.techglock.health.app.home.common.DataHandler.NavDrawerOption
import com.techglock.health.app.home.common.DownloadReportApiService
import com.techglock.health.app.home.common.HRAObservationModel
import com.techglock.health.app.home.common.SmitFitEventsService
import com.techglock.health.app.home.common.WellfieSingleton
import com.techglock.health.app.home.domain.AktivoManagementUseCase
import com.techglock.health.app.home.domain.BackgroundCallUseCase
import com.techglock.health.app.home.domain.HomeManagementUseCase
import com.techglock.health.app.home.domain.SudLifePolicyManagementUseCase
import com.techglock.health.app.home.ui.*
import com.techglock.health.app.home.ui.wellfie.BmiVitalsActivity
import com.techglock.health.app.model.aktivo.AktivoCreateUserModel
import com.techglock.health.app.model.aktivo.AktivoGetUserTokenModel
import com.techglock.health.app.model.blogs.BlogItem
import com.techglock.health.app.model.blogs.BlogModel
import com.techglock.health.app.model.blogs.BlogRecommendationListModel
import com.techglock.health.app.model.blogs.BlogsListAllModel
import com.techglock.health.app.model.entity.HRASummary
import com.techglock.health.app.model.entity.UserRelatives
import com.techglock.health.app.model.entity.Users
import com.techglock.health.app.model.fitness.GetStepsGoalModel
import com.techglock.health.app.model.home.AddFeatureAccessLog
import com.techglock.health.app.model.home.ContactUsModel
import com.techglock.health.app.model.home.EventsBannerModel
import com.techglock.health.app.model.home.GetSSOUrlModel
import com.techglock.health.app.model.home.LiveSessionModel
import com.techglock.health.app.model.home.PasswordChangeModel
import com.techglock.health.app.model.home.ProfileImageModel
import com.techglock.health.app.model.home.SaveCloudMessagingIdModel
import com.techglock.health.app.model.home.SaveFeedbackModel
import com.techglock.health.app.model.home.WellfieGetSSOUrlModel
import com.techglock.health.app.model.home.WellfieGetVitalsModel
import com.techglock.health.app.model.home.WellfieListVitalsModel
import com.techglock.health.app.model.home.WellfieSaveVitalsModel
import com.techglock.health.app.model.hra.HraMedicalProfileSummaryModel
import com.techglock.health.app.model.parameter.SaveParameterModel
import com.techglock.health.app.model.sudLifePolicy.PolicyProductsModel
import com.techglock.health.app.model.waterTracker.GetDailyWaterIntakeModel
import com.techglock.health.app.repository.utils.Resource
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class DashboardViewModel @Inject constructor(
    application: Application,
    private val homeManagementUseCase: HomeManagementUseCase,
    private val backgroundCallUseCase: BackgroundCallUseCase,
    private val sudLifePolicyManagementUseCase: SudLifePolicyManagementUseCase,
    private val aktivoManagementUseCase: AktivoManagementUseCase,
    private val preferenceUtils: PreferenceUtils,
    private val dataHandler: DataHandler,
    val context: Context?
) : BaseViewModel(application) {

    private val localResource =
        LocaleHelper.getLocalizedResources(context!!, Locale(LocaleHelper.getLanguage(context)))!!
    private val fileUtils = FileUtils
    val preferenceUtil = preferenceUtils
    private val wellfieSingleton = WellfieSingleton.getInstance()!!

    var adminPersonId = preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0")
    var personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
    var accountID = preferenceUtils.getPreference(PreferenceConstants.ACCOUNTID, "0")
    var email = preferenceUtils.getPreference(PreferenceConstants.EMAIL, "")
    var phone = preferenceUtils.getPreference(PreferenceConstants.PHONE, "")
    var firstName = preferenceUtils.getPreference(PreferenceConstants.FIRSTNAME, "")
    var gender = preferenceUtils.getPreference(PreferenceConstants.GENDER, "")
    var authToken = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
    var relationshipCode = preferenceUtils.getPreference(PreferenceConstants.RELATIONSHIPCODE, "")
    var profileImageID = preferenceUtils.getPreference(PreferenceConstants.PROFILE_IMAGE_ID, "")

    //var age = DateHelper.calculatePersonAge(preferenceUtils.getPreference(PreferenceConstants.DOB, ""),context!!)
    var age = if ( !Utilities.isNullOrEmpty(preferenceUtils.getPreference(PreferenceConstants.DOB, "")) ) {
        DateHelper.calculatePersonAge(preferenceUtils.getPreference(PreferenceConstants.DOB, ""),context!!)
    } else {
        ""
    }
    var userDetails = MutableLiveData<Users>()
    val userRelativesList = MutableLiveData<List<UserRelatives>>()
    val navDrawerOptionList = MutableLiveData<List<NavDrawerOption>>()
    var hraDetails = MutableLiveData<HRASummary>()
    val postProcessResponse = MutableLiveData<String>()
    val liveSessions = MutableLiveData<List<LiveSessionModel>>()
    var healthBlogList = MutableLiveData<List<BlogItem>>()
    var trackersList = MutableLiveData<List<CalculatorModel>>()
    var hraObservationLiveData = MutableLiveData<HRAObservationModel>()
    var healthBlogSuggestionList = MutableLiveData<List<BlogItem>>()

    var saveCloudMessagingIdSource: LiveData<Resource<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>> = MutableLiveData()
    val _saveCloudMessagingId = MediatorLiveData<Resource<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>>()
    val saveCloudMessagingId: LiveData<Resource<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>> get() = _saveCloudMessagingId

    var medicalProfileSummarySource: LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> = MutableLiveData()
    val _medicalProfileSummary = MediatorLiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>>()
    val medicalProfileSummary: LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> get() = _medicalProfileSummary

    private var passwordChangeSource: LiveData<Resource<PasswordChangeModel.ChangePasswordResponse>> = MutableLiveData()
    private val _passwordChange = MediatorLiveData<Resource<PasswordChangeModel.ChangePasswordResponse>>()
    val passwordChange: LiveData<Resource<PasswordChangeModel.ChangePasswordResponse>> get() = _passwordChange

    private var eventsBannerSource: LiveData<Resource<EventsBannerModel.EventsBannerResponse>> = MutableLiveData()
    private val _eventsBanner = MediatorLiveData<Resource<EventsBannerModel.EventsBannerResponse>>()
    val eventsBanner: LiveData<Resource<EventsBannerModel.EventsBannerResponse>> get() = _eventsBanner

    private var policyProductsSource: LiveData<Resource<PolicyProductsModel.PolicyProductsResponse>> = MutableLiveData()
    private val _policyProducts = MediatorLiveData<Resource<PolicyProductsModel.PolicyProductsResponse>>()
    val policyProducts: LiveData<Resource<PolicyProductsModel.PolicyProductsResponse>> get() = _policyProducts

    private var addFeatureAccessLogSource: LiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>> = MutableLiveData()
    private val _addFeatureAccessLog = MediatorLiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>>()
    val addFeatureAccessLog: LiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>> get() = _addFeatureAccessLog

    private var getSSOUrlSource: LiveData<Resource<GetSSOUrlModel.GetSSOUrlResponse>> = MutableLiveData()
    private val _getSSOUrl = MediatorLiveData<Resource<GetSSOUrlModel.GetSSOUrlResponse>>()
    val getSSOUrl: LiveData<Resource<GetSSOUrlModel.GetSSOUrlResponse>> get() = _getSSOUrl

    var saveParamUserSource: LiveData<Resource<SaveParameterModel.Response>> = MutableLiveData()
    private val _saveParam = MediatorLiveData<Resource<SaveParameterModel.Response>>()
    val saveParam: LiveData<Resource<SaveParameterModel.Response>> get() = _saveParam

    private var wellfieSaveVitalsSource: LiveData<Resource<WellfieSaveVitalsModel.WellfieSaveVitalsResponse>> = MutableLiveData()
    private val _wellfieSaveVitals = MediatorLiveData<Resource<WellfieSaveVitalsModel.WellfieSaveVitalsResponse>>()
    val wellfieSaveVitals: LiveData<Resource<WellfieSaveVitalsModel.WellfieSaveVitalsResponse>> get() = _wellfieSaveVitals

    private var wellfieGetVitalsSource: LiveData<Resource<WellfieGetVitalsModel.WellfieGetVitalsResponse>> = MutableLiveData()
    private val _wellfieGetVitals = MediatorLiveData<Resource<WellfieGetVitalsModel.WellfieGetVitalsResponse>>()
    val wellfieGetVitals: LiveData<Resource<WellfieGetVitalsModel.WellfieGetVitalsResponse>> get() = _wellfieGetVitals

    private var wellfieGetSSOUrlSource: LiveData<Resource<WellfieGetSSOUrlModel.WellfieGetSSOUrlResponse>> = MutableLiveData()
    private val _wellfieGetSSOUrl = MediatorLiveData<Resource<WellfieGetSSOUrlModel.WellfieGetSSOUrlResponse>>()
    val wellfieGetSSOUrl: LiveData<Resource<WellfieGetSSOUrlModel.WellfieGetSSOUrlResponse>> get() = _wellfieGetSSOUrl

    var getStepsGoalSource: LiveData<Resource<GetStepsGoalModel.Response>> = MutableLiveData()
    val _getStepsGoal = MediatorLiveData<Resource<GetStepsGoalModel.Response>>()
    val getStepsGoal: LiveData<Resource<GetStepsGoalModel.Response>> get() = _getStepsGoal

    private var getDailyWaterIntakeSource: LiveData<Resource<GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse>> = MutableLiveData()
    private val _getDailyWaterIntake = MediatorLiveData<Resource<GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse>>()
    val getDailyWaterIntake: LiveData<Resource<GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse>> get() = _getDailyWaterIntake

    private var aktivoCreateUserSource: LiveData<Resource<AktivoCreateUserModel.AktivoCreateUserResponse>> = MutableLiveData()
    private val _aktivoCreateUser = MediatorLiveData<Resource<AktivoCreateUserModel.AktivoCreateUserResponse>>()
    val aktivoCreateUser: LiveData<Resource<AktivoCreateUserModel.AktivoCreateUserResponse>> get() = _aktivoCreateUser

    private var aktivoGetUserTokenSource: LiveData<Resource<AktivoGetUserTokenModel.AktivoGetUserTokenResponse>> = MutableLiveData()
    private val _aktivoGetUserToken = MediatorLiveData<Resource<AktivoGetUserTokenModel.AktivoGetUserTokenResponse>>()
    val aktivoGetUserToken: LiveData<Resource<AktivoGetUserTokenModel.AktivoGetUserTokenResponse>> get() = _aktivoGetUserToken

    private var blogListSource: LiveData<Resource<BlogsListAllModel.BlogsListAllResponse>> = MutableLiveData()
    private val _blogList = MediatorLiveData<Resource<BlogsListAllModel.BlogsListAllResponse>>()
    val blogList: LiveData<Resource<BlogsListAllModel.BlogsListAllResponse>> get() = _blogList

    private var blogsRelatedSource: LiveData<Resource<BlogRecommendationListModel.BlogsResponse>> = MutableLiveData()
    private val _blogsRelated = MediatorLiveData<Resource<BlogRecommendationListModel.BlogsResponse>>()
    val blogsRelated: LiveData<Resource<BlogRecommendationListModel.BlogsResponse>> get() = _blogsRelated

    private var saveFeedbackSource: LiveData<Resource<SaveFeedbackModel.SaveFeedbackResponse>> =
        MutableLiveData()
    private val _saveFeedback = MediatorLiveData<Resource<SaveFeedbackModel.SaveFeedbackResponse>>()
    val saveFeedback: LiveData<Resource<SaveFeedbackModel.SaveFeedbackResponse>> get() = _saveFeedback

    private var contactUsSource: LiveData<Resource<ContactUsModel.ContactUsResponse>> = MutableLiveData()
    private val _contactUs = MediatorLiveData<Resource<ContactUsModel.ContactUsResponse>>()
    val contactUs: LiveData<Resource<ContactUsModel.ContactUsResponse>> get() = _contactUs

    private var wellfieListVitalsSource: LiveData<Resource<WellfieListVitalsModel.WellfieListVitalsResponse>> = MutableLiveData()
    private val _wellfieListVitals = MediatorLiveData<Resource<WellfieListVitalsModel.WellfieListVitalsResponse>>()
    val wellfieListVitals: LiveData<Resource<WellfieListVitalsModel.WellfieListVitalsResponse>> get() = _wellfieListVitals

    private var profileImageSource: LiveData<Resource<ProfileImageModel.ProfileImageResponse>> = MutableLiveData()
    private val _profileImage = MediatorLiveData<Resource<ProfileImageModel.ProfileImageResponse>>()
    val profileImage: LiveData<Resource<ProfileImageModel.ProfileImageResponse>> get() = _profileImage

    fun getAllTrackersList() {
        trackersList.postValue(dataHandler.getTrackersList())
    }

    fun getMainUserPersonID(): String {
        return preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0")
    }

    fun getLoggedInPersonDetails() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            userDetails.postValue(homeManagementUseCase.invokeGetLoggedInPersonDetails())
        }
    }

    private fun refreshFcmToken(newToken: String) {
        preferenceUtils.storePreference(PreferenceConstants.FCM_REGISTRATION_ID, newToken)
    }

    fun logoutFromDB() = viewModelScope.launch(Dispatchers.Main) {
        Configuration.EntityID = "0"
        withContext(Dispatchers.IO) {
            backgroundCallUseCase.invokeLogout()
        }
    }

    fun isSelfUser(): Boolean {
        val personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
        val adminPersonId = preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0")
        Utilities.printLogError("PersonId--->$personId")
        Utilities.printLogError("AdminPersonId--->$adminPersonId")
        var isSelfUser = false
        if (!Utilities.isNullOrEmptyOrZero(personId)
            && !Utilities.isNullOrEmptyOrZero(adminPersonId)
            && personId == adminPersonId
        ) {
            isSelfUser = true
        }
        return isSelfUser
    }

    fun setDrawerUserDetails(
        imgUser: ImageView,
        txtUsername: TextView,
        txtUserEmail: TextView,
        imgSud: ImageView,
        imgBoi: ImageView,
        imgUbi: ImageView
    ) {
        txtUsername.text = preferenceUtils.getPreference(PreferenceConstants.FIRSTNAME, "")
        //txtUserEmail.text = preferenceUtils.getPreference(PreferenceConstants.EMAIL, "")
        txtUserEmail.text = Utilities.getRelationshipByRelationshipCode(
            preferenceUtils.getPreference(
                PreferenceConstants.RELATIONSHIPCODE,
                ""
            ), context!!
        )
        val profPicBitmap = UserSingleton.getInstance()!!.profPicBitmap
        if (isSelfUser() && profPicBitmap != null) {
            imgUser.setImageBitmap(profPicBitmap)
        } else {
            imgUser.setImageResource(
                Utilities.getRelativeImgIdWithGender(
                    preferenceUtils.getPreference(PreferenceConstants.RELATIONSHIPCODE, ""),
                    preferenceUtils.getPreference(PreferenceConstants.GENDER, "")
                )
            )
        }
        when (Utilities.getEmployeeType()) {
            Constants.SUD_LIFE -> {
                imgSud.visibility = View.VISIBLE
                imgBoi.visibility = View.GONE
                imgUbi.visibility = View.GONE
            }

            Constants.BOI -> {
                imgSud.visibility = View.GONE
                imgBoi.visibility = View.VISIBLE
                imgUbi.visibility = View.GONE
            }

            Constants.UBI -> {
                imgSud.visibility = View.GONE
                imgBoi.visibility = View.GONE
                imgUbi.visibility = View.VISIBLE
            }

            else -> {
                imgSud.visibility = View.GONE
                imgBoi.visibility = View.GONE
                imgUbi.visibility = View.GONE
            }
        }
    }

    fun getUserRelatives() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
/*            val list = homeManagementUseCase.invokeGetUserRelatives().toMutableList()
            // Set Selected Member to First in the List
            val relative = list.first { it.relativeID == sharedPref.getString(PreferenceConstants.PERSONID,"") }
            list.remove(relative)
            list.add(0,relative)
            Utilities.printLogError("Updated Relative List--->$list")*/
            userRelativesList.postValue(homeManagementUseCase.invokeGetUserRelatives())
        }
    }

    fun getDrawerOptionList() {
        if (isSelfUser()) {
            navDrawerOptionList.postValue(dataHandler.getNavDrawerList())
        } else {
            navDrawerOptionList.postValue(dataHandler.getSwitchProfileNavDrawerList())
        }
    }

    fun setHraSummaryDetails(hraSummary: HRASummary) = viewModelScope.launch(Dispatchers.Main) {
        withContext(Dispatchers.IO) {
            hraDetails.postValue(hraSummary)
            //hraDetails.postValue(homeManagementUseCase.invokeGetHraSummaryDetails())
        }
    }

    fun setFirstTimeHomeVisitFlag(flag: Boolean) {
        preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_FIRST_HOME_VISIT, flag)
    }

    fun isFirstTimeHomeVisit(): Boolean {
        return preferenceUtils.getBooleanPreference(PreferenceConstants.IS_FIRST_HOME_VISIT, true)
    }

    fun getWellfieStatusMsg(code: String, apiMsg: String): String {
        return dataHandler.getWellfieStatusMsg(code, apiMsg)
    }

    fun getOtpAuthenticatedStatus(): Boolean {
        val isOtpAuthenticated =
            preferenceUtils.getBooleanPreference(PreferenceConstants.IS_OTP_AUTHENTICATED, false)
        Utilities.printLogError("IsOtpAuthenticated--->$isOtpAuthenticated")
        return isOtpAuthenticated
    }

    fun setupHRAWidgetData(hraSummary: HRASummary?) {
        try {
            var hraObservation = 0
            var color = R.color.dia_ichi_grey_dark
            var observation = localResource.getString(R.string.TAKE_YOUR_HRA)
            if (hraSummary!!.hraCutOff == "1") {
                hraObservation = hraSummary.scorePercentile.toInt()
            } else {
                hraObservation = 0
                observation = localResource.getString(R.string.TAKE_YOUR_HRA)
            }

            if (hraSummary != null) {
                var wellnessScore = 0
                var hraCutOff = ""
                var currentHRAHistoryID = ""
                wellnessScore = hraSummary.scorePercentile.toInt()
                hraCutOff = hraSummary.hraCutOff
                currentHRAHistoryID = hraSummary.currentHRAHistoryID.toString()
                if (wellnessScore <= 0) {
                    wellnessScore = 0
                }

                if (!Utilities.isNullOrEmpty(currentHRAHistoryID) && !currentHRAHistoryID.equals(
                        "0",
                        ignoreCase = true
                    )
                ) {
                    when {
                        hraCutOff.equals("0", ignoreCase = true) -> {
                            observation = localResource.getString(R.string.TAKE_YOUR_HRA)
                            color = R.color.dia_ichi_grey_dark
                        }

                        wellnessScore in 0..15 -> {
                            observation = localResource.getString(R.string.HIGH_RISK)
                            color = R.color.risk_high
                        }

                        wellnessScore in 16..45 -> {
                            observation = localResource.getString(R.string.MODERATE_RISK)
                            color = R.color.risk_moderate
                        }

                        wellnessScore in 46..85 -> {
                            observation = localResource.getString(R.string.HEALTHY)
                            color = R.color.risk_healthy
                        }

                        wellnessScore > 85 -> {
                            observation = localResource.getString(R.string.OPTIMUM)
                            color = R.color.risk_optimum

                        }
                    }
                } else {
                    color = R.color.dark_gray
                    observation = localResource.getString(R.string.TAKE_YOUR_HRA)
                }
            } else {
                color = R.color.dark_gray
                observation = localResource.getString(R.string.TAKE_YOUR_HRA)
            }
            HRAData.data = HRAObservationModel(color, observation, hraObservation)
            hraObservationLiveData.postValue(
                HRAObservationModel(
                    color,
                    observation,
                    hraObservation
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun navigateToHospitalsNearMe(context: Context) {
        try {
            val gmmIntentUri = Uri.parse("geo:0,0?z=10&q=hospital near me")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            context.startActivity(mapIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + "com.google.android.apps.maps")
                )
            )
        }
    }

    fun switchProfile(userRelative: UserRelatives) {
        preferenceUtils.storePreference(PreferenceConstants.PERSONID, userRelative.relativeID)
        preferenceUtils.storePreference(
            PreferenceConstants.RELATIONSHIPCODE,
            userRelative.relationshipCode
        )
        preferenceUtils.storePreference(PreferenceConstants.EMAIL, userRelative.emailAddress)
        preferenceUtils.storePreference(PreferenceConstants.PHONE, userRelative.contactNo)
        preferenceUtils.storePreference(PreferenceConstants.FIRSTNAME, userRelative.firstName)
        preferenceUtils.storePreference(PreferenceConstants.GENDER, userRelative.gender)
        preferenceUtils.storePreference(PreferenceConstants.AGE, userRelative.age)
        preferenceUtils.storePreference(PreferenceConstants.DOB, userRelative.dateOfBirth)
        clearTablesForSwitchProfile()
    }

    private fun clearTablesForSwitchProfile() = viewModelScope.launch(Dispatchers.Main) {
        withContext(Dispatchers.IO) {
            homeManagementUseCase.invokeClearTablesForSwitchProfile()
        }
    }

    fun refreshPersonId() {
        personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "")
        email = preferenceUtils.getPreference(PreferenceConstants.EMAIL, "")
        phone = preferenceUtils.getPreference(PreferenceConstants.PHONE, "")
        firstName = preferenceUtils.getPreference(PreferenceConstants.FIRSTNAME, "")
        gender = preferenceUtils.getPreference(PreferenceConstants.GENDER, "")
        relationshipCode = preferenceUtils.getPreference(PreferenceConstants.RELATIONSHIPCODE, "")
    }

    fun callSaveCloudMessagingIdApi(newFcmToken: String, forceRefresh: Boolean) =
        viewModelScope.launch(Dispatchers.Main) {

            Utilities.printLogError("\nOldFcmToken--->${preferenceUtils.getPreference(PreferenceConstants.FCM_REGISTRATION_ID, "")}")
            //if (Utilities.isNullOrEmpty(fcmToken) || newFcmToken != fcmToken) {

            Utilities.printLog("\n*************Sending RegistrationId to Server*************")
            Utilities.printLog("\nRegistrationID----->$newFcmToken")
            _saveCloudMessagingId.removeSource(saveCloudMessagingIdSource)
            withContext(Dispatchers.IO) {
                try {
                    //var adminUser = useCase.invokeGetLoggedInPersonDetails()
                    val requestData = SaveCloudMessagingIdModel(
                        Gson().toJson(
                            SaveCloudMessagingIdModel.JSONDataRequest(
                                key = SaveCloudMessagingIdModel.Key(
                                    accountID = preferenceUtils.getPreference(PreferenceConstants.ACCOUNTID, ""),
                                    registrationID = newFcmToken,
                                    userMetaData = SaveCloudMessagingIdModel.UserMetaData(
                                        name = preferenceUtils.getPreference(PreferenceConstants.FIRSTNAME, ""),
                                        email = preferenceUtils.getPreference(PreferenceConstants.EMAIL, ""),
                                        contact = preferenceUtils.getPreference(PreferenceConstants.PHONE, ""),
                                        gender = if (preferenceUtils.getPreference(PreferenceConstants.GENDER, "") == "1") "Male" else "Female",
                                        dateofbirth = preferenceUtils.getPreference(PreferenceConstants.DOB, "")
                                    )
                                )
                            ), SaveCloudMessagingIdModel.JSONDataRequest::class.java
                        ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))
                    saveCloudMessagingIdSource = backgroundCallUseCase.invokeSaveCloudMessagingId(
                        isForceRefresh = forceRefresh,
                        data = requestData
                    )
                } catch (e: Exception) {
                    Utilities.printException(e)
                }
            }
            _saveCloudMessagingId.addSource(saveCloudMessagingIdSource) {
                try {
                    _saveCloudMessagingId.value = it
                    it.data?.let { data ->
                        when (it.status) {
                            Resource.Status.SUCCESS -> {
                                val registrationID = data.registrationID
                                when {
                                    !Utilities.isNullOrEmpty(registrationID) -> {
                                        refreshFcmToken(registrationID)
                                        Utilities.printLogError("\nRefreshedFcmToken--->$registrationID")
                                    }

                                    else -> {

                                    }
                                }
                            }

                            Resource.Status.ERROR -> {
                                if (it.errorNumber.equals("1100014", true)) {
                                    _sessionError.value = Event(true)
                                    logoutFromDB()
                                } else {
                                    //toastMessage(it.errorMessage)
                                }
                            }

                            else -> {}
                        }
                    }
                } catch (e: Exception) {
                    Utilities.printException(e)
                }

            }
            //}
        }

    fun getMedicalProfileSummary() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = HraMedicalProfileSummaryModel(Gson().toJson(
            HraMedicalProfileSummaryModel.JSONDataRequest(
                PersonID = (preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0").ifEmpty { "0" })
            ), HraMedicalProfileSummaryModel.JSONDataRequest::class.java
        ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _medicalProfileSummary.removeSource(medicalProfileSummarySource)
        withContext(Dispatchers.IO) {
            medicalProfileSummarySource = backgroundCallUseCase.invokeMedicalProfileSummary(
                isForceRefresh = false,
                data = requestData,
                personId = (preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0").ifEmpty { "0" })
            )
        }
        _medicalProfileSummary.addSource(medicalProfileSummarySource) {
            try {
                _medicalProfileSummary.value = it
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        Utilities.printLogError("MedicalProfileSummery=>Inside view model::  $it")
                        var wellnessScore = 0
                        val abc = it.data!!.MedicalProfileSummary!!.scorePercentile
                        if ( !Utilities.isNullOrEmptyOrZero(abc!!.toString()) ) {
                            wellnessScore = abc.toInt()
                            if (wellnessScore <= 0) {
                                wellnessScore = 0
                            }
                        }
                        val hraData = HashMap<String, Any>()
                        hraData[CleverTapConstants.HRA_SCORE] = wellnessScore
                        CleverTapHelper.pushEventWithProperties(context,
                            CleverTapConstants.HRA_DATA_INFO,hraData)
                    }

                    Resource.Status.ERROR -> {
                        if (it.errorNumber.equals("1100014", true)) {
                            _sessionError.value = Event(true)
                            logoutFromDB()
                        } else {
                            //toastMessage(it.errorMessage)
                        }
                    }

                    else -> {}
                }
            } catch (e: Exception) {
                Utilities.printException(e)
            }

        }
    }

    fun callPasswordChangeApi(
        oldPassword: String,
        newPassword: String,
        activity: PasswordChangeActivity
    ) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = PasswordChangeModel(
            Gson().toJson(
                PasswordChangeModel.JSONDataRequest(
                    loginName = email,
                    emailAddress = email,
                    oldPassword = oldPassword,
                    newPassword = newPassword
                ), PasswordChangeModel.JSONDataRequest::class.java
            ), authToken
        )

        _progressBar.value = Event("Updating Password...")
        _passwordChange.removeSource(passwordChangeSource)
        withContext(Dispatchers.IO) {
            passwordChangeSource = homeManagementUseCase.invokePasswordChange(
                isForceRefresh = true,
                data = requestData
            )
        }
        _passwordChange.addSource(passwordChangeSource) {
            _passwordChange.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    if (newPassword.equals(it.data.newPassword, ignoreCase = true)) {
                        activity.showPasswordUpdatedDialog()
                    }
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                when {
                    it.errorNumber.equals("1100014", true) -> {
                        _sessionError.value = Event(true)
                    }

                    it.errorNumber.equals("1100005", true) -> {
                        toastMessage(activity.resources.getString(R.string.ERROR_INVALID_OLD_PASSWORD))
                    }

                    else -> {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        }

    }

    fun callEventsBannerApi() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = EventsBannerModel(
            Gson().toJson(
                EventsBannerModel.JSONDataRequest(
                    personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                        .toInt(),
                    accountId = preferenceUtils.getPreference(PreferenceConstants.ACCOUNTID, "0")
                        .toInt(),
                    orgEmpID = preferenceUtils.getPreference(
                        PreferenceConstants.ORG_EMPLOYEE_ID,
                        ""
                    ),
                    orgName = preferenceUtils.getPreference(PreferenceConstants.ORG_NAME, ""),
                ), EventsBannerModel.JSONDataRequest::class.java
            ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        //_progressBar.value = Event("")
        _eventsBanner.removeSource(eventsBannerSource)
        withContext(Dispatchers.IO) {
            eventsBannerSource = homeManagementUseCase.invokeEventsBanner(data = requestData)
        }
        _eventsBanner.addSource(eventsBannerSource) {
            _eventsBanner.value = it

            if (it.status == Resource.Status.SUCCESS) {
                //_progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    Utilities.printData("EventsBanner", it.data)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                //_progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    //toastMessage(it.errorMessage)
                }
            }
        }

    }

    fun callPolicyProductsApi(fragment: HomeScreenFragment) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = PolicyProductsModel(
                Gson().toJson(
                    PolicyProductsModel.JSONDataRequest(
                        screen = "DASHBOARD",
                        personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                            .toInt(),
                        accountId = preferenceUtils.getPreference(
                            PreferenceConstants.ACCOUNTID,
                            "0"
                        ).toInt(),
                        orgEmpID = preferenceUtils.getPreference(
                            PreferenceConstants.ORG_EMPLOYEE_ID,
                            ""
                        ),
                        orgName = preferenceUtils.getPreference(PreferenceConstants.ORG_NAME, ""),
                        userType = Utilities.getEmployeeType()
                    ),
                    PolicyProductsModel.JSONDataRequest::class.java
                ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            //_progressBar.value = Event("")
            _policyProducts.removeSource(policyProductsSource)
            withContext(Dispatchers.IO) {
                policyProductsSource =
                    sudLifePolicyManagementUseCase.invokePolicyProducts(data = requestData)
            }
            _policyProducts.addSource(policyProductsSource) {
                _policyProducts.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    //_progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        //Utilities.printData("PolicyProductsDashboard",it.data!!)
                        fragment.stopDashboardProductsShimmer()
                        if (it.data.policyProducts.isNotEmpty()) {
                            val list = it.data.policyProducts.toMutableList()
                            list.sortBy { it.productID }
                            fragment.policyProduct = list[0]
                            fragment.setUpSlidingPolicyBannerViewPager(list)
                            fragment.setUpBannerData(list)
                        } else {
                            fragment.hideBannerView()
                        }
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    //_progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        //toastMessage(it.errorMessage)
                    }
                    fragment.stopDashboardProductsShimmer()
                    fragment.hideBannerView()
                }
            }

        }

    fun callAddFeatureAccessLogApi(code: String, desc: String, service: String, url: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = AddFeatureAccessLog(
                Gson().toJson(
                    AddFeatureAccessLog.JSONDataRequest(
                        AddFeatureAccessLog.FeatureAccessLog(
                            partnerCode = Constants.PartnerCode,
                            personId = personId.toInt(),
                            code = code,
                            description = desc,
                            service = service,
                            url = url,
                            appversion = Utilities.getVersionName(context!!),
                            device = Build.BRAND + "~" + Build.MODEL,
                            devicetype = "Android",
                            platform = "App"
                        )
                    ), AddFeatureAccessLog.JSONDataRequest::class.java
                ), authToken
            )

            //_progressBar.value = Event("")
            _addFeatureAccessLog.removeSource(addFeatureAccessLogSource)
            withContext(Dispatchers.IO) {
                addFeatureAccessLogSource = homeManagementUseCase.invokeAddFeatureAccessLog(
                    isForceRefresh = true,
                    data = requestData
                )
            }
            _addFeatureAccessLog.addSource(addFeatureAccessLogSource) {
                _addFeatureAccessLog.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    //_progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        if (!Utilities.isNullOrEmptyOrZero(it.data.featureAccessLogID)) {
                            //toastMessage("Count Saved")
                        }
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    //_progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        //toastMessage(it.errorMessage)
                    }
                }
            }

        }

    fun callGetSSOUrlApi(moduleCode: String) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = GetSSOUrlModel(
            Gson().toJson(
                GetSSOUrlModel.JSONDataRequest(
                    accountID = accountID.toInt(),
                    personID = personId.toInt(),
                    moduleCode = moduleCode,
                    integrationCode = "AMAHA"
                ), GetSSOUrlModel.JSONDataRequest::class.java
            ), authToken
        )

        _progressBar.value = Event("")
        _getSSOUrl.removeSource(getSSOUrlSource)
        withContext(Dispatchers.IO) {
            getSSOUrlSource =
                homeManagementUseCase.invokeGetSSOUrl(isForceRefresh = true, data = requestData)
        }
        _getSSOUrl.addSource(getSSOUrlSource) {
            _getSSOUrl.value = it

            if (it.status == Resource.Status.SUCCESS) {
                //_progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    /*                    if ( !Utilities.isNullOrEmptyOrZero(it.data!!.featureAccessLogID) ) {
                                            toastMessage("Count Saved")
                                        }*/
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }

    }

    fun saveParameter(
        activity: BmiVitalsActivity,
        height: String,
        weight: String,
        clientID: String,
        processDataAPIUrl: String,
        processDataAPIToken: String,
        socketUrl: String,
        socketToken: String
    ) = viewModelScope.launch(Dispatchers.Main) {

        val bmi = String.format(
            "%.1f",
            CalculateParameters.roundOffPrecision(
                CalculateParameters.getBMI(
                    height.toDouble(),
                    weight.toDouble()
                ), 1
            )
        )
        val recordList = arrayListOf<SaveParameterModel.Record>()

        recordList.add(
            SaveParameterModel.Record(
                personId,
                DateHelper.currentUTCDatetimeInMillisecAsString,
                personId,
                DateHelper.currentUTCDatetimeInMillisecAsString,
                "HEIGHT",
                personId,
                "BMI",
                DateHelper.currentDateAsStringddMMMyyyy,
                "cm",
                height,
                ""
            )
        )

        recordList.add(
            SaveParameterModel.Record(
                personId,
                DateHelper.currentUTCDatetimeInMillisecAsString,
                personId,
                DateHelper.currentUTCDatetimeInMillisecAsString,
                "WEIGHT",
                personId,
                "BMI",
                DateHelper.currentDateAsStringddMMMyyyy,
                "kg",
                weight,
                ""
            )
        )

        recordList.add(
            SaveParameterModel.Record(
                personId,
                DateHelper.currentUTCDatetimeInMillisecAsString,
                personId,
                DateHelper.currentUTCDatetimeInMillisecAsString,
                "BMI",
                personId,
                "BMI",
                DateHelper.currentDateAsStringddMMMyyyy,
                "cm",
                bmi,
                ""
            )
        )

        val requestData = SaveParameterModel(
            Gson().toJson(
                SaveParameterModel.JSONDataRequest(recordList),
                SaveParameterModel.JSONDataRequest::class.java
            ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _progressBar.value = Event("Saving Parameter values...")
        _saveParam.removeSource(saveParamUserSource)
        withContext(Dispatchers.IO) {
            saveParamUserSource = homeManagementUseCase.invokeSaveParameter(data = requestData)
        }
        _saveParam.addSource(saveParamUserSource) {
            _saveParam.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                //Utilities.toastMessageShort(context,"Vitals Updated")
                activity.openAnotherActivity(destination = NavigationConstants.WELLFIE_SCREEN) {
                    putString(Constants.HEIGHT, height)
                    putString(Constants.WEIGHT, weight)
                    putString(Constants.CLIENT_ID, clientID)
                    putString(Constants.PROCESS_DATA_API_URL, processDataAPIUrl)
                    putString(Constants.PROCESS_DATA_API_TOKEN, processDataAPIToken)
                    putString(Constants.SOCKET_URL, socketUrl)
                    putString(Constants.SOCKET_TOKEN, socketToken)
                }
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }

    }

    fun callWellfieSaveVitalsApi(
        wellfieParameters: List<WellfieSaveVitalsModel.WellfieParameter>,
        height: String,
        weight: String
    ) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = WellfieSaveVitalsModel(
            Gson().toJson(
                WellfieSaveVitalsModel.JSONDataRequest(
                    mode = "",
                    personID = personId.toInt(),
                    recordDate = DateHelper.currentDateAsStringyyyyMMdd + "T" + DateHelper.currentTimeAs_hh_mm_ss,
                    height = height,
                    weight = weight,
                    gender = "Male",
                    wellfieParameters = wellfieParameters
                ), WellfieSaveVitalsModel.JSONDataRequest::class.java
            ), authToken
        )

        Utilities.printData("Request", requestData, false)

        _progressBar.value = Event("")
        _wellfieSaveVitals.removeSource(wellfieSaveVitalsSource)
        withContext(Dispatchers.IO) {
            wellfieSaveVitalsSource = homeManagementUseCase.invokeWellfieSaveVitals(
                isForceRefresh = true,
                data = requestData
            )
        }
        _wellfieSaveVitals.addSource(wellfieSaveVitalsSource) {
            _wellfieSaveVitals.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    if (it.data.isSave!!.equals(Constants.TRUE, ignoreCase = true)) {
                        WellfieSingleton.getInstance()!!.logCleverTapScanVitalsInfo2(context!!,wellfieParameters.toMutableList(),DateHelper.currentDateAsStringyyyyMMdd)
                    }
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }

    }

    fun callWellfieGetVitalsApi(fragment: HomeScreenFragment) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = WellfieGetVitalsModel(
            Gson().toJson(
                WellfieGetVitalsModel.JSONDataRequest(
                    mode = "",
                    personID = personId.toInt(),
                ), WellfieGetVitalsModel.JSONDataRequest::class.java
            ), authToken
        )

        //_progressBar.value = Event("")
        _wellfieGetVitals.removeSource(wellfieGetVitalsSource)
        withContext(Dispatchers.IO) {
            wellfieGetVitalsSource = homeManagementUseCase.invokeWellfieGetVitals(
                isForceRefresh = true,
                data = requestData
            )
        }
        _wellfieGetVitals.addSource(wellfieGetVitalsSource) {
            _wellfieGetVitals.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    fragment.loadWellfieData(it.data!!.result!!.report)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    //toastMessage(it.errorMessage)
                }
            }
        }

    }

    fun callWellfieGetSSOUrlApi() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = WellfieGetSSOUrlModel(
            Gson().toJson(
                WellfieGetSSOUrlModel.JSONDataRequest(
                    accountID = accountID.toInt(),
                    personID = personId.toInt()
                ), WellfieGetSSOUrlModel.JSONDataRequest::class.java
            ), authToken
        )

        _progressBar.value = Event("")
        _wellfieGetSSOUrl.removeSource(wellfieGetSSOUrlSource)
        withContext(Dispatchers.IO) {
            wellfieGetSSOUrlSource = homeManagementUseCase.invokeWellfieGetSSOUrl(
                isForceRefresh = true,
                data = requestData
            )
        }
        _wellfieGetSSOUrl.addSource(wellfieGetSSOUrlSource) {
            _wellfieGetSSOUrl.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    /*                    if ( !Utilities.isNullOrEmptyOrZero(it.data!!.featureAccessLogID) ) {
                                            toastMessage("Count Saved")
                                        }*/
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }

    }

    fun fetchStepsGoal() = viewModelScope.launch(Dispatchers.Main) {
        try {
            val requestData = GetStepsGoalModel(
                Gson().toJson(
                    GetStepsGoalModel.JSONDataRequest(
                        personID = personId
                    ), GetStepsGoalModel.JSONDataRequest::class.java
                ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            _getStepsGoal.removeSource(getStepsGoalSource)
            withContext(Dispatchers.IO) {
                getStepsGoalSource = homeManagementUseCase.invokeFetchStepsGoal(requestData)
            }
            _getStepsGoal.addSource(getStepsGoalSource) {
                _getStepsGoal.value = it
                if (it.status == Resource.Status.SUCCESS) {
                }

                if (it.status == Resource.Status.ERROR) {
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                        //logoutUser()
                    } else {
                        //toastMessage(it.errorMessage)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun callGetDailyWaterIntakeApi(date: String) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = GetDailyWaterIntakeModel(
            Gson().toJson(
                GetDailyWaterIntakeModel.JSONDataRequest(
                    GetDailyWaterIntakeModel.Request(
                        personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                            .toInt(),
                        recordDate = date
                    )
                ),
                GetDailyWaterIntakeModel.JSONDataRequest::class.java
            ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        //_progressBar.value = Event("")
        _getDailyWaterIntake.removeSource(getDailyWaterIntakeSource)
        withContext(Dispatchers.IO) {
            getDailyWaterIntakeSource = homeManagementUseCase.invokeGetDailyWaterIntake(
                isForceRefresh = true,
                data = requestData
            )
        }
        _getDailyWaterIntake.addSource(getDailyWaterIntakeSource) {
            _getDailyWaterIntake.value = it

            if (it.status == Resource.Status.SUCCESS) {
                if (it.data != null) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    //toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callAktivoCreateUserApi(deviceToken: String) = viewModelScope.launch(Dispatchers.Main) {

        val objMember = JsonObject()
        objMember.addProperty(
            "externalId",
            preferenceUtils.getPreference(PreferenceConstants.PERSONID, "")
        )
        objMember.addProperty(
            "nickname",
            preferenceUtils.getPreference(PreferenceConstants.FIRSTNAME, "")
        )
        objMember.addProperty(
            "date_of_birth",
            preferenceUtils.getPreference(PreferenceConstants.DOB, "")
        )
        objMember.addProperty(
            "sex",
            Utilities.getDisplayGender(
                preferenceUtils.getPreference(
                    PreferenceConstants.GENDER,
                    ""
                )
            )
        )
        if (!Utilities.isNullOrEmpty(deviceToken)) {
            objMember.addProperty("deviceToken", deviceToken)
        }

        for (i in wellfieSingleton.getBmiVitalsList()) {
            when (i.parameterCode) {
                "HEIGHT" -> {
                    objMember.addProperty("height_cm", i.value!!.toInt().toString())
                }

                "WEIGHT" -> {
                    objMember.addProperty("weight_kg", i.value!!.toInt().toString())
                }
            }
        }

        val obj = JsonObject()
        obj.add("member", objMember)
        val requestData =
            AktivoCreateUserModel(obj, preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

        //_progressBar.value = Event("")
        _aktivoCreateUser.removeSource(aktivoCreateUserSource)
        withContext(Dispatchers.IO) {
            aktivoCreateUserSource =
                aktivoManagementUseCase.invokeAktivoCreateUser(data = requestData)
        }
        _aktivoCreateUser.addSource(aktivoCreateUserSource) {
            _aktivoCreateUser.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    Utilities.printLog("Response--->" + it.data)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                //toastMessage(it.errorMessage)
            }
        }

    }

    fun callAktivoGetUserTokenApi(aktivoMemberId: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val obj = JsonObject()
            obj.addProperty("member_id", aktivoMemberId)
            val requestData = AktivoGetUserTokenModel(
                obj,
                preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            //_progressBar.value = Event("")
            _aktivoGetUserToken.removeSource(aktivoGetUserTokenSource)
            withContext(Dispatchers.IO) {
                aktivoGetUserTokenSource =
                    aktivoManagementUseCase.invokeAktivoGetUserToken(data = requestData)
            }
            _aktivoGetUserToken.addSource(aktivoGetUserTokenSource) {
                _aktivoGetUserToken.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        Utilities.printLog("Response--->" + it.data)
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    //toastMessage(it.errorMessage)
                }
            }

        }

    fun callGetBlogsFromServerApi(
        VisibleThreshold: Int,
        CurrentPage: Int,
        fragment: HomeScreenFragment
    ) = viewModelScope.launch(Dispatchers.Main) {

        val obj = JsonObject()
        obj.addProperty("per_page", VisibleThreshold)
        obj.addProperty("offset", CurrentPage)
        val requestData = BlogsListAllModel(obj, authToken)

        //_progressBar.value = Event("Loading Health Blog...")
        _blogList.removeSource(blogListSource)
        withContext(Dispatchers.IO) {
            blogListSource =
                homeManagementUseCase.invokeDownloadBlog(isForceRefresh = true, data = requestData)
        }
        _blogList.addSource(blogListSource) {
            _blogList.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                fragment.stopBlogsShimmer()
                if (it.data != null) {
                    Utilities.printLog("Blog_Response--->" + it.data)
                    extractBlogContent(it.data.listAll, "ALL", "")
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                fragment.stopBlogsShimmer()
                //toastMessage(it.errorMessage)
            }
        }
    }

    private fun extractBlogContent(list: List<BlogModel.Blog>, type: String, blogId: String) {
        val blogList: ArrayList<BlogItem> = ArrayList()
        for (blog in list) {
            val link = blog.link
            val id = blog.id.toString()
            val date = DateHelper.getDateTimeAs_ddMMMyyyyNew(blog.date)
            val renderedTitle = blog.title.rendered
            val renderedContent = blog.content.rendered
            val renderedExcerpt = blog.excerpt.rendered
            Utilities.printLog("Title--->$renderedTitle")
            Utilities.printLog("BlogLink--->$link")
            val documentExcerpt: Document = Jsoup.parse(renderedExcerpt)
            val pExcerpt: Elements = documentExcerpt.getElementsByTag("p")
            var strDescription = ""
            for (x: Element in pExcerpt) {
                strDescription += x.text()
            }
            Utilities.printLog("Description--->$strDescription")
            val documentContent: Document = Jsoup.parse(renderedContent)
            val pContent: Elements = documentContent.getElementsByTag("p")
            var strBody = ""
            for (x in pContent) {
                strBody += x.text()
            }
            val elementsByClassImage: Elements = documentContent.getElementsByTag("img")
            val strImgSrc = elementsByClassImage.attr("src")
            Utilities.printLog("ImgUrl--->$strImgSrc")
            var categoryId = 0
            if (!blog.categories.isNullOrEmpty()) {
                categoryId = blog.categories.get(0)
            }
            val blogItem = BlogItem(
                id = id,
                title = renderedTitle,
                description = strDescription,
                date = date,
                image = strImgSrc,
                link = link,
                body = renderedContent,
                categoryId = categoryId
            )
            if (!blogList.contains(blogItem)) {
                if (blogId.isNullOrEmpty()) {
                    blogList.add(blogItem)
                } else {
                    if (!blogId.equals(id, true))
                        blogList.add(blogItem)
                }
            }
        }
        if (type.equals("ALL", true)) {
            healthBlogList.postValue(blogList)
        } else {
            healthBlogSuggestionList.postValue(blogList)
        }
    }

    fun viewBlog(view: View, blog: BlogItem) {
        val intent = Intent()
        intent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.BLOG_DETAIL)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(Constants.TITLE, blog.title)
        intent.putExtra(Constants.DESCRIPTION, blog.description)
        intent.putExtra(Constants.BODY, blog.body)
        intent.putExtra(Constants.BLOG_ID, blog.id)
        intent.putExtra(Constants.CATEGORY_ID, blog.categoryId)
        //intent.putExtra("LINK", blog.link)
        intent.putExtra(Constants.LINK, blog.link)
        context!!.startActivity(intent)
    }

    fun getRelatedBlogApi(
        page: Int, blogID: String, categroryId: Int
    ) = viewModelScope.launch(Dispatchers.Main) {

        val obj = JsonObject()
        obj.addProperty("categories", categroryId)
        obj.addProperty("page", page)
        obj.addProperty("per_page", 5)
        obj.addProperty("sorting", "JUMBLED")
        val requestData = BlogRecommendationListModel(obj, authToken)

        //_progressBar.value = Event("")
        _blogsRelated.removeSource(blogsRelatedSource)
        withContext(Dispatchers.IO) {
            blogsRelatedSource = homeManagementUseCase.invokeBlogsListRelatedTo(true, requestData)
        }
        _blogsRelated.addSource(blogsRelatedSource) {
            _blogsRelated.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    //Utilities.printData("BlogsByCategoryResp",it.data!!,true)
                    extractBlogContent(it.data.blogs, "SUG", blogID)
                    //extractBlogContent(it.data!!.result.blogs,fragment,Constants.CATEGORY)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callSmitFitEventsApi() {

        //_progressBar.value = Event("")
        val smitFitEventsService =
            provideRetrofit("https://apiv2.smit.fit/").create(SmitFitEventsService::class.java)
        val call = smitFitEventsService.getSmitFitEventsApi()

        call.enqueue(object : Callback<List<LiveSessionModel>> {
            override fun onResponse(
                call: Call<List<LiveSessionModel>>,
                response: Response<List<LiveSessionModel>>
            ) {
                if (response.body() != null) {
                    val result = response.body()!!
                    if (response.isSuccessful) {
                        Utilities.printData("SmitFitEvents", result, true)
                        CoroutineScope(Dispatchers.Main).launch {
                            withContext(Dispatchers.IO) {
                                liveSessions.postValue(result)
                            }
                        }
                    } else {
                        Utilities.printLogError("Server Contact failed")
                    }
                } else {
                    Utilities.printLogError("response.body is null")
                }
                _progressBar.value = Event(Event.HIDE_PROGRESS)
            }

            override fun onFailure(call: Call<List<LiveSessionModel>>, t: Throwable) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                Utilities.printLog("Api failed" + t.printStackTrace())
            }
        })
    }

    private fun provideRetrofit(baseUrl: String): Retrofit {
        val logging = HttpLoggingInterceptor { message ->
            Utilities.printLog("HttpLogging--> $message")
        }
        logging.level = HttpLoggingInterceptor.Level.BODY
        return Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .protocols(listOf(Protocol.HTTP_1_1))
                    .addInterceptor(logging)
                    .connectTimeout(3, TimeUnit.MINUTES)
                    .writeTimeout(3, TimeUnit.MINUTES)
                    .readTimeout(3, TimeUnit.MINUTES)
                    .build()
            )
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun callWellfiePostProcessorApi(url: String, auth: String, jsonObject: JSONObject): String {
        var strResponse = ""
        try {
            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)
            val authorization = auth.replace("Bearer ", "")
            val request: Request = Request.Builder()
                .addHeader("Authorization", "Bearer $authorization")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .url(url)
                .build()
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    strResponse = requestCallToServer(request)
                    //Utilities.printLog("strResponse--> $strResponse")
                    postProcessResponse.postValue(strResponse)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strResponse
    }

    private fun requestCallToServer(request: Request): String {
        var strResponse = ""
        try {
            val logging = HttpLoggingInterceptor { message: String? ->
                Utilities.printLog("HttpLogging--> $message")
            }
            logging.level = HttpLoggingInterceptor.Level.BODY
            val okHttpClient = OkHttpClient.Builder()
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .addInterceptor(logging)
                .connectTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .build()
            val response = okHttpClient.newCall(request).execute()
            strResponse = response.body!!.string()
                .replace("\\r\\n", "")
                .replace("\\\"", "\"")
                .replace("\"{", "{")
                .replace("}\"", "}")
            Utilities.printLogError("OkHttp strResponse-----> $strResponse")
            return strResponse
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strResponse
    }

    fun shareAppInvite() {
        val linkGenerator = ShareInviteHelper.generateInviteUrl(context)
        linkGenerator.addParameter(Constants.DEEP_LINK_VALUE, Constants.DEEP_LINK_REFERRAL)
        linkGenerator.addParameter(Constants.DEEP_LINK_SUB1, firstName)
        linkGenerator.addParameter(Constants.DEEP_LINK_SUB2, personId)
        linkGenerator.addParameter(Constants.AF_DP,Constants.AF_DP_VALUE)
        linkGenerator.campaign = "user_invite"
        linkGenerator.channel = "mobile_share"

        val listener: LinkGenerator.ResponseListener = object : LinkGenerator.ResponseListener {
            override fun onResponse(urlToShare: String) {
                shareAppReferralMessage(context!!, urlToShare)
            }

            override fun onResponseError(s: String) {
                Utilities.printLogError("onResponseError--->$s")
            }
        }
        linkGenerator.generateLink(context, listener)

        val logInviteMap = HashMap<String, String>()
        logInviteMap["referrerId"] = personId
        logInviteMap["campaign"] = "user_invite"

        ShareInviteHelper.logInvite(context, "mobile_share", logInviteMap)
    }

    fun shareAppInviteMpt(cohortTitle: String) {
        val linkGenerator = ShareInviteHelper.generateInviteUrl(context)
        linkGenerator.addParameter(Constants.DEEP_LINK_VALUE, Constants.DEEP_LINK_REFERRAL)
        linkGenerator.addParameter(Constants.DEEP_LINK_SUB1, firstName)
        linkGenerator.addParameter(Constants.DEEP_LINK_SUB2, personId)
        linkGenerator.addParameter(Constants.AF_DP,Constants.AF_DP_VALUE)
        linkGenerator.campaign = "user_invite"
        linkGenerator.channel = "mobile_share"

        val listener: LinkGenerator.ResponseListener = object : LinkGenerator.ResponseListener {
            override fun onResponse(urlToShare: String) {
                shareMptResultInvite(context!!, urlToShare, cohortTitle)
            }

            override fun onResponseError(s: String) {
                Utilities.printLogError("onResponseError--->$s")
            }
        }
        linkGenerator.generateLink(context, listener)

        val logInviteMap = HashMap<String, String>()
        logInviteMap["referrerId"] = personId
        logInviteMap["campaign"] = "user_invite"

        ShareInviteHelper.logInvite(context, "mobile_share", logInviteMap)
    }

    fun shareAppReferralMessage(context: Context, url: String) {
        try {
            val localResource = LocaleHelper.getLocalizedResources(
                context,
                Locale(LocaleHelper.getLanguage(context))
            )!!
            localResource.getString(R.string.REFER_MSG1)

            val userName = preferenceUtils.getPreference(PreferenceConstants.FIRSTNAME, "")
            val playStoreLink = "$url?af_force_deeplink=true"
            Utilities.printLogError("PlayStoreLink--->$playStoreLink")

            val title = "${localResource.getString(R.string.REFER_MSG1)}! $userName ${
                localResource.getString(R.string.REFER_MSG2)
            }"
            val text = title + "\n\n" + "${localResource.getString(R.string.REFER_MSG3)}\n\n" +
                    Html.fromHtml(
                        "${localResource.getString(R.string.REFER_MSG10)} " +
                                "<a href=\"$playStoreLink\">$playStoreLink</a> " + "</br></br>"
                    ) + " ${localResource.getString(R.string.REFER_MSG11)}"

            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                localResource.getString(R.string.REFER_SUBJECT)
            )
            sendIntent.putExtra(Intent.EXTRA_TEXT, text)
            sendIntent.type = "text/plain"
            sendIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.startActivity(sendIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun shareBannerWithFriends(
        context: Context,
        policyProduct: PolicyProductsModel.PolicyProducts
    ) {
        try {
            //val shareLink=Utilities.getCenturionShareUrl(Utilities.getUserPhoneNumber())
            var shareLink = policyProduct.productShareURL
            if (policyProduct.productCode == Constants.CENTURION) {
                shareLink = Utilities.getCenturionShareUrl(
                    policyProduct.productShareURL,
                    Utilities.getUserPhoneNumber()
                )
            }
            var shareText = localResource.getString(R.string.SHARE_MESSAGE_FOR_BANNER_GENERAL)
            when (policyProduct.productCode) {
                Constants.CENTURION -> {
                    shareText = localResource.getString(R.string.SHARE_MESSAGE_FOR_CENTURION_BANNER)
                }

                Constants.SMART_HEALTH_PRODUCT -> {
                    shareText =
                        localResource.getString(R.string.SHARE_MESSAGE_FOR_SMART_HEALTH_PRODUCT_BANNER)
                }
            }
            val tinyURLAPI = "https://tinyurl.com/api-create.php?url="
            val encodedURL = Uri.encode(shareLink)
            val urlString = tinyURLAPI + encodedURL
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputReader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = inputReader.readLine()
                inputReader.close()
                val sharingMessage = shareText + response

                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    localResource.getString(R.string.REFER_SUBJECT)
                )
                sendIntent.putExtra(Intent.EXTRA_TEXT, sharingMessage)
                sendIntent.type = "text/plain"
                sendIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.startActivity(sendIntent)
            } else {
                Utilities.toastMessageShort(context, "Error fetching shortened URL")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*    fun shareAppReferralMessage(context: Context,url: String) {
            try {
                val localResource = LocaleHelper.getLocalizedResources(context,Locale(LocaleHelper.getLanguage(context)))!!
                localResource.getString(R.string.REFER_MSG1)

                val userName = preferenceUtils.getPreference(PreferenceConstants.FIRSTNAME, "")
                val playStoreLink = "$url?af_force_deeplink=true"
                Utilities.printLogError("PlayStoreLink--->$playStoreLink")
                val appName = context.resources.getString(R.string.app_name)

                val title = "${localResource.getString(R.string.REFER_MSG1)}! $userName ${localResource.getString(R.string.REFER_MSG2)}"
                val text = title + "\n\n" + "${localResource.getString(R.string.REFER_MSG3)}\n\n" +
                        "- ${localResource.getString(R.string.REFER_MSG4)} :\n\n" +
                        "- ${localResource.getString(R.string.REFER_MSG5)}\n" +
                        "- ${localResource.getString(R.string.REFER_MSG6)}\n" +
                        "- ${localResource.getString(R.string.REFER_MSG7)}\n" +
                        "- ${localResource.getString(R.string.REFER_MSG8)}\n\n" +
                        "*${localResource.getString(R.string.REFER_MSG9)}*\n\n" +
                        Html.fromHtml("${localResource.getString(R.string.REFER_MSG10)} " +
                                "<a href=\"$playStoreLink\">$playStoreLink</a> " + "</br></br>"
                        ) + " ${localResource.getString(R.string.REFER_MSG11)}"

                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_SUBJECT,localResource.getString(R.string.REFER_SUBJECT))
                sendIntent.putExtra(Intent.EXTRA_TEXT, text)
                sendIntent.type = "text/plain"
                sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.startActivity(sendIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/

    /*    fun spreadTheWordNew(context: Context,url: String?) {
            try {
                val userName = preferenceUtils.getPreference(PreferenceConstants.FIRSTNAME, "")
                val playStoreLink = "$url?af_force_deeplink=true"
                Utilities.printLogError("PlayStoreLink--->$playStoreLink")
                val appName = context.resources.getString(R.string.app_name)

                val title = "Hey there! $userName wants you to join $appName ! \uD83D\uDE4B\u200D♀️"
                val text = title + "\n\n" + "$appName is a comprehensive health & wellness app that helps you take care of your body and mind \uD83D\uDCAA\uD83E\uDDE0.\n\n" +
                        Html.fromHtml("Download the app ⬇️ " +
                                "<a href=\"$playStoreLink\">$playStoreLink</a> " + "</br></br>"
                        ) + " and start taking care of yourself today! \uD83D\uDE0A"

                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_SUBJECT,localResource.getString(R.string.REFER_SUBJECT))
                sendIntent.putExtra(Intent.EXTRA_TEXT, text)
                sendIntent.type = "text/plain"
                sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.startActivity(sendIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/

    /*    fun spreadTheWordNew(context: Context,url: String?) {
            try {
                val userName = preferenceUtils.getPreference(PreferenceConstants.FIRSTNAME, "")
                val playStoreLink = "$url?af_force_deeplink=true"
                Utilities.printLogError("PlayStoreLink--->$playStoreLink")
                val appName = context.resources.getString(R.string.app_name)

                val title = "Hey there! $userName wants you to join $appName ! \uD83D\uDE4B\u200D♀️"
                val text = title + "\n\n" + "$appName is a comprehensive health & wellness app that helps you take care of your body and mind \uD83D\uDCAA\uD83E\uDDE0.\n\n" +
                        "With $appName, you can:\n\n" +
                        "- Track your wellness score \uD83E\uDD29\n" +
                        "- Get personalized recommendations and health goals \uD83C\uDFAF\n" +
                        "- Compete with your friends and participate in various fitness challenges \uD83C\uDFC3\uD83C\uDFFB\uD83C\uDFC3\uD83C\uDFFB\u200D♀\n" +
                        "- And do a 1-minute face scan \uD83D\uDD75️\u200D♀️ to get your vitals immediately \uD83D\uDE2E\n\n" +
                        "*It's like having your own personal health coach in your pocket!*\uD83D\uDCF1\n\n" +
                        Html.fromHtml("Download the app ⬇️ " +
                            "<a href=\"$playStoreLink\">$playStoreLink</a> " + "</br></br>"
                ) + " and start taking care of yourself today! \uD83D\uDE0A"

                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_SUBJECT,localResource.getString(R.string.REFER_SUBJECT))
                sendIntent.putExtra(Intent.EXTRA_TEXT, text)
                sendIntent.type = "text/plain"
                sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.startActivity(sendIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/

    fun shareMptResultInvite(context: Context, url: String, cohortTitle: String) {
        try {
            val localResource = LocaleHelper.getLocalizedResources(
                context,
                Locale(LocaleHelper.getLanguage(context))
            )!!

            val userName = preferenceUtils.getPreference(PreferenceConstants.FIRSTNAME, "")
            val playStoreLink = "$url?af_force_deeplink=true"
            Utilities.printLogError("PlayStoreLink--->$playStoreLink")

            val title = "${localResource.getString(R.string.REFER_MSG1)}! $userName ${
                localResource.getString(R.string.REFER_MSG2)
            }"
            val text = title + "\n\n" +
                    "${localResource.getString(R.string.REFER_MPT_MSG2)}\n\n" +
                    "${localResource.getString(R.string.REFER_MPT_MSG1)} $cohortTitle\n\n" +
                    Html.fromHtml(
                        "${localResource.getString(R.string.REFER_MSG12)} " +
                                "<a href=\"$playStoreLink\">$playStoreLink</a> " + "</br></br>"
                    )

            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                localResource.getString(R.string.REFER_SUBJECT)
            )
            sendIntent.putExtra(Intent.EXTRA_TEXT, text)
            sendIntent.type = "text/plain"
            sendIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.startActivity(sendIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareBlog(blog: BlogItem) {
        val title = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(blog.title, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            Html.fromHtml(blog.title).toString()
        }
        val desc = blog.description!!.substring(0, 70) + ".....Continue reading at,"
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        sendIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + desc + "\n" + blog.link)
        /*        sendIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + desc + "\n"
                        + context.resources.getString(R.string.blog_link))*/
        sendIntent.type = "text/plain"
        sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context!!.startActivity(sendIntent)
    }

    fun callSaveFeedbackApi(context: Context, feedback: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val deviceName = Build.BRAND + "~" + Build.MODEL
            val requestData = SaveFeedbackModel(
                Gson().toJson(
                    SaveFeedbackModel.JSONDataRequest(
                        SaveFeedbackModel.AppFeedback(
                            app = Configuration.strAppIdentifier,
                            appVersion = Utilities.getAppVersion(context).toString(),
                            deviceType = "ANDROID",
                            deviceName = deviceName,
                            personID = personId.toInt(),
                            accountID = accountID.toInt(),
                            emailID = email,
                            phoneNumber = phone,
                            type = "Feedback",
                            feedback = feedback
                        )
                    ), SaveFeedbackModel.JSONDataRequest::class.java
                ), authToken
            )

            _progressBar.value = Event("Saving Feedback...")
            _saveFeedback.removeSource(saveFeedbackSource)
            withContext(Dispatchers.IO) {
                saveFeedbackSource = homeManagementUseCase.invokeSaveFeedback(
                    isForceRefresh = true,
                    data = requestData
                )
            }
            _saveFeedback.addSource(saveFeedbackSource) {
                _saveFeedback.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }
            }

        }

    fun callContactUsApi(context: Context, fromEmail: String, fromMobile: String, message: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = ContactUsModel(
                Gson().toJson(
                    ContactUsModel.JSONDataRequest(
                        emailAddress = email,
                        fromEmail = fromEmail,
                        fromMobile = fromMobile,
                        message = message
                    ), ContactUsModel.JSONDataRequest::class.java
                ), authToken
            )

            _progressBar.value = Event("Posting Your Message...")
            _contactUs.removeSource(contactUsSource)
            withContext(Dispatchers.IO) {
                contactUsSource =
                    homeManagementUseCase.invokeContactUs(isForceRefresh = true, data = requestData)
            }
            _contactUs.addSource(contactUsSource) {
                _contactUs.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        if (it.errorNumber.equals("0", true)) {
                            val intentToPass = Intent()
                            intentToPass.component =
                                ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
                            intentToPass.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intentToPass)
                        }
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }
            }

        }

    fun callWellfieListVitalsApi() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = WellfieListVitalsModel(
            Gson().toJson(
                WellfieListVitalsModel.JSONDataRequest(
                    criteria = WellfieListVitalsModel.Criteria(
                        personID = personId.toInt(),
                        mode = "",
                        fromDate = "",
                        toDate = ""
                    )
                ), WellfieListVitalsModel.JSONDataRequest::class.java
            ), authToken
        )

        _progressBar.value = Event("")
        _wellfieListVitals.removeSource(wellfieListVitalsSource)
        withContext(Dispatchers.IO) {
            wellfieListVitalsSource = homeManagementUseCase.invokeWellfieListVitals(
                isForceRefresh = true,
                data = requestData
            )
        }
        _wellfieListVitals.addSource(wellfieListVitalsSource) {
            _wellfieListVitals.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    /*                    if ( !Utilities.isNullOrEmptyOrZero(it.data!!.featureAccessLogID) ) {
                                            toastMessage("Count Saved")
                                        }*/
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }

    }

    fun callDownloadReport() {

        _progressBar.value = Event("Downloading HRA Report.....")
        val baseURL = Constants.strAPIUrl
        val proxyURL = Constants.strProxyUrl
        val hraUrl = Constants.HRAurl
        val partnerCode = Constants.PartnerCode
        val personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
        val accountId = preferenceUtils.getPreference(PreferenceConstants.ACCOUNTID, "0")
        val hash = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        // Hash will be AUTH_TICKET we are getting in Login response.

        val finalHRADownloadURL =
            baseURL + proxyURL + hraUrl + "&P=" + personId + "&PCD=" + partnerCode + "&UID=" + accountId + "&E=" + "&Skey=" + hash
        Utilities.printLog("FinalHRADownloadURL--> $finalHRADownloadURL")

        val hraDownloadURL =
            proxyURL + hraUrl + "&P=" + personId + "&PCD=" + partnerCode + "&UID=" + accountId + "&E=" + "&Skey=" + hash
        val logging = HttpLoggingInterceptor { message ->
            Utilities.printLog("HttpLogging--> $message")
        }
        logging.level = HttpLoggingInterceptor.Level.BODY
        val retrofit = Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .protocols(Arrays.asList(Protocol.HTTP_1_1))
                    .addInterceptor(logging)
                    .connectTimeout(3, TimeUnit.MINUTES)
                    .writeTimeout(3, TimeUnit.MINUTES)
                    .readTimeout(3, TimeUnit.MINUTES)
                    .build()
            )
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val downloadService = retrofit.create(DownloadReportApiService::class.java)

        val call = downloadService.downloadHraReport(hraDownloadURL)
        call.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (response.body() != null) {
                    val result = response.body()!!
                    if (response.isSuccessful) {
                        Utilities.printLog("Server contacted and has file--> ")
                        val writtenToDisk = saveHRAReportAppDirectory(result, context!!)
                        Utilities.printLog("File download was---->$writtenToDisk")
                    } else {
                        Utilities.printLog("Server Contact failed")
                    }
                } else {
                    Utilities.toastMessageShort(
                        context,
                        localResource.getString(R.string.ERROR_UNABLE_TO_DOWNLOAD)
                    )
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                Utilities.printLog("Download Report call failed" + t.printStackTrace())
            }
        })
    }

    fun saveHRAReportAppDirectory(body: ResponseBody, context: Context): Boolean {
        var save = false
        try {
            if (fileUtils.isExternalStorageAvailable && fileUtils.isExternalStorageWritable) {

                val folderName = Utilities.getAppFolderLocation(context)
                val fileName = fileUtils.generateUniqueFileName(
                    Configuration.strAppIdentifier + "_HRA",
                    ".pdf"
                )

                val myDirectory = File(folderName)
                if (!myDirectory.exists()) {
                    myDirectory.mkdirs()
                }

                val hraReportFile = File(folderName, fileName)
                Utilities.printLogError("downloadDocPath: ----->$hraReportFile")

                val inputStream: InputStream = body.byteStream()
                val fileReader = ByteArray(4096)

                context.contentResolver.openFileDescriptor(Uri.fromFile(hraReportFile), "w")
                    ?.use { parcelFileDescriptor ->
                        FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outStream ->
                            while (true) {
                                val read = inputStream.read(fileReader)
                                if (read == -1) {
                                    break
                                }
                                outStream.write(fileReader, 0, read)
                            }
                            save = true
                            openDownloadedDocumentFile(hraReportFile, context)
                        }
                    }
                inputStream.close()
            } else {
                save = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            save = false
        }
        return save
    }

    private fun openDownloadedDocumentFile(file: File, context: Context) {
        try {
            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            //val openIntent = Intent.createChooser(intent,"Open using")
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Utilities.toastMessageShort(
                context,
                context.resources.getString(R.string.ERROR_NO_APPLICATION_TO_VIEW_PDF)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Utilities.toastMessageShort(
                context,
                context.resources.getString(R.string.ERROR_UNABLE_TO_OPEN_FILE)
            )
        }
    }

    fun getUserPreference(key: String): String {
        val userPreference = preferenceUtils.getPreference(key)
        Utilities.printLogError("$key--->$userPreference")
        return userPreference
    }

    fun storeUserPreference(key: String, value: String) {
        Utilities.printLogError("Storing $key--->$value")
        preferenceUtils.storePreference(key, value)
    }

    fun showProgress() {
        _progressBar.value = Event("")
    }

    fun hideProgress() {
        _progressBar.value = Event(Event.HIDE_PROGRESS)
    }

    fun callGetProfileImageApiMain(documentID: String) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = ProfileImageModel(
            Gson().toJson(
                ProfileImageModel.JSONDataRequest(
                    documentID = documentID
                ),
                ProfileImageModel.JSONDataRequest::class.java
            ), authToken
        )

        _profileImage.removeSource(profileImageSource)
        withContext(Dispatchers.IO) {
            profileImageSource = homeManagementUseCase.invokeGetProfileImage(
                isForceRefresh = true,
                data = requestData
            )
        }
        _profileImage.addSource(profileImageSource) {
            _profileImage.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {

                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }


}
