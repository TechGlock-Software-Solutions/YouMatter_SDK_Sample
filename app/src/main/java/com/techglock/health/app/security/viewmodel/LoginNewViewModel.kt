package com.techglock.health.app.security.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.Event
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.PreferenceUtils
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.model.entity.Users
import com.techglock.health.app.model.home.AddFeatureAccessLog
import com.techglock.health.app.model.security.EmailExistsModel
import com.techglock.health.app.model.security.GenerateOtpModel
import com.techglock.health.app.model.security.PhoneExistsModel
import com.techglock.health.app.repository.utils.Resource
import com.techglock.health.app.security.domain.UserManagementUseCase
import com.techglock.health.app.security.ui.LoginFragment
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class LoginNewViewModel @Inject constructor(
    application: Application,
    private val userManagementUseCase: UserManagementUseCase,
    private val preferenceUtils: PreferenceUtils,
    val context: Context?
) : BaseViewModel(application) {

    val localResource =
        LocaleHelper.getLocalizedResources(context!!, Locale(LocaleHelper.getLanguage(context)))!!
    private lateinit var argsLogin: String

    private var isEmailExistSource: LiveData<Resource<EmailExistsModel.IsExistResponse>> =
        MutableLiveData()
    private val _isEmail = MediatorLiveData<Resource<EmailExistsModel.IsExistResponse>>()
    val isEmail: LiveData<Resource<EmailExistsModel.IsExistResponse>> get() = _isEmail

    private var isEmailExistGoogleSource: LiveData<Resource<EmailExistsModel.IsExistResponse>> =
        MutableLiveData()
    private val _isEmailGoogle = MediatorLiveData<Resource<EmailExistsModel.IsExistResponse>>()
    val isEmailGoogle: LiveData<Resource<EmailExistsModel.IsExistResponse>> get() = _isEmailGoogle

    private var phoneSource: LiveData<Resource<PhoneExistsModel.IsExistResponse>> =
        MutableLiveData()
    private val _isPhone = MediatorLiveData<Resource<PhoneExistsModel.IsExistResponse>>()
    val isPhone: LiveData<Resource<PhoneExistsModel.IsExistResponse>> get() = _isPhone

    private var otpGenerateSource: LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> =
        MutableLiveData()
    private val _otpGenerateData =
        MediatorLiveData<Resource<GenerateOtpModel.GenerateOTPResponse>>()
    val otpGenerateData: LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> get() = _otpGenerateData

    private var otpValidateSource: LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> =
        MutableLiveData()
    private val _otpValidateData =
        MediatorLiveData<Resource<GenerateOtpModel.GenerateOTPResponse>>()
    val otpValidateData: LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> get() = _otpValidateData

    private var loginUserSource: LiveData<Resource<Users>> = MutableLiveData()
    private val _isLogin = MediatorLiveData<Users>()
    val isLogin: LiveData<Users> get() = _isLogin

    private var addFeatureAccessLogSource: LiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>> =
        MutableLiveData()
    private val _addFeatureAccessLog =
        MediatorLiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>>()
    val addFeatureAccessLog: LiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>> get() = _addFeatureAccessLog

    /*    fun checkEmailExistOrNot(email: String,fragment:LoginFragmentNew) = viewModelScope.launch(Dispatchers.Main) {

            val requestData = EmailExistsModel(Gson().toJson(EmailExistsModel.JSONDataRequest(
                emailAddress = email),EmailExistsModel.JSONDataRequest::class.java))

            _progressBar.value = Event("Validating Email..")
            _isEmail.removeSource(isEmailExistSource)
            withContext(Dispatchers.IO) {
                isEmailExistSource = userManagementUseCase.invokeEmailExist(true, requestData)
            }
            _isEmail.addSource(isEmailExistSource) {
                _isEmail.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    fragment.isExist = it.data?.isExist.equals(Constants.TRUE, true)
                    fragment.loginType = Constants.EMAIL
                    //fragment.email = fragment.binding.edtLoginEmailPhone.text.toString().trim()
                    fragment.email = email
                    if (fragment.isExist) {
                        fragment.phone = it.data?.account!!.primaryPhone!!
                        fragment.viewModel.callGenerateVerificationCode(
                            email = it.data?.account!!.emailAddress!!,
                            phone = it.data?.account!!.primaryPhone!!,fragment)
                    } else {
                        fragment.phone = ""
                        fragment.showNotRegisteredDialog(Constants.EMAIL,email)
                    }
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        }*/

    fun checkEmailExistOrNotGoogle(
        name: String = "", email: String, fragment: LoginFragment
    ) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = EmailExistsModel(
            Gson().toJson(
                EmailExistsModel.JSONDataRequest(
                    emailAddress = email
                ), EmailExistsModel.JSONDataRequest::class.java
            )
        )

        _progressBar.value = Event("Validating Email..")
        _isEmailGoogle.removeSource(isEmailExistGoogleSource)
        withContext(Dispatchers.IO) {
            isEmailExistGoogleSource = userManagementUseCase.invokeEmailExist(true, requestData)
        }
        _isEmailGoogle.addSource(isEmailExistGoogleSource) {
            _isEmailGoogle.value = it

            when (it.status) {
                Resource.Status.SUCCESS -> {
                    //_progressBar.value = Event(Event.HIDE_PROGRESS)
                    it?.data?.let { data ->
                        fragment.isExist = data.isExist.equals(Constants.TRUE, true)
                        fragment.loginType = Constants.EMAIL
                        if (fragment.isExist) {
                            fragment.viewModel.callLogin(
                                emailStr = data.account!!.emailAddress!!,
                                passwordStr = "",
                                socialLogin = true,
                                source = Constants.GOOGLE_SOURCE,
                                view = fragment.binding.btnGetOtp
                            )
                        } else {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            fragment.phone = ""
                            fragment.showNotRegisteredDialog(Constants.EMAIL, email)
                        }
                    } ?: run {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                    }


                    /*                if (it.data?.isExist.equals(Constants.TRUE, true)) {
                            callLogin(
                                name = name,
                                emailStr = email,
                                passwordStr = "",
                                socialLogin = true,
                                source = Constants.GOOGLE_SOURCE,
                                view = view)
                        } else {
                            toastMessage(localResource.getString(R.string.ERROR_EMAIL_NOT_REGISTERED))
                        }*/
                }

                else -> {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun checkPhoneExistAPI(phoneNumber: String = "", fragment: LoginFragment) =
        viewModelScope.launch(Dispatchers.Main) {

            _progressBar.value = Event("")
            val requestData = PhoneExistsModel(
                Gson().toJson(
                    PhoneExistsModel.JSONDataRequest(
                        primaryPhone = phoneNumber
                    ), PhoneExistsModel.JSONDataRequest::class.java
                )
            )

            _isPhone.removeSource(phoneSource)
            withContext(Dispatchers.IO) {
                phoneSource = userManagementUseCase.invokePhoneExist(true, requestData)
            }
            _isPhone.addSource(phoneSource) {
                _isPhone.value = it

                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        //_progressBar.value = Event(Event.HIDE_PROGRESS)
                        it?.data?.let { data ->
                            fragment.isExist = data.isExist.equals(Constants.TRUE, true)
                            fragment.loginType = Constants.PHONE
                            //fragment.phone = fragment.binding.edtLoginEmailPhone.text.toString().trim()
                            fragment.phone = phoneNumber
                            if (fragment.isExist) {
                                fragment.email = data.account!!.emailAddress!!
                                fragment.viewModel.callGenerateVerificationCode(
                                    "", phone = data.account!!.primaryPhone!!, fragment
                                )
                            } else {
                                _progressBar.value = Event(Event.HIDE_PROGRESS)
                                fragment.showNotRegisteredDialog(Constants.PHONE, phoneNumber)
                            }

                        } ?: run {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)

                        }
                        /*                if (it.data?.isExist.equals(Constants.TRUE, true)) {
                            callGenerateVerificationCode(email = it.data?.account!!.emailAddress!!, phone = phoneNumber)
                        } else {
                            callGenerateVerificationCode(email = "", phone = phoneNumber)
                            toastMessage(localResource.getString(R.string.ERROR_PHONE_NOT_REGISTERED))
                        }*/
                    }

                    else -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        toastMessage(it.errorMessage)
                    }
                }
            }
        }

    fun callGenerateVerificationCode(
        email: String, phone: String = "", fragment: LoginFragment
    ) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = GenerateOtpModel(
            Gson().toJson(
                GenerateOtpModel.JSONDataRequest(
                    GenerateOtpModel.UPN(
                        loginName = email, emailAddress = email, primaryPhone = phone
                    ), message = "Generating OTP"
                ), GenerateOtpModel.JSONDataRequest::class.java
            )
        )

        //_progressBar.value = Event("Generating OTP")
        _otpGenerateData.removeSource(otpGenerateSource)
        withContext(Dispatchers.IO) {
            otpGenerateSource = userManagementUseCase.invokeGenerateOTP(true, requestData)
        }
        _otpGenerateData.addSource(otpGenerateSource) {
            _otpGenerateData.value = it

            when (it.status) {
                Resource.Status.SUCCESS -> {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data!!.status.equals(Constants.SUCCESS, ignoreCase = true)) {
                        //toastMessage(localResource.getString(R.string.MSG_VERIFICATION_CODE_SENT))
                        if (it.data!!.status.equals(Constants.SUCCESS, ignoreCase = true)) {
                            Utilities.printLogError("IsBottomSheetOpen--->${fragment.isBottomSheetOpen}")
                            if (!fragment.isBottomSheetOpen) {
                                fragment.showBottomSheet()
                            } else {
                                fragment.modalBottomSheetOTP!!.refreshTimer()
                            }
                            fragment.startSmsUserConsent()
                        }
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }

                else -> {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    //toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callValidateVerificationCode(
        otpReceived: String, email: String = "", phone: String = "", fragment: LoginFragment
    ) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = GenerateOtpModel(
            Gson().toJson(
                GenerateOtpModel.JSONDataRequest(
                    upn = GenerateOtpModel.UPN(primaryPhone = phone),
                    otp = otpReceived,
                    message = "Verifing Code..."
                ), GenerateOtpModel.JSONDataRequest::class.java
            )
        )

        _progressBar.value = Event("Verifing Code...")
        _otpValidateData.removeSource(otpValidateSource)
        withContext(Dispatchers.IO) {
            otpValidateSource = userManagementUseCase.invokeValidateOTP(true, requestData)
        }
        _otpValidateData.addSource(otpValidateSource) {
            _otpValidateData.value = it

            when (it.status) {
                Resource.Status.SUCCESS -> {
                    //_progressBar.value = Event(Event.HIDE_PROGRESS)
                    it?.data?.let { data ->
                        if (data.validity.equals(Constants.TRUE, true)) {
                            fragment.modalBottomSheetOTP!!.otpTimer.cancel()
                            fragment.modalBottomSheetOTP!!.dismiss()
                            fragment.loginOrProceedRegistration()
                        } else {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            Utilities.toastMessageShort(
                                context, localResource.getString(R.string.ERROR_INVALID_OTP)
                            )
                        }
                    } ?: run {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        Utilities.printLog("callValidateVerificationCode--->${it.data}")
                    }/*                    callLogin(
                                    forceRefresh = true,
                                    name = name,
                                    emailStr = emailStr,
                                    passwordStr = passwordStr,
                                    socialLogin = socialLogin,
                                    source = source,
                                    socialId = socialId,
                                    view = view)*//*                    callLogin(
                                    forceRefresh = true,
                                    emailStr = it.data!!.account!!.emailAddress.toString(),
                                    passwordStr = passwordStr,
                                    view = view)*/
                }

                else -> {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callLogin(
        name: String = "",
        emailStr: String,
        mobileStr: String = "",
        passwordStr: String = "",
        socialLogin: Boolean = false,
        source: String = Constants.LOGIN_SOURCE,
        view: View
    ) = viewModelScope.launch(Dispatchers.Main) {

        argsLogin = Utilities.getEncryptedData(
            email = emailStr,
            password = passwordStr,
            name = emailStr,
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
                        it?.data?.let { getData ->
                            Utilities.printLog("LoginResp--->$getData")

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
                            preferenceUtils.storePreference(
                                PreferenceConstants.ORG_NAME,
                                getData.orgName
                            )
                            preferenceUtils.storePreference(
                                PreferenceConstants.ORG_EMPLOYEE_ID,
                                getData.orgEmpID
                            )
                            val pid = getData.personId.toDouble().toInt()
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
                                getData.createdDate!!.split("T").toTypedArray()[0]
                            )
                            preferenceUtils.storePreference(
                                PreferenceConstants.DOB,
                                getData.dateOfBirth!!.split("T").toTypedArray()[0]
                            )
                            val bundle = Bundle()
                            when (source) {
                                Constants.LOGIN_SOURCE -> {
                                    bundle.putString(Constants.FROM, Constants.LOGIN_WITH_OTP)
                                    preferenceUtils.storeBooleanPreference(
                                        PreferenceConstants.IS_OTP_AUTHENTICATED,
                                        true
                                    )
                                    preferenceUtils.storePreference(
                                        PreferenceConstants.POLICY_MOBILE_NUMBER,
                                        getData.phoneNumber
                                    )
                                }

                                Constants.GOOGLE_SOURCE -> {
                                    bundle.putString(Constants.FROM, Constants.LOGIN)
                                }
                            }
                            /* if (!Utilities.isNullOrEmpty(getData.phoneNumber) && !Utilities.isNullOrEmpty(
                                     getData.dateOfBirth) && !Utilities.isNullOrEmpty(getData.gender)) {


                             }*/

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
                            CleverTapHelper.addUser(context)
                            when (source) {
                                Constants.LOGIN_SOURCE -> CleverTapHelper.pushEvent(
                                    context, CleverTapConstants.LOGIN_WITH_OTP
                                )

                                Constants.GOOGLE_SOURCE -> CleverTapHelper.pushEvent(
                                    context, CleverTapConstants.LOGIN_WITH_GOOGLE
                                )

                                Constants.FACEBOOK_SOURCE -> CleverTapHelper.pushEvent(
                                    context, CleverTapConstants.LOGIN_WITH_FACEBOOK
                                )
                            }
                            if (preferenceUtils.getBooleanPreference(PreferenceConstants.IS_REFERRAL_DETAILS_AVAILABLE)) {
                                val referralName =
                                    preferenceUtils.getPreference(CleverTapConstants.REFERRAL_NAME)
                                val referralPID =
                                    preferenceUtils.getPreference(CleverTapConstants.REFERRAL_PID)
                                val data = HashMap<String, Any>()
                                data[CleverTapConstants.REFERRAL_NAME] = referralName
                                data[CleverTapConstants.REFERRAL_PID] = referralPID
                                CleverTapHelper.pushEventWithProperties(
                                    context, CleverTapConstants.LOGIN_BY_REFERRAL, data
                                )
                                preferenceUtils.storeBooleanPreference(
                                    PreferenceConstants.IS_REFERRAL_DETAILS_AVAILABLE, false
                                )
                                //AddFeatureAccessLog
                                val desc =
                                    "ReferralName:$referralName|ReferralPID:$referralPID|PersonID:$pid"
                                callAddFeatureAccessLogApi(
                                    pid, Constants.LOGIN_BY_REFERRAL, desc
                                )
                            }
                            Navigation.findNavController(view)
                                .navigate(R.id.action_loginFragment_to_main_activity, bundle)


                        } ?: run {
                            Utilities.printLog("LoginResp--->${it.data}")
                        }


                        /*                    else {
                                Utilities.printLogError("Some User Details missing")
                                bundle.putString(Constants.UserConstants.SOURCE,source)
                                bundle.putString(Constants.NAME,name)
                                bundle.putString(Constants.EMAIL,emailStr)
                                bundle.putString(Constants.PHONE,getData.phoneNumber)
                                if ( !Utilities.isNullOrEmpty(getData.dateOfBirth) ) {
                                    bundle.putString(Constants.DATE_OF_BIRTH,getData.dateOfBirth!!.split("T").toTypedArray()[0])
                                } else {
                                    bundle.putString(Constants.DATE_OF_BIRTH,"")
                                }
                                bundle.putString(Constants.GENDER,getData.gender!!)
                                bundle.putString(Constants.PASSWORD,"")
                                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_userInfoFragment,bundle)
                            }*/

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
                            service = Constants.REFERRAL,
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
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.data != null) {
                            if (!Utilities.isNullOrEmptyOrZero(it.data!!.featureAccessLogID)) {
                                //toastMessage("Count Saved")
                            }
                        }
                    }

                    else -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.errorNumber.equals("1100014", true)) {
                            _sessionError.value = Event(true)
                        } else {
                            //toastMessage(it.errorMessage)
                        }
                    }
                }
            }

        }

    fun showProgress() {
        _progressBar.value = Event("")
    }

    fun hideProgress() {
        _progressBar.value = Event(Event.HIDE_PROGRESS)
    }
}