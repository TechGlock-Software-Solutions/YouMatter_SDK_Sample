package com.techglock.health.app.home.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Configuration
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Event
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.PreferenceUtils
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.home.common.DataHandler
import com.techglock.health.app.home.domain.HomeManagementUseCase
import com.techglock.health.app.model.home.LanguageModel
import com.techglock.health.app.model.home.PersonDeleteModel
import com.techglock.health.app.model.home.RefreshTokenModel
import com.techglock.health.app.model.home.UpdateLanguageProfileModel
import com.techglock.health.app.model.security.TermsConditionsModel
import com.techglock.health.app.repository.utils.Resource
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class SettingsViewModel @Inject constructor(
    application: Application,
    private val homeManagementUseCase: HomeManagementUseCase,
    private val dataHandler: DataHandler,
    private val preferenceUtils: PreferenceUtils,
    val context: Context?
) : BaseViewModel(application) {

    val localResource =
        LocaleHelper.getLocalizedResources(context!!, Locale(LocaleHelper.getLanguage(context)))!!
    var languageListLiveData = MutableLiveData<List<LanguageModel>>()
    val settingsOptionListData = MutableLiveData<List<DataHandler.Option>>()

    var personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
    var accountID = preferenceUtils.getPreference(PreferenceConstants.ACCOUNTID, "0")
    //var authToken = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")

    private var personDeleteSource: LiveData<Resource<PersonDeleteModel.PersonDeleteResponse>> =
        MutableLiveData()
    private val _personDelete = MediatorLiveData<Resource<PersonDeleteModel.PersonDeleteResponse>>()
    val personDelete: LiveData<Resource<PersonDeleteModel.PersonDeleteResponse>> get() = _personDelete

    var updateProfileSource: LiveData<Resource<UpdateLanguageProfileModel.UpdateLanguageProfileResponse>> =
        MutableLiveData()
    val _updateProfile =
        MediatorLiveData<Resource<UpdateLanguageProfileModel.UpdateLanguageProfileResponse>>()
    val updateProfile: LiveData<Resource<UpdateLanguageProfileModel.UpdateLanguageProfileResponse>> get() = _updateProfile

    var refreshTokenSource: LiveData<Resource<RefreshTokenModel.RefreshTokenResponse>> =
        MutableLiveData()
    val _refreshToken = MediatorLiveData<Resource<RefreshTokenModel.RefreshTokenResponse>>()
    val refreshToken: LiveData<Resource<RefreshTokenModel.RefreshTokenResponse>> get() = _refreshToken

    var userSourceTerms: LiveData<Resource<TermsConditionsModel.TermsConditionsResponse>> =
        MutableLiveData()
    val _termsConditions =
        MediatorLiveData<Resource<TermsConditionsModel.TermsConditionsResponse>>()
    val termsConditions: LiveData<Resource<TermsConditionsModel.TermsConditionsResponse>> get() = _termsConditions

    fun callPersonDeleteApi() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = PersonDeleteModel(
            Gson().toJson(
                PersonDeleteModel.JSONDataRequest(
                    reason = "Something else",
                    requestedDateTime = DateHelper.currentDateAsStringyyyyMMdd + "T" + DateHelper.currentTimeAs_hh_mm_ss,
                    accountID = preferenceUtils.getPreference(PreferenceConstants.ACCOUNTID, "0")
                        .toInt()
                ),
                PersonDeleteModel.JSONDataRequest::class.java
            ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _progressBar.value = Event("")
        _personDelete.removeSource(personDeleteSource)
        withContext(Dispatchers.IO) {
            personDeleteSource =
                homeManagementUseCase.invokePersonDelete(isForceRefresh = true, data = requestData)
        }
        _personDelete.addSource(personDeleteSource) {

            try {
                _personDelete.value = it
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                    }

                    Resource.Status.ERROR -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.errorNumber.equals("1100014", true)) {
                            _sessionError.value = Event(true)
                        } else {
                            toastMessage(it.errorMessage)
                        }
                    }

                    else -> {}
                }
            } catch (e: Exception) {
                Utilities.printException(e)
            }

        }

    }

    fun callUpdateProfileListApi(languageCode: String) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = UpdateLanguageProfileModel(
            Gson().toJson(
                UpdateLanguageProfileModel.JSONDataRequest(
                    details = UpdateLanguageProfileModel.Details(
                        personId =
                        personId, languageCode = languageCode
                    )
                ), UpdateLanguageProfileModel.JSONDataRequest::class.java
            ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _updateProfile.removeSource(updateProfileSource)
        _progressBar.value = Event("")
        withContext(Dispatchers.IO) {
            updateProfileSource =
                homeManagementUseCase.invokeUpdateLanguageSettings(
                    isForceRefresh = true,
                    data = requestData
                )
        }
        _updateProfile.addSource(updateProfileSource) {
            try {
                _updateProfile.value = it
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        Utilities.printLog("UpdateProfile----->$it")
                    }

                    Resource.Status.ERROR -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.errorNumber.equals("1100014", true)) {
                            _sessionError.value = Event(true)
                        } else {
                            toastMessage(it.errorMessage)
                        }
                    }

                    else -> {}
                }
            } catch (e: Exception) {
                Utilities.printException(e)
            }

        }
    }

    fun callRefreshTokenApi() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = RefreshTokenModel(
            Gson().toJson(
                RefreshTokenModel.JSONDataRequest(accountID = accountID, personID = personId),
                RefreshTokenModel.JSONDataRequest::class.java
            ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )
        _progressBar.value = Event("")
        _refreshToken.removeSource(refreshTokenSource)
        withContext(Dispatchers.IO) {
            refreshTokenSource = homeManagementUseCase.invokeRefreshTokenResponse(
                isForceRefresh = true,
                data = requestData
            )
        }
        _refreshToken.addSource(refreshTokenSource) {
            try {
                _refreshToken.value = it
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                    }

                    Resource.Status.ERROR -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.errorNumber.equals("1100014", true)) {
                            _sessionError.value = Event(true)
                        } else {
                            toastMessage(it.errorMessage)
                        }
                    }

                    else -> {}
                }
            } catch (e: Exception) {
                Utilities.printException(e)
            }

        }
    }

    fun getTermsAndConditionsData(forceRefresh: Boolean) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = TermsConditionsModel(
            Gson().toJson(
                TermsConditionsModel.JSONDataRequest(
                    applicationCode = Configuration.ApplicationCode,
                    partnerCode = Constants.PartnerCode
                ),
                TermsConditionsModel.JSONDataRequest::class.java
            )
        )

        _progressBar.value = Event("Loading")
        _termsConditions.removeSource(userSourceTerms)
        withContext(Dispatchers.IO) {
            userSourceTerms = homeManagementUseCase.invokeTermsCondition(
                isForceRefresh = forceRefresh,
                data = requestData
            )
        }
        _termsConditions.addSource(userSourceTerms) {
            try {
                _termsConditions.value = it
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                    }

                    Resource.Status.ERROR -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.errorNumber.equals("1100014", true)) {
                            _sessionError.value = Event(true)
                        } else {
                            toastMessage(it.errorMessage)
                        }
                    }

                    else -> {}
                }
            } catch (e: Exception) {
                Utilities.printException(e)
            }

        }
    }

    fun getLanguageList(context: Context): ArrayList<LanguageModel> {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<LanguageModel> = ArrayList()
        if (LocaleHelper.getLanguage(context) == Constants.LANGUAGE_CODE_ENGLISH) {
            list.add(
                LanguageModel(
                    localResource.getString(R.string.ENGLISH),
                    Constants.LANGUAGE_CODE_ENGLISH,
                    R.drawable.img_english,
                    R.color.color_english,
                    true
                )
            )
            list.add(
                LanguageModel(
                    localResource.getString(R.string.HINDI),
                    Constants.LANGUAGE_CODE_HINDI,
                    R.drawable.img_hindi,
                    R.color.color_hindi,
                    false
                )
            )
        } else {
            list.add(
                LanguageModel(
                    localResource.getString(R.string.ENGLISH),
                    Constants.LANGUAGE_CODE_ENGLISH,
                    R.drawable.img_english,
                    R.color.color_english,
                    false
                )
            )
            list.add(
                LanguageModel(
                    localResource.getString(R.string.HINDI),
                    Constants.LANGUAGE_CODE_HINDI,
                    R.drawable.img_hindi,
                    R.color.color_hindi,
                    true
                )
            )
        }
        return list
    }

    /*    fun getLanguageList() = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var list: ArrayList<DataHandler.LanguageModel> = arrayListOf()
                if(LocaleHelper.getLanguage(context).equals("en")) {
                    list.add(DataHandler.LanguageModel("English", "en",true))
                    list.add(DataHandler.LanguageModel("Vietnamese","vi",false))
                } else {
                    list.add(DataHandler.LanguageModel("English", "en",false))
                    list.add(DataHandler.LanguageModel("Vietnamese","vi",true))
                }
                languageListLiveData.postValue(list)
            }
        }*/

    /*    fun getSettingsOptionList() {
            settingsOptionListData.postValue( dataHandler.getSettingsOptionListData() )
        }*/

    fun getSettingsOptionList() {
        if (isSelfUser()) {
            settingsOptionListData.postValue(dataHandler.getSettingsOptionListData())
        } else {
            settingsOptionListData.postValue(dataHandler.getSwitchProfileSettingsOptionListData())
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

    fun isBiometricAuthentication(): Boolean {
        val isBiometricAuthentication = preferenceUtils.getBooleanPreference(
            PreferenceConstants.IS_BIOMETRIC_AUTHENTICATION,
            true
        )
        //Utilities.printLogError("IsBiometricAuthentication--->$isBiometricAuthentication")
        return isBiometricAuthentication
    }

    fun setBiometricAuthentication(flag: Boolean) {
        preferenceUtils.storeBooleanPreference(
            PreferenceConstants.IS_BIOMETRIC_AUTHENTICATION,
            flag
        )
    }

    fun isPushNotificationEnabled(): Boolean {
        return preferenceUtils.getBooleanPreference(
            PreferenceConstants.ENABLE_PUSH_NOTIFICATION,
            true
        )
    }

    fun setPushNotificationEnableOrDisable(isEnabled: Boolean) {
        preferenceUtils.storeBooleanPreference(
            PreferenceConstants.ENABLE_PUSH_NOTIFICATION,
            isEnabled
        )
    }

    fun updateUserPreference(unit: String?) {
        if (!unit.isNullOrEmpty()) {
            when (unit.lowercase()) {
                "cm" -> {
                    preferenceUtils.storePreference(PreferenceConstants.HEIGHT_PREFERENCE, "cm")
                }

                "kg" -> {
                    preferenceUtils.storePreference(PreferenceConstants.WEIGHT_PREFERENCE, "kg")
                }

                "lbs" -> {
                    preferenceUtils.storePreference(PreferenceConstants.WEIGHT_PREFERENCE, "lib")
                }

                "feet/inch" -> {
                    preferenceUtils.storePreference(PreferenceConstants.HEIGHT_PREFERENCE, "feet")
                }

                "inch" -> {
                    preferenceUtils.storePreference(PreferenceConstants.HEIGHT_PREFERENCE, "feet")
                }
            }
        }
    }


    fun getHeightPreference(): String {
        return preferenceUtils.getPreference(PreferenceConstants.HEIGHT_PREFERENCE, "feet")
    }

    fun getWeightPreference(): String {
        return preferenceUtils.getPreference(PreferenceConstants.WEIGHT_PREFERENCE, "kg")
    }

    fun refreshToken(token: String) {
        preferenceUtils.storePreference(PreferenceConstants.TOKEN, token)
        //authToken = token
    }

    fun navigateToSettingsActivity() {

    }

    fun navigateToHomeScreen() {

    }


}