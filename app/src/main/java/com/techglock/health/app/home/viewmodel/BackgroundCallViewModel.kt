package com.techglock.health.app.home.viewmodel

//import com.caressa.allizhealth.app.model.tempconst.Configuration.EntityID
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.ApiConstants
import com.techglock.health.app.common.constants.Configuration.EntityID
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.fitness.FitnessDataManager
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Event
import com.techglock.health.app.common.utils.PreferenceUtils
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.fitness_tracker.common.StepsDataSingleton
import com.techglock.health.app.fitness_tracker.util.StepCountHelper
import com.techglock.health.app.home.domain.BackgroundCallUseCase
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.model.fitness.StepsHistoryModel
import com.techglock.health.app.model.home.CheckAppUpdateModel
import com.techglock.health.app.model.home.SaveCloudMessagingIdModel
import com.techglock.health.app.model.hra.HraMedicalProfileSummaryModel
import com.techglock.health.app.model.parameter.LabRecordsListModel
import com.techglock.health.app.model.parameter.ParameterListModel
import com.techglock.health.app.model.parameter.VitalsHistoryModel
import com.techglock.health.app.model.shr.ListDocumentTypesModel
import com.techglock.health.app.repository.utils.Resource
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class BackgroundCallViewModel @Inject constructor(
    application: Application,
    private val useCase: BackgroundCallUseCase,
    private val preferenceUtils: PreferenceUtils,
    private val context: Context?
) : BaseViewModel(application) {

    var personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
    var email = preferenceUtils.getPreference(PreferenceConstants.EMAIL, "")
    var phone = preferenceUtils.getPreference(PreferenceConstants.PHONE, "")
    var firstName = preferenceUtils.getPreference(PreferenceConstants.FIRSTNAME, "")
    var gender = preferenceUtils.getPreference(PreferenceConstants.GENDER, "")
    private val fcmToken = preferenceUtils.getPreference(PreferenceConstants.FCM_REGISTRATION_ID, "")

    var isBackgroundApiCall = false
    var profileSwitched = false
    val labParameterList = MutableLiveData<List<TrackParameterMaster.History>>()

    var saveCloudMessagingIdSource: LiveData<Resource<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>> = MutableLiveData()
    val _saveCloudMessagingId =
        MediatorLiveData<Resource<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>>()
    val saveCloudMessagingId: LiveData<Resource<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>> get() = _saveCloudMessagingId

    var medicalProfileSummarySource: LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> = MutableLiveData()
    val _medicalProfileSummary = MediatorLiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>>()
    val medicalProfileSummary: LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> get() = _medicalProfileSummary

    var paramUserSource: LiveData<Resource<ParameterListModel.Response>> = MutableLiveData()
    val _paramList = MediatorLiveData<Resource<ParameterListModel.Response>>()
    val paramList: LiveData<Resource<ParameterListModel.Response>> get() = _paramList

    var labRecordUserSource: LiveData<Resource<TrackParameterMaster.HistoryResponse>> = MutableLiveData()
    val _labRecordList = MediatorLiveData<Resource<TrackParameterMaster.HistoryResponse>>()
    val labRecordList: LiveData<Resource<TrackParameterMaster.HistoryResponse>> get() = _labRecordList

    var labRecordVitalsUserSource: LiveData<Resource<VitalsHistoryModel.Response>> = MutableLiveData()
    val _labRecordVitalsList = MediatorLiveData<Resource<VitalsHistoryModel.Response>>()
    val labRecordVitalsList: LiveData<Resource<VitalsHistoryModel.Response>> get() = _labRecordVitalsList

    var checkAppUpdateSource: LiveData<Resource<CheckAppUpdateModel.CheckAppUpdateResponse>> = MutableLiveData()
    val _checkAppUpdate = MediatorLiveData<Resource<CheckAppUpdateModel.CheckAppUpdateResponse>>()
    val checkAppUpdate: LiveData<Resource<CheckAppUpdateModel.CheckAppUpdateResponse>> get() = _checkAppUpdate

    private var stepsHistorySource: LiveData<Resource<StepsHistoryModel.Response>> = MutableLiveData()
    private val _stepsHistoryList = MediatorLiveData<Resource<StepsHistoryModel.Response>>()
    val stepsHistoryList: LiveData<Resource<StepsHistoryModel.Response>> get() = _stepsHistoryList

    var listDocumentTypesSource: LiveData<Resource<ListDocumentTypesModel.ListDocumentTypesResponse>> = MutableLiveData()
    val _listDocumentTypes =
        MediatorLiveData<Resource<ListDocumentTypesModel.ListDocumentTypesResponse>>()
    val listDocumentTypes: LiveData<Resource<ListDocumentTypesModel.ListDocumentTypesResponse>> get() = _listDocumentTypes

    /*    var listRelativesSource: LiveData<Resource<ListRelativesModel.ListRelativesResponse>> = MutableLiveData()
        val _listRelatives = MediatorLiveData<Resource<ListRelativesModel.ListRelativesResponse>>()
        val listRelatives: LiveData<Resource<ListRelativesModel.ListRelativesResponse>> get() = _listRelatives*/

    /*    var bmiUserSource: LiveData<Resource<BMIHistoryModel.Response>> = MutableLiveData()
        private val _bmiHistoryList = MediatorLiveData<BMIHistoryModel.Response>()
        val bmiHistoryList: LiveData<BMIHistoryModel.Response> get() = _bmiHistoryList

        var whrUserSource: LiveData<Resource<WHRHistoryModel.Response>> = MutableLiveData()
        private val _whrHistoryList = MediatorLiveData<WHRHistoryModel.Response>()
        val whrHistoryList: LiveData<WHRHistoryModel.Response> get() = _whrHistoryList

        var bloodPressureUserSource: LiveData<Resource<BloodPressureHistoryModel.Response>> = MutableLiveData()
        private val _bloodPressureHistoryList = MediatorLiveData<BloodPressureHistoryModel.Response>()
        val bloodPressureHistoryList: LiveData<BloodPressureHistoryModel.Response> get() = _bloodPressureHistoryList*/

    fun isPushNotificationEnabled(): Boolean {
        return preferenceUtils.getBooleanPreference(
            PreferenceConstants.ENABLE_PUSH_NOTIFICATION,
            true
        )
    }

    fun getUserJoiningDate(): String {
        return preferenceUtils.getPreference(PreferenceConstants.JOINING_DATE, "")
    }

    private fun getIsFitnessDataNotSync(): Boolean {
        val isFitnessDataNotSync =
            preferenceUtils.getBooleanPreference(PreferenceConstants.IS_FITNESS_DATA_NOT_SYNC, true)
        Utilities.printLogError("isFitnessDataNotSync--->$isFitnessDataNotSync")
        return isFitnessDataNotSync
    }

    fun callBackgroundApiCall(showProgress: Boolean) = viewModelScope.launch(Dispatchers.Main) {
        if (showProgress) {
            _progressBar.value = Event("Synchronising Profile...")
        }
        Utilities.printLog("Inside callBackgroundApiCall=> $isBackgroundApiCall")
        if (!isBackgroundApiCall) {
            withContext(Dispatchers.IO) {
                val dataSyncList = useCase.invokeGetSyncMasterData(preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"))
                //Weekly Sync Api Calls
                Utilities.printLogError("Data Sync--->$dataSyncList")
                Utilities.printLogError("PersonId--->${preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")}")
                if (dataSyncList.find { it.apiName == ApiConstants.TRACK_PARAM_LIST_MASTER } == null) {
                    Utilities.printLogError("Inside Fresh Login")
                    getParameterList()
                    callDocumentTypesApi(forceRefresh = false)
                    //getMedicalProfileSummary()
                    //callListRelativesApi()
                    /*getBMIHistory("")
                    getBloodPressureHistory("")
                    getWHRHistory("")*/
                    getLabRecordList("")
                    getLabRecordVitalsList("")

                    //getStepsHistory()
                } else if (profileSwitched) {
                    Utilities.printLogError("Inside Switch Profile")
                    useCase.invokeDeleteHistoryWithOtherPersonId(preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"))
                    //getMedicalProfileSummary()
                    /*getBMIHistory("")
                    getBloodPressureHistory("")
                    getWHRHistory("")*/
                    getLabRecordList("")
                    getLabRecordVitalsList("")

                    //getStepsHistory()
                    //callListRelativesApi()
                    profileSwitched = false
                } else {
                    Utilities.printLogError("Inside Normal Flow")
                    for (item in dataSyncList) {
                        when (item.apiName) {
                            ApiConstants.TRACK_PARAM_LIST_MASTER -> {
                                if (DateHelper.differenceInDays(
                                        DateHelper.convertStringDateToDate(
                                            item.syncDate,
                                            DateHelper.SERVER_DATE_YYYYMMDD
                                        ), DateHelper.currentDate!!
                                    ) >= 7
                                ) {
                                    getParameterList()
                                }
                            }

                            ApiConstants.DOC_TYPE_MASTER -> {
                                if (DateHelper.differenceInDays(
                                        DateHelper.convertStringDateToDate(
                                            item.syncDate,
                                            DateHelper.SERVER_DATE_YYYYMMDD
                                        ), DateHelper.currentDate!!
                                    ) >= 7
                                ) {
                                    callDocumentTypesApi(forceRefresh = true)
                                }
                            }
                            /*ApiConstants.BMI_HISTORY -> {
                                getBMIHistory(item.syncDate!!)
                            }
                            ApiConstants.BLOOD_PRESSURE_HISTORY -> {
                                getBloodPressureHistory(item.syncDate!!)
                            }
                            ApiConstants.WHR_HISTORY -> {
                                getWHRHistory(item.syncDate!!)
                            }*/
                            ApiConstants.PARAMETER_HISTORY -> {
                                getLabRecordList(item.syncDate!!)
                            }

                            ApiConstants.VITALS_HISTORY -> {
                                getLabRecordVitalsList(item.syncDate!!)
                            }
                        }
                    }
                    //getMedicalProfileSummary()

                    //getStepsHistory()
                    //callListRelativesApi()
                }

            }
            isBackgroundApiCall = true
        }
        _progressBar.value = Event(Event.HIDE_PROGRESS)
    }

    fun callSaveCloudMessagingIdApi(newFcmToken: String, forceRefresh: Boolean) =
        viewModelScope.launch(Dispatchers.Main) {

            Utilities.printLogError("\nOldFcmToken--->$fcmToken")
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
                                    accountID = preferenceUtils.getPreference(
                                        PreferenceConstants.ACCOUNTID,
                                        ""
                                    ),
                                    registrationID = newFcmToken,
                                    userMetaData = SaveCloudMessagingIdModel.UserMetaData(
                                        name = preferenceUtils.getPreference(
                                            PreferenceConstants.FIRSTNAME,
                                            ""
                                        ),
                                        email = preferenceUtils.getPreference(
                                            PreferenceConstants.EMAIL,
                                            ""
                                        ),
                                        contact = preferenceUtils.getPreference(
                                            PreferenceConstants.PHONE,
                                            ""
                                        ),
                                        gender = if (preferenceUtils.getPreference(
                                                PreferenceConstants.GENDER,
                                                ""
                                            ) == "1"
                                        ) "Male" else "Female",
                                        dateofbirth = preferenceUtils.getPreference(
                                            PreferenceConstants.DOB,
                                            ""
                                        )
                                    )
                                )
                            ), SaveCloudMessagingIdModel.JSONDataRequest::class.java
                        ),
                        preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
                    )
                    saveCloudMessagingIdSource = useCase.invokeSaveCloudMessagingId(
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

        val requestData = HraMedicalProfileSummaryModel(
            Gson().toJson(
                HraMedicalProfileSummaryModel.JSONDataRequest(
                    PersonID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                ), HraMedicalProfileSummaryModel.JSONDataRequest::class.java
            ),
            preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _medicalProfileSummary.removeSource(medicalProfileSummarySource)
        withContext(Dispatchers.IO) {
            medicalProfileSummarySource = useCase.invokeMedicalProfileSummary(
                isForceRefresh = false,
                data = requestData,
                personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
            )
        }
        _medicalProfileSummary.addSource(medicalProfileSummarySource) {
            try {
                _medicalProfileSummary.value = it
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        Utilities.printLogError("MedicalProfileSummery=>Inside view model::  $it")
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

    fun getParameterList() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = ParameterListModel(
            Gson().toJson(
                ParameterListModel.JSONDataRequest(
                    from = "60",
                    message = "Getting List.."
                ), ParameterListModel.JSONDataRequest::class.java
            ),
            preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _paramList.removeSource(paramUserSource)
        withContext(Dispatchers.IO) {
            paramUserSource = useCase.invokeParamList(data = requestData)
        }
        _paramList.addSource(paramUserSource) {
            try {
                _paramList.value = it
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                    }

                    Resource.Status.ERROR -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.errorNumber.equals("1100014", true)) {
                            _sessionError.value = Event(true)
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

    fun getLabRecordList(lastSyncDate: String) = viewModelScope.launch(Dispatchers.Main) {

        val jsonReq: LabRecordsListModel.JSONDataRequest = if (lastSyncDate.isNotEmpty()) {
            LabRecordsListModel.JSONDataRequest(
                personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"),
                lastSyncDate = DateHelper.convertDateTimeValue(
                    lastSyncDate,
                    DateHelper.SERVER_DATE_YYYYMMDD,
                    DateHelper.DISPLAY_DATE_DDMMMYYYY
                )!!
            )
        } else {
            LabRecordsListModel.JSONDataRequest(personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"))
        }
        val requestData = LabRecordsListModel(
            Gson().toJson(jsonReq, LabRecordsListModel.JSONDataRequest::class.java),
            preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _labRecordList.removeSource(labRecordUserSource)
        withContext(Dispatchers.IO) {
            labRecordUserSource =
                useCase.invokeLabRecordsList(data = requestData, personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"))
        }
        _labRecordList.addSource(labRecordUserSource) {
            try {
                _labRecordList.value = it
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        getParameterData("BMI", "BLOODPRESSURE", preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"))
                    }

                    Resource.Status.ERROR -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.errorNumber.equals("1100014", true)) {
                            _sessionError.value = Event(true)
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

    fun getLabRecordVitalsList(lastSyncDate: String) = viewModelScope.launch(Dispatchers.Main) {

        val jsonReq: VitalsHistoryModel.JSONDataRequest = if (lastSyncDate.isNotEmpty()) {
            VitalsHistoryModel.JSONDataRequest(
                personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"),
                lastSyncDate = DateHelper.convertDateTimeValue(
                    lastSyncDate,
                    DateHelper.SERVER_DATE_YYYYMMDD,
                    DateHelper.DISPLAY_DATE_DDMMMYYYY
                )!!
            )
        } else {
            VitalsHistoryModel.JSONDataRequest(personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"))
        }
        val requestData = VitalsHistoryModel(
            Gson().toJson(jsonReq, VitalsHistoryModel.JSONDataRequest::class.java),
            preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _labRecordVitalsList.removeSource(labRecordVitalsUserSource)
        withContext(Dispatchers.IO) {
            labRecordVitalsUserSource =
                useCase.invokeLabRecordsVitalsList(data = requestData, personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"))
        }
        _labRecordVitalsList.addSource(labRecordVitalsUserSource) {
            try {
                _labRecordVitalsList.value = it
                it.data?.let { data ->
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            Utilities.printLogError("ListVitalsHistor--->${data.vHistory}")
                        }

                        Resource.Status.ERROR -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.errorNumber.equals("1100014", true)) {
                                _sessionError.value = Event(true)
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
    }

    private fun getParameterData(profileCode: String, profileCodeTwo: String, personId: String) =
        viewModelScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                labParameterList.postValue(
                    useCase.getVitalsData(
                        profileCode,
                        profileCodeTwo,
                        personId
                    )
                )
            }
        }

    /*    fun callCheckAppUpdateApi(context: Context,activity: Activity) = viewModelScope.launch(Dispatchers.Main) {

            val requestData = CheckAppUpdateModel(
                Gson().toJson(
                    CheckAppUpdateModel.JSONDataRequest(
                        app = Configuration.strAppIdentifier,
                        device = Configuration.Device,
                        appVersion = Utilities.getAppVersion(context).toString()
                    ), CheckAppUpdateModel.JSONDataRequest::class.java
                ),
                preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            _checkAppUpdate.removeSource(checkAppUpdateSource)
            withContext(Dispatchers.IO) {
                checkAppUpdateSource =
                    useCase.invokeCheckAppUpdate(isForceRefresh = true, data = requestData)
            }
            _checkAppUpdate.addSource(checkAppUpdateSource) {
                try {
                    _checkAppUpdate.value = it

                    it.data?.let { getData ->
                        when (it.status) {
                            Resource.Status.SUCCESS -> {
                                Utilities.printLog("UpdateData=>${getData}")
                                if (getData.result.appVersion.isNotEmpty()) {
                                    val appVersion = getData.result.appVersion[0]

                                    val currentVersion = appVersion.currentVersion!!.toDouble().toInt()
                                    val forceUpdate = appVersion.forceUpdate

                                    val existingVersion = Utilities.getAppVersion(context)
                                    Utilities.printLog("CurrentVersion,ExistingVersion=>$currentVersion , $existingVersion")
                                    if (existingVersion < currentVersion) {
                                        if (forceUpdate) {
                                            logoutFromDB()
                                            Utilities.logoutForceUpdate(context, appVersion.forceUpdate, appVersion.description!!,activity)
                                        } else {
                                            val dialogUpdateApp = DialogUpdateApp(context, appVersion)
                                            dialogUpdateApp.show()
                                        }
                                    }
                                }
                            }

                            Resource.Status.ERROR -> {
                                _progressBar.value = Event(Event.HIDE_PROGRESS)
                                if (it.errorNumber.equals("1100014", true)) {
                                    _sessionError.value = Event(true)
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

        }*/

    /*    fun getAppVersionDetails(context: Context) = viewModelScope.launch {
            var versionDetails: AppVersion? = null
            withContext(Dispatchers.IO) {
                versionDetails = useCase.invokeGetAppVersionDetails()
                Utilities.printLogError("appVersionDetails--->$versionDetails")
            }
            if (versionDetails != null) {
                val lastUpdateDate = versionDetails!!.lastUpdateDate!!
                val updateCallInterval = versionDetails!!.apiCallInterval
                val forceUpdate = versionDetails!!.forceUpdate
                val currentVersion = versionDetails!!.currentVersion!!.toDouble().toInt()
                //val currentVersion = 103
                val existingVersion = Utilities.getAppVersion(context)
                if (forceUpdate) {
                    if (existingVersion < currentVersion) {
                        val dialogUpdateApp = DialogUpdateApp(context, versionDetails!!)
                        dialogUpdateApp.show()
                    } else {
                        callCheckAppUpdateApi(context)
                    }
                } else {
                    if (existingVersion < currentVersion) {
                        if (DateHelper.getDifferenceInDays(lastUpdateDate, DateHelper.currentDateAsStringyyyyMMdd) >= updateCallInterval) {
                            val dialogUpdateApp = DialogUpdateApp(context, versionDetails!!)
                            dialogUpdateApp.show()
                        }
                    }
                }
            } else {
                callCheckAppUpdateApi(context)
            }
        }*/

    fun logoutFromDB() = viewModelScope.launch(Dispatchers.Main) {
        EntityID = "0"
        withContext(Dispatchers.IO) {
            useCase.invokeLogout()
        }
    }

    fun refreshPersonId() {
        personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "")
        email = preferenceUtils.getPreference(PreferenceConstants.EMAIL, "")
        phone = preferenceUtils.getPreference(PreferenceConstants.PHONE, "")
        firstName = preferenceUtils.getPreference(PreferenceConstants.FIRSTNAME, "")
        gender = preferenceUtils.getPreference(PreferenceConstants.GENDER, "")
    }

    private fun refreshFcmToken(newToken: String) {
        preferenceUtils.storePreference(PreferenceConstants.FCM_REGISTRATION_ID, newToken)
    }

    private fun isSelfUser(): Boolean {
        val personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
        val adminPersonId = preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0")
        var isSelfUser = false
        if (!Utilities.isNullOrEmptyOrZero(personId)
            && !Utilities.isNullOrEmptyOrZero(adminPersonId)
            && personId == adminPersonId
        ) {
            isSelfUser = true
        }
        return isSelfUser
    }

    fun getMainUserPersonID(): String {
        val adminPersonId = preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0")
        Utilities.printLog("AdminPersonId--->$adminPersonId")
        return adminPersonId
    }

    fun getStepsHistory() {
        if (FitnessDataManager.getInstance(context)!!.oAuthPermissionsApproved() && isSelfUser()) {
            if (getIsFitnessDataNotSync()) {
                StepsDataSingleton.instance!!.stepHistoryList.clear()
                //HomeStepCountHelper.refreshStepCountSync(context)
                val fitnessDataSyncHelper = StepCountHelper(context!!)
                fitnessDataSyncHelper.synchronize(context, null)
            } else {
                callStepsHistoryApi()
            }
        }
    }

    // For Step Sync
    private fun callStepsHistoryApi() = viewModelScope.launch(Dispatchers.Main) {

        val stepsDataSingleton = StepsDataSingleton.instance!!
        val df = SimpleDateFormat(DateHelper.SERVER_DATE_YYYYMMDD, Locale.ENGLISH)
        val calEndTime = Calendar.getInstance()
        calEndTime.add(Calendar.DATE, -30)
        val fromDate = df.format(calEndTime.time)

        val searchCriteria: StepsHistoryModel.SearchCriteria = StepsHistoryModel.SearchCriteria(
            fromDate = fromDate, toDate = DateHelper.currentDateAsStringyyyyMMdd,
            personID = preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0")
        )

        val requestData = StepsHistoryModel(
            Gson().toJson(
                StepsHistoryModel.JSONDataRequest(
                    searchCriteria = searchCriteria, message = "Getting List.."
                ),
                StepsHistoryModel.JSONDataRequest::class.java
            ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        //_progressBar.value = Event("Loading...")
        _stepsHistoryList.removeSource(stepsHistorySource)
        withContext(Dispatchers.IO) {
            stepsHistorySource = useCase.invokeStepsHistory(requestData)
        }
        _stepsHistoryList.addSource(stepsHistorySource) {
            try {
                _stepsHistoryList.value = it

                it.data?.let { getData ->
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            //Utilities.printLog("History---> ${it.data}")
                            Utilities.printLogError("stepsHistoryList--->${getData.stepGoalHistory.size}")
                            val history = getData.stepGoalHistory.toMutableList()
                            //Utilities.printData("ApiHistory", history)
                            if (history.isNotEmpty()) {
                                stepsDataSingleton.stepHistoryList.clear()
                                stepsDataSingleton.stepHistoryList.addAll(history)
                            }

                            if (FitnessDataManager.getInstance(context)!!
                                    .oAuthPermissionsApproved() && isSelfUser()
                            ) {
                                //HomeStepCountHelper.refreshStepCountSync(context)
                                val fitnessDataSyncHelper = StepCountHelper(context!!)
                                fitnessDataSyncHelper.synchronize(context, null, false)
                            }
                        }

                        Resource.Status.ERROR -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.errorNumber.equals("1100014", true)) {
                                _sessionError.value = Event(true)
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
    }

    private fun callDocumentTypesApi(forceRefresh: Boolean) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = ListDocumentTypesModel(
                Gson().toJson(
                    ListDocumentTypesModel.JSONDataRequest(
                        from = "70"
                    ), ListDocumentTypesModel.JSONDataRequest::class.java
                ),
                preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            _listDocumentTypes.removeSource(listDocumentTypesSource)
            withContext(Dispatchers.IO) {
                listDocumentTypesSource =
                    useCase.invokeDocumentType(isForceRefresh = forceRefresh, data = requestData)
            }
            _listDocumentTypes.addSource(listDocumentTypesSource) {

                try {
                    _listDocumentTypes.value = it

                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                        }

                        Resource.Status.ERROR -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.errorNumber.equals("1100014", true)) {
                                _sessionError.value = Event(true)
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

    /*    private fun callListRelativesApi() = viewModelScope.launch(Dispatchers.Main) {

            val adminPersonId = preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0")
            val requestData = ListRelativesModel(Gson().toJson(ListRelativesModel.JSONDataRequest(
                personID = adminPersonId), ListRelativesModel.JSONDataRequest::class.java),
                preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

            _listRelatives.removeSource(listRelativesSource)
            withContext(Dispatchers.IO) {
                listRelativesSource = useCase.invokeRelativesList(isForceRefresh = false, data = requestData)
            }
            _listRelatives.addSource(listRelativesSource) {
                _listRelatives.value = it

                if (it.status == Resource.Status.SUCCESS) {
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
        }*/

    /*    fun getBMIHistory(lastSyncDate: String) = viewModelScope.launch(Dispatchers.Main) {

    *//*        val requestData = BMIHistoryModel(Gson().toJson(BMIHistoryModel.JSONDataRequest(
                    personID = sharedPref.getString(PreferenceConstants.PERSONID,"")!!), BMIHistoryModel.JSONDataRequest::class.java), sharedPref.getString(PreferenceConstants.TOKEN,"")!! )*//*

        val jsonReq: BMIHistoryModel.JSONDataRequest
        if (lastSyncDate.isNotEmpty()) { jsonReq = BMIHistoryModel.JSONDataRequest(
            personID = personId,
            lastSyncDate = DateHelper.convertDateTimeValue(lastSyncDate, DateHelper.SERVER_DATE_YYYYMMDD, DateHelper.DISPLAY_DATE_DDMMMYYYY)!!
        )
        } else {
            jsonReq = BMIHistoryModel.JSONDataRequest(personID = personId)
        }
        val requestData = BMIHistoryModel(Gson().toJson(jsonReq, BMIHistoryModel.JSONDataRequest::class.java),
            preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

        _bmiHistoryList.removeSource(bmiUserSource)
        withContext(Dispatchers.IO) {
            bmiUserSource = useCase.invokeBMIHistory(data = requestData, personId)
        }
        _bmiHistoryList.addSource(bmiUserSource) {
            _bmiHistoryList.value = it.data
            if (it.status == Resource.Status.SUCCESS) {
            }

            if (it.status == Resource.Status.ERROR) {
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    //toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun getWHRHistory(lastSyncDate: String) = viewModelScope.launch(Dispatchers.Main) {

*//*        val requestData = WHRHistoryModel(Gson().toJson(WHRHistoryModel.JSONDataRequest(
                    personID = sharedPref.getString(PreferenceConstants.PERSONID,"")!!), WHRHistoryModel.JSONDataRequest::class.java), sharedPref.getString(PreferenceConstants.TOKEN,"")!! )*//*

        val jsonReq: WHRHistoryModel.JSONDataRequest
        if (lastSyncDate.isNotEmpty()) {
            jsonReq = WHRHistoryModel.JSONDataRequest(
                personID = personId,
                lastSyncDate = DateHelper.convertDateTimeValue(lastSyncDate, DateHelper.SERVER_DATE_YYYYMMDD, DateHelper.DISPLAY_DATE_DDMMMYYYY)!!)
        } else {
            jsonReq = WHRHistoryModel.JSONDataRequest(personID = personId)
        }
        val requestData = WHRHistoryModel(Gson().toJson(jsonReq, WHRHistoryModel.JSONDataRequest::class.java),
            preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

        _whrHistoryList.removeSource(whrUserSource)
        withContext(Dispatchers.IO) {
            whrUserSource = useCase.invokeWHRHistory(data = requestData, personId)
        }
        _whrHistoryList.addSource(whrUserSource) {
            _whrHistoryList.value = it.data
            if (it.status == Resource.Status.SUCCESS) {
            }

            if (it.status == Resource.Status.ERROR) {
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    //toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun getBloodPressureHistory(lastSyncDate: String) = viewModelScope.launch(Dispatchers.Main) {

*//*        val requestData = BloodPressureHistoryModel(Gson().toJson(BloodPressureHistoryModel.JSONDataRequest(
                    personID = sharedPref.getString(PreferenceConstants.PERSONID,"")!!), BloodPressureHistoryModel.JSONDataRequest::class.java), sharedPref.getString(PreferenceConstants.TOKEN,"")!! )*//*

        val jsonReq: BloodPressureHistoryModel.JSONDataRequest
        if (lastSyncDate.isNotEmpty()) {
            jsonReq = BloodPressureHistoryModel.JSONDataRequest(
                personID = personId,
                lastSyncDate = DateHelper.convertDateTimeValue(lastSyncDate, DateHelper.SERVER_DATE_YYYYMMDD, DateHelper.DISPLAY_DATE_DDMMMYYYY)!!)
        } else {
            jsonReq = BloodPressureHistoryModel.JSONDataRequest(personID = personId)
        }
        val requestData = BloodPressureHistoryModel(Gson().toJson(jsonReq, BloodPressureHistoryModel.JSONDataRequest::class.java),
            preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

        _bloodPressureHistoryList.removeSource(bloodPressureUserSource)
        withContext(Dispatchers.IO) {
            bloodPressureUserSource =
                useCase.invokeBloodPressureHistory(data = requestData, personId)
        }
        _bloodPressureHistoryList.addSource(bloodPressureUserSource) {
            _bloodPressureHistoryList.value = it.data
            if (it.status == Resource.Status.SUCCESS) {
            }

            if (it.status == Resource.Status.ERROR) {
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    //toastMessage(it.errorMessage)
                }
            }
        }
    }*/


}