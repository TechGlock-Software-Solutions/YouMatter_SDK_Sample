package com.techglock.health.app.security.viewmodel

//import com.caressa.allizhealth.app.model.tempconst.Configuration.EntityID
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.techglock.health.app.SSOLoaderActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Configuration
import com.techglock.health.app.common.constants.Configuration.EntityID
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.EncryptionUtility
import com.techglock.health.app.common.utils.Event
import com.techglock.health.app.common.utils.PreferenceUtils
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.Validation
import com.techglock.health.app.home.ui.HomeMainActivity
import com.techglock.health.app.model.entity.Users
import com.techglock.health.app.model.home.AddFeatureAccessLog
import com.techglock.health.app.model.home.CheckAppUpdateModel
import com.techglock.health.app.model.security.DarwinBoxDataModel
import com.techglock.health.app.repository.utils.Resource
import com.techglock.health.app.security.domain.StartupManagementUseCase
import com.techglock.health.app.security.domain.UserManagementUseCase
import com.techglock.health.app.security.ui.SplashScreenActivity
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class StartupViewModel @Inject constructor(
    application: Application,
    private val startupManagementUseCase: StartupManagementUseCase,
    private val userManagementUseCase: UserManagementUseCase,
    private val preferenceUtils: PreferenceUtils,
    val context: Context?
) : BaseViewModel(application) {

    private var authToken = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")

    private var checkAppUpdateSource: LiveData<Resource<CheckAppUpdateModel.CheckAppUpdateResponse>> =
        MutableLiveData()
    private val _checkAppUpdate =
        MediatorLiveData<Resource<CheckAppUpdateModel.CheckAppUpdateResponse>>()
    val checkAppUpdate: LiveData<Resource<CheckAppUpdateModel.CheckAppUpdateResponse>> get() = _checkAppUpdate

    private var darwinBoxDataSource: LiveData<Resource<DarwinBoxDataModel.DarwinBoxDataResponse>> =
        MutableLiveData()
    private val _darwinBoxData =
        MediatorLiveData<Resource<DarwinBoxDataModel.DarwinBoxDataResponse>>()
    val darwinBoxData: LiveData<Resource<DarwinBoxDataModel.DarwinBoxDataResponse>> get() = _darwinBoxData

    private lateinit var argsLogin: String
    private var loginUserSource: LiveData<Resource<Users>> = MutableLiveData()
    private val _isLogin = MediatorLiveData<Users>()
    val isLogin: LiveData<Users> get() = _isLogin

    private var registerUserSource: LiveData<Resource<Users>> = MutableLiveData()
    private val _isRegister = MediatorLiveData<Users>()
    val isRegister: LiveData<Users> get() = _isRegister

    private var addFeatureAccessLogSource: LiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>> =
        MutableLiveData()
    private val _addFeatureAccessLog =
        MediatorLiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>>()
    val addFeatureAccessLog: LiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>> get() = _addFeatureAccessLog

    private var ssoLoginRegisterSource: LiveData<Resource<Users>> = MutableLiveData()
    private val _ssoLoginRegister = MediatorLiveData<Users>()
    val ssoLoginRegister: LiveData<Users> get() = _ssoLoginRegister

    fun getMainUserPersonID(): String {
        return preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0")
    }

    fun getOrgName(): String {
        return preferenceUtils.getPreference(PreferenceConstants.ORG_NAME, "")
    }

    fun getEmployeeId(): String {
        return preferenceUtils.getPreference(PreferenceConstants.ORG_EMPLOYEE_ID, "")
    }

    fun isFirstTimeUser(): Boolean {
        val isFirstVisit =
            preferenceUtils.getBooleanPreference(PreferenceConstants.IS_FIRST_VISIT, true)
        Utilities.printLogError("IsFirstVisit--->$isFirstVisit")
        return isFirstVisit
    }

    fun setFirstTimeUserFlag(flag: Boolean) {
        preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_FIRST_VISIT, flag)
    }

    fun isSelfUser(): Boolean {
        val personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
        val adminPersonId = preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0")
        Utilities.printLogError("PersonId--->$personId")
        Utilities.printLogError("AdminPersonId--->$adminPersonId")
        var isSelfUser = false
        if (!Utilities.isNullOrEmptyOrZero(personId) && !Utilities.isNullOrEmptyOrZero(adminPersonId) && personId == adminPersonId) {
            isSelfUser = true
        }
        return isSelfUser
    }

    fun getLoginStatus(): Boolean {
        val isLogin = preferenceUtils.getBooleanPreference(PreferenceConstants.IS_LOGIN, false)
        if (isLogin) {
            //val adminPersonId = preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID,"")!!
            //preferenceUtils.storePreference(PreferenceConstants.PERSONID, adminPersonId)
        }
        Utilities.printLogError("IsUserLoggedIn--->$isLogin")
        return isLogin
    }

    fun getBaseurlChangedStatus(): Boolean {
        val isBaseurlChanged =
            preferenceUtils.getBooleanPreference(PreferenceConstants.IS_BASEURL_CHANGED, false)
        Utilities.printLogError("IsBaseurlChanged--->$isBaseurlChanged")
        return isBaseurlChanged
    }

    fun setBaseurlChangedStatus(flag: Boolean) {
        preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_BASEURL_CHANGED, flag)
    }

    fun clearPreviousData() {

    }

    fun callSSO(
        ssoData: String ,
        ssoLoaderActivity: SSOLoaderActivity) = viewModelScope.launch(Dispatchers.Main) {
            try {

                val ssoInput = JSONObject(ssoData)
                val ssoObject = JSONObject()
                //ssoObject.put(Constants.UserConstants.AUTH_TYPE, "SSO")
                //ssoObject.put(Constants.UserConstants.SOURCE, Constants.SSO_SOURCE)
                ssoObject.put(Constants.UserConstants.PARTNER_CODE, ssoInput.get("PartnerCode"))
                ssoObject.put(Constants.UserConstants.NAME, ssoInput.get("Name"))
                ssoObject.put(Constants.UserConstants.EMAIL_ADDRESS, ssoInput.get("EmailAddress"))
                ssoObject.put(Constants.UserConstants.PHONE_NUMBER, ssoInput.get("PhoneNumber"))
                ssoObject.put(Constants.UserConstants.DOB, ssoInput.get("DOB"))
                ssoObject.put(Constants.UserConstants.GENDER, ssoInput.get("Gender"))
                ssoObject.put(Constants.UserConstants.ORG_NAME, ssoInput.get("PartnerCode"))
                ssoObject.put(Constants.UserConstants.CLIENT_KEY, ssoInput.get("ClientKey"))
                ssoObject.put(Constants.UserConstants.CLIENT_USER_ID, ssoInput.get("ClientUserId"))
                if ( ssoInput.has("EmployeeID") && !Utilities.isNullOrEmpty(ssoInput.get("EmployeeID").toString()) ) {
                    ssoObject.put(Constants.UserConstants.EMPLOYEE_ID, ssoInput.get("EmployeeID"))
                } else {
                    ssoObject.put(Constants.UserConstants.EMPLOYEE_ID,"")
                }
                //ssoObject.put(Constants.UserConstants.CLIENT_APP_BUNDLE_ID, ssoInput.get("ClientAppBundleId"))

                ssoObject.put(Constants.UserConstants.MEDIUM, "ANDROID")
                ssoObject.put(Constants.UserConstants.HANDSHAKE, "PER")
                ssoObject.put(Constants.UserConstants.PASSWORD, "")
                ssoObject.put(Constants.UserConstants.OTP, "")
                ssoObject.put(Constants.UserConstants.CLUSTER_CODE, "")

                Utilities.printLogError("SSO_Before---> $ssoObject")
                val argsSso = EncryptionUtility.encrypt(ssoObject.toString())

                _ssoLoginRegister.removeSource(ssoLoginRegisterSource)
                withContext(Dispatchers.IO) {
                    ssoLoginRegisterSource = startupManagementUseCase.invokeSso( data = argsSso)
                }
                _ssoLoginRegister.addSource(ssoLoginRegisterSource) {
                    it?.data?.let { data ->
                        _ssoLoginRegister.value = data
                    }

                    if (it.status == Resource.Status.SUCCESS) {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        it?.data?.let { getData ->
                            Utilities.printLog("SSO_Resp--->$getData")
                            val pid = getData.personId.toDouble().toInt()
                            Utilities.printLog("Person Id => $pid")
                            preferenceUtils.storePreference(PreferenceConstants.TOKEN, getData.authToken)
                            preferenceUtils.storePreference(PreferenceConstants.ADMIN_PERSON_ID, pid.toString())
                            preferenceUtils.storePreference(PreferenceConstants.PERSONID, pid.toString())
                            preferenceUtils.storePreference(PreferenceConstants.ACCOUNTID, getData.accountId.toDouble().toInt().toString())
                            preferenceUtils.storePreference(PreferenceConstants.EMAIL, getData.emailAddress)
                            preferenceUtils.storePreference(PreferenceConstants.PHONE, getData.phoneNumber)
                            preferenceUtils.storePreference(PreferenceConstants.FIRSTNAME, getData.firstName)
                            preferenceUtils.storePreference(PreferenceConstants.PROFILE_IMAGE_ID, getData.profileImageID.toString())
                            preferenceUtils.storePreference(PreferenceConstants.ORG_NAME, getData.orgName)
                            preferenceUtils.storePreference(PreferenceConstants.ORG_EMPLOYEE_ID, getData.orgEmpID)
                            preferenceUtils.storePreference(PreferenceConstants.RELATIONSHIPCODE, Constants.SELF_RELATIONSHIP_CODE)
                            preferenceUtils.storePreference(PreferenceConstants.JOINING_DATE, getData.createdDate!!.split("T").toTypedArray()[0])
                            if ( !Utilities.isNullOrEmpty(getData.gender) ) {
                                preferenceUtils.storePreference(PreferenceConstants.GENDER, getData.gender)
                            }
                            if ( !Utilities.isNullOrEmpty(getData.dateOfBirth) ) {
                                preferenceUtils.storePreference(PreferenceConstants.DOB, getData.dateOfBirth!!.split("T").toTypedArray()[0])
                            }
                            if ( !Utilities.isNullOrEmpty(getData.orgName)
                                && getData.orgName.equals(Constants.BOI,ignoreCase = true) ) {
                                Utilities.setEmployeeType(Constants.BOI)
                            }
                            Utilities.setEmployeeType(Constants.BOI)
                            preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_LOGIN, true)
                            preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_FIRST_VISIT, false)
                            preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_BASEURL_CHANGED, true)
                            CleverTapHelper.addUser(context)
                            //Constants.LOGIN_SOURCE -> CleverTapHelper.pushEvent(context, CleverTapConstants.LOGIN_WITH_OTP)
                            //preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_OTP_AUTHENTICATED, true)
                            //preferenceUtils.storePreference(PreferenceConstants.POLICY_MOBILE_NUMBER, getData.phoneNumber)
                            Handler(Looper.getMainLooper()).postDelayed({
                                val intent = Intent(context,HomeMainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                //intent.putExtra(Constants.FROM,"")
                                ssoLoaderActivity.startActivity(intent)
                                ssoLoaderActivity.finish()
                            },(Constants.LOADER_ANIM_DELAY_IN_MS).toLong())
                        } ?: run {
                            Utilities.printLog("No Data Found")
                        }
                    }
                    if (it.status == Resource.Status.ERROR) {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        Utilities.toastMessageLong(context,it.errorMessage)
                        ssoLoaderActivity.finish()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }

    fun callCheckAppUpdateApi() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = CheckAppUpdateModel(
            Gson().toJson(
                CheckAppUpdateModel.JSONDataRequest(
                    app = Configuration.strAppIdentifier,
                    device = Configuration.Device,
                    appVersion = Utilities.getAppVersion(context!!).toString()
                ), CheckAppUpdateModel.JSONDataRequest::class.java
            ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _checkAppUpdate.removeSource(checkAppUpdateSource)
        withContext(Dispatchers.IO) {
            checkAppUpdateSource = startupManagementUseCase.invokeCheckAppUpdate(
                isForceRefresh = true, data = requestData
            )
        }
        _checkAppUpdate.addSource(checkAppUpdateSource) {
            _checkAppUpdate.value = it

            when (it.status) {
                Resource.Status.SUCCESS -> {

                }

                else -> {
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

    fun callGetLoginInfoWithDarwinBoxApi(darwinBoxUrl: String, activity: SplashScreenActivity) =
        viewModelScope.launch(Dispatchers.Main) {
            try {
                EntityID = "0"
                val requestData = DarwinBoxDataModel(
                    Gson().toJson(
                        DarwinBoxDataModel.JSONDataRequest(
                            encodedInput = darwinBoxUrl.split(".").toTypedArray()[1]
                        ), DarwinBoxDataModel.JSONDataRequest::class.java
                    )
                )

                _darwinBoxData.removeSource(darwinBoxDataSource)
                withContext(Dispatchers.IO) {
                    darwinBoxDataSource = startupManagementUseCase.invokeDarwinBoxDataResponse(
                        isForceRefresh = true, data = requestData
                    )
                }
                _darwinBoxData.addSource(darwinBoxDataSource) {
                    _darwinBoxData.value = it

                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            Utilities.printData("LoginInfoWithDarwinBox", it.data!!, true)
                            if (it.data.darwinBoxCheckTokenAPI.status == 1) {
                                if (it.data.darwinBoxEmployeeAPI.status == 1) {
                                    val empData = it.data.darwinBoxEmployeeAPI.employeeData[0]
                                    val name =
                                        Validation.checkAndConvertToValidUsername("${empData.firstName!!} ${empData.lastName!!}")
                                    var number = empData.primaryMobileNumber!!
                                    if (empData.primaryMobileNumber.length > 10) {
                                        number = empData.primaryMobileNumber.takeLast(10)
                                    }
                                    val dob = DateHelper.convertDateSourceToDestination(
                                        empData.dateOfBirth!!,
                                        DateHelper.DISPLAY_DATE_DDMMMYYYY,
                                        DateHelper.SERVER_DATE_YYYYMMDD
                                    )
                                    when (it.data.isAccountExists!!.lowercase()) {
                                        Constants.TRUE -> {
                                            callLogin(
                                                name = name,
                                                emailStr = empData.companyEmailId!!,
                                                phoneNumber = number,
                                                dob = dob,
                                                gender = empData.gender!!,
                                                employeeID = it.data.orgEmpID!!,
                                                orgName = it.data.orgName!!,
                                                activity = activity
                                            )
                                        }

                                        Constants.FALSE -> {
                                            callRegisterAPI(
                                                name = name,
                                                emailStr = empData.companyEmailId!!,
                                                phoneNumber = number,
                                                dob = dob,
                                                gender = empData.gender!!,
                                                employeeID = it.data.orgEmpID!!,
                                                orgName = it.data.orgName!!,
                                                activity = activity
                                            )
                                        }
                                    }
                                } else {
                                    Utilities.toastMessageLong(
                                        context, it.data.darwinBoxEmployeeAPI.message!!
                                    )
                                }
                            } else {
                                Utilities.toastMessageLong(
                                    context, it.data.darwinBoxCheckTokenAPI.message!!
                                )
                            }
                        }

                        else -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.errorNumber.equals("1100014", true)) {
                                _sessionError.value = Event(true)
                            } else {
                                toastMessage(it.errorMessage)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private fun callLogin(
        name: String = "",
        emailStr: String,
        phoneNumber: String = "",
        dob: String = "",
        gender: String = "",
        passwordStr: String = "",
        employeeID: String = "",
        orgName: String = "",
        socialLogin: Boolean = true,
        source: String = Constants.DARWINBOX_SOURCE,
        activity: Activity
    ) = viewModelScope.launch(Dispatchers.Main) {

        argsLogin = Utilities.getEncryptedData(
            name = name,
            //name = emailStr,
            email = emailStr,
            phoneNumber = phoneNumber,
            dob = dob,
            gender = gender,
            password = passwordStr,
            employeeId = employeeID,
            orgName = orgName,
            source = source,
            isSocial = socialLogin
        )

        //_progressBar.value = Event("Authenticating..")
        _isLogin.removeSource(loginUserSource) // We make sure there is only one source of livedata (allowing us properly refresh)
        withContext(Dispatchers.IO) {
            loginUserSource = userManagementUseCase(isForceRefresh = true, data = argsLogin)
        }
        _isLogin.addSource(loginUserSource) {
            it?.data?.let { data ->
                _isLogin.value = data
            }

            when (it.status) {
                Resource.Status.SUCCESS -> {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    try {
                        Utilities.printLog("LoginResp--->" + it.data)
                        preferenceUtils.storePreference(
                            PreferenceConstants.EMAIL,
                            it.data?.emailAddress!!
                        )
                        preferenceUtils.storePreference(
                            PreferenceConstants.PHONE,
                            it.data.phoneNumber
                        )
                        preferenceUtils.storePreference(
                            PreferenceConstants.TOKEN,
                            it.data.authToken
                        )
                        preferenceUtils.storePreference(
                            PreferenceConstants.ACCOUNTID,
                            it.data.accountId.toDouble().toInt().toString()
                        )
                        preferenceUtils.storePreference(
                            PreferenceConstants.FIRSTNAME,
                            it.data.firstName
                        )
                        preferenceUtils.storePreference(
                            PreferenceConstants.PROFILE_IMAGE_ID,
                            it.data.profileImageID.toString()
                        )
                        preferenceUtils.storePreference(PreferenceConstants.GENDER, it.data.gender)
                        val pid = it.data.personId.toDouble().toInt()
                        Utilities.printLog("Person Id => $pid")
                        preferenceUtils.storePreference(
                            PreferenceConstants.PERSONID,
                            pid.toString()
                        )
                        preferenceUtils.storePreference(
                            PreferenceConstants.ADMIN_PERSON_ID,
                            pid.toString()
                        )
                        preferenceUtils.storePreference(
                            PreferenceConstants.RELATIONSHIPCODE,
                            Constants.SELF_RELATIONSHIP_CODE
                        )
                        preferenceUtils.storePreference(
                            PreferenceConstants.JOINING_DATE,
                            it.data.createdDate!!.split("T").toTypedArray()[0]
                        )
                        preferenceUtils.storePreference(
                            PreferenceConstants.DOB,
                            it.data.dateOfBirth!!.split("T").toTypedArray()[0]
                        )
                        preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_LOGIN, true)
                        preferenceUtils.storeBooleanPreference(
                            PreferenceConstants.IS_FIRST_VISIT,
                            false
                        )
                        preferenceUtils.storeBooleanPreference(
                            PreferenceConstants.IS_BASEURL_CHANGED,
                            true
                        )

                        Utilities.setEmployeeType(Constants.SUD_LIFE)
                        Utilities.logCleverTapEmployeeEventLogin(context!!, Constants.SUD_LIFE)
                        CleverTapHelper.addUser(context)
                        val data = HashMap<String, Any>()
                        data[CleverTapConstants.EMPLOYEE_ID] = employeeID
                        CleverTapHelper.pushEventWithProperties(
                            context,
                            CleverTapConstants.LOGIN_WITH_DARWINBOX,
                            data
                        )

                        //val desc = "EmployeeID:$employeeID|PersonID:$pid"
                        //callAddFeatureAccessLogApi(pid!!,Constants.LOGIN_BY_DARWINBOX,desc)
                        activity.openAnotherActivity(
                            destination = NavigationConstants.HOME,
                            clearTop = true
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                else -> {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    private fun callRegisterAPI(
        name: String = "",
        emailStr: String,
        phoneNumber: String = "",
        dob: String = "",
        gender: String = "1",
        passwordStr: String = "Test@1234",
        source: String = Constants.DARWINBOX_SOURCE,
        employeeID: String = "",
        orgName: String = "",
        socialLogin: Boolean = false,
        activity: Activity
    ) = viewModelScope.launch(Dispatchers.Main) {

        val registerEnc = Utilities.getEncryptedData(
            name = name,
            email = emailStr,
            phoneNumber = phoneNumber,
            dob = dob,
            gender = gender,
            password = passwordStr,
            employeeId = employeeID,
            orgName = orgName,
            source = source,
            isSocial = socialLogin
        )

        _progressBar.value = Event("Authentication User")
        _isRegister.removeSource(registerUserSource)
        withContext(Dispatchers.IO) {
            registerUserSource = userManagementUseCase.invokeRegistration(
                data = registerEnc, isOtpAuthenticated = true
            )
        }
        _isRegister.addSource(registerUserSource) {

            it?.data?.let { data ->
                _isRegister.value = data
            }
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    try {
                        it?.data?.let { getData ->
                            preferenceUtils.storePreference(
                                PreferenceConstants.EMAIL,
                                getData.emailAddress
                            )
                            preferenceUtils.storePreference(
                                PreferenceConstants.PHONE,
                                getData.phoneNumber
                            )
                            preferenceUtils.storePreference(
                                PreferenceConstants.TOKEN,
                                getData.authToken
                            )
                            preferenceUtils.storePreference(
                                PreferenceConstants.ACCOUNTID,
                                getData.accountId.toDouble().toInt().toString()
                            )
                            preferenceUtils.storePreference(
                                PreferenceConstants.FIRSTNAME,
                                getData.firstName
                            )
                            preferenceUtils.storePreference(
                                PreferenceConstants.PROFILE_IMAGE_ID,
                                getData.profileImageID.toString()
                            )
                            preferenceUtils.storePreference(
                                PreferenceConstants.GENDER,
                                getData.gender
                            )
                            val pid = getData.personId.toDouble().toInt()
                            Utilities.printLogError("Person Id => $pid")
                            preferenceUtils.storePreference(
                                PreferenceConstants.PERSONID,
                                pid.toString()
                            )
                            preferenceUtils.storePreference(
                                PreferenceConstants.ADMIN_PERSON_ID,
                                pid.toString()
                            )
                            preferenceUtils.storePreference(
                                PreferenceConstants.RELATIONSHIPCODE,
                                Constants.SELF_RELATIONSHIP_CODE
                            )
                            preferenceUtils.storeBooleanPreference(
                                PreferenceConstants.IS_OTP_AUTHENTICATED,
                                true
                            )
                            preferenceUtils.storePreference(
                                PreferenceConstants.POLICY_MOBILE_NUMBER,
                                getData.phoneNumber
                            )
                            var joiningDate = DateHelper.currentDateAsStringyyyyMMdd
                            if (!Utilities.isNullOrEmpty(getData.createdDate!!)) {
                                joiningDate = getData.createdDate!!.split("T").toTypedArray()[0]
                            }
                            preferenceUtils.storePreference(
                                PreferenceConstants.JOINING_DATE,
                                joiningDate
                            )
                            preferenceUtils.storePreference(
                                PreferenceConstants.DOB,
                                getData.dateOfBirth!!.split("T").toTypedArray()[0]
                            )
                            preferenceUtils.storeBooleanPreference(
                                PreferenceConstants.IS_LOGIN,
                                true
                            )
                            preferenceUtils.storeBooleanPreference(
                                PreferenceConstants.IS_FIRST_VISIT,
                                false
                            )
                            preferenceUtils.storeBooleanPreference(
                                PreferenceConstants.IS_BASEURL_CHANGED,
                                true
                            )

                            Utilities.setEmployeeType(Constants.SUD_LIFE)
                            Utilities.logCleverTapEmployeeEventSignUp(context!!, Constants.SUD_LIFE)
                            CleverTapHelper.addUser(context)
                            val data = HashMap<String, Any>()
                            data[CleverTapConstants.EMPLOYEE_ID] = employeeID
                            CleverTapHelper.pushEventWithProperties(
                                context,
                                CleverTapConstants.SIGN_UP_WITH_DARWINBOX,
                                data
                            )

                            //val desc = "EmployeeID:$employeeID|PersonID:$pid"
                            //callAddFeatureAccessLogApi(pid!!,Constants.REGISTRATION_BY_DARWINBOX,desc)
                            activity.openAnotherActivity(
                                destination = NavigationConstants.HOME,
                                clearTop = true
                            )
                        } ?: run {
                            Utilities.printLog("No Data Found")

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                else -> {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                }
            }
        }

    }

    private fun callAddFeatureAccessLogApi(personId: Int, code: String, desc: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = AddFeatureAccessLog(
                Gson().toJson(
                    AddFeatureAccessLog.JSONDataRequest(
                        AddFeatureAccessLog.FeatureAccessLog(
                            partnerCode = Constants.PartnerCode,
                            personId = personId,
                            code = code,
                            description = desc,
                            service = Constants.DARWINBOX,
                            url = "",
                            appversion = Utilities.getVersionName(context!!),
                            device = Build.BRAND + "~" + Build.MODEL,
                            devicetype = "Android",
                            platform = "App"
                        )
                    ), AddFeatureAccessLog.JSONDataRequest::class.java
                ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            //_progressBar.value = Event("")
            _addFeatureAccessLog.removeSource(addFeatureAccessLogSource)
            withContext(Dispatchers.IO) {
                addFeatureAccessLogSource = userManagementUseCase.invokeAddFeatureAccessLog(
                    isForceRefresh = true, data = requestData
                )
            }
            _addFeatureAccessLog.addSource(addFeatureAccessLogSource) {
                _addFeatureAccessLog.value = it

                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        //_progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.data != null) {
                            if (!Utilities.isNullOrEmptyOrZero(it.data.featureAccessLogID)) {
                                //toastMessage("Count Saved")
                            }
                        }
                    }

                    else -> {
                        //_progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.errorNumber.equals("1100014", true)) {
                            _sessionError.value = Event(true)
                        } else {
                            //toastMessage(it.errorMessage)
                        }
                    }
                }
            }

        }

    fun isBiometricAuthentication(): Boolean {
        val isBiometricAuthentication = preferenceUtils.getBooleanPreference(
            PreferenceConstants.IS_BIOMETRIC_AUTHENTICATION, true
        )
        Utilities.printLogError("IsBiometricAuthentication--->$isBiometricAuthentication")
        return isBiometricAuthentication
    }

    fun logoutFromDB() = viewModelScope.launch(Dispatchers.Main) {
        EntityID = "0"
        withContext(Dispatchers.IO) {
            startupManagementUseCase.invokeLogout()
        }
    }

    fun setReferralDetails(referralName: String, referralPID: String) {
        preferenceUtils.storePreference(CleverTapConstants.REFERRAL_NAME, referralName)
        preferenceUtils.storePreference(CleverTapConstants.REFERRAL_PID, referralPID)
        preferenceUtils.storeBooleanPreference(
            PreferenceConstants.IS_REFERRAL_DETAILS_AVAILABLE, true
        )
    }
}