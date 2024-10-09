package com.techglock.health.app.home.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.utils.Event
import com.techglock.health.app.common.utils.PreferenceUtils
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.home.common.WellfieSingleton
import com.techglock.health.app.home.domain.AktivoManagementUseCase
import com.techglock.health.app.model.aktivo.AktivoCheckUserModel
import com.techglock.health.app.model.aktivo.AktivoCreateUserModel
import com.techglock.health.app.model.aktivo.AktivoGetRefreshTokenModel
import com.techglock.health.app.model.aktivo.AktivoGetUserModel
import com.techglock.health.app.model.aktivo.AktivoGetUserTokenModel
import com.techglock.health.app.repository.utils.Resource
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class AktivoViewModel @Inject constructor(
    application: Application,
    private val aktivoManagementUseCase: AktivoManagementUseCase,
    private val preferenceUtils: PreferenceUtils,
    val context: Context?
) : BaseViewModel(application) {

    private val wellfieSingleton = WellfieSingleton.getInstance()!!

    private var aktivoCreateUserSource: LiveData<Resource<AktivoCreateUserModel.AktivoCreateUserResponse>> =
        MutableLiveData()
    private val _aktivoCreateUser =
        MediatorLiveData<Resource<AktivoCreateUserModel.AktivoCreateUserResponse>>()
    val aktivoCreateUser: LiveData<Resource<AktivoCreateUserModel.AktivoCreateUserResponse>> get() = _aktivoCreateUser

    private var aktivoGetUserTokenSource: LiveData<Resource<AktivoGetUserTokenModel.AktivoGetUserTokenResponse>> =
        MutableLiveData()
    private val _aktivoGetUserToken =
        MediatorLiveData<Resource<AktivoGetUserTokenModel.AktivoGetUserTokenResponse>>()
    val aktivoGetUserToken: LiveData<Resource<AktivoGetUserTokenModel.AktivoGetUserTokenResponse>> get() = _aktivoGetUserToken

    private var aktivoGetRefreshTokenSource: LiveData<Resource<AktivoGetRefreshTokenModel.AktivoGetRefreshTokenResponse>> =
        MutableLiveData()
    private val _aktivoGetRefreshToken =
        MediatorLiveData<Resource<AktivoGetRefreshTokenModel.AktivoGetRefreshTokenResponse>>()
    val aktivoGetRefreshToken: LiveData<Resource<AktivoGetRefreshTokenModel.AktivoGetRefreshTokenResponse>> get() = _aktivoGetRefreshToken

    private var aktivoGetUserSource: LiveData<Resource<AktivoGetUserModel.AktivoGetUserResponse>> =
        MutableLiveData()
    private val _aktivoGetUser =
        MediatorLiveData<Resource<AktivoGetUserModel.AktivoGetUserResponse>>()
    val aktivoGetUser: LiveData<Resource<AktivoGetUserModel.AktivoGetUserResponse>> get() = _aktivoGetUser

    private var aktivoCheckUserSource: LiveData<Resource<AktivoCheckUserModel.AktivoCheckUserResponse>> =
        MutableLiveData()
    private val _aktivoCheckUser =
        MediatorLiveData<Resource<AktivoCheckUserModel.AktivoCheckUserResponse>>()
    val aktivoCheckUser: LiveData<Resource<AktivoCheckUserModel.AktivoCheckUserResponse>> get() = _aktivoCheckUser

    fun callAktivoCreateUserApi(deviceToken: String) = viewModelScope.launch(Dispatchers.Main) {

        val objMember = JsonObject()
        objMember.addProperty(
            "externalId", preferenceUtils.getPreference(PreferenceConstants.PERSONID, "")
        )
        objMember.addProperty(
            "nickname", preferenceUtils.getPreference(PreferenceConstants.FIRSTNAME, "")
        )
        objMember.addProperty(
            "date_of_birth", preferenceUtils.getPreference(PreferenceConstants.DOB, "")
        )
        objMember.addProperty(
            "sex", Utilities.getDisplayGender(
                preferenceUtils.getPreference(
                    PreferenceConstants.GENDER, ""
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

        _progressBar.value = Event("")
        _aktivoCreateUser.removeSource(aktivoCreateUserSource)
        withContext(Dispatchers.IO) {
            aktivoCreateUserSource =
                aktivoManagementUseCase.invokeAktivoCreateUser(data = requestData)
        }
        _aktivoCreateUser.addSource(aktivoCreateUserSource) {
            try {
                _aktivoCreateUser.value = it
                it.data?.let { data ->
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            Utilities.printLog("Response--->$data")
                        }

                        Resource.Status.ERROR -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                        }

                        else -> {}
                    }
                }
            } catch (e: Exception) {
                Utilities.printException(e)
            }
        }

    }

    fun callAktivoGetUserTokenApi(aktivoMemberId: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val obj = JsonObject()
            obj.addProperty("member_id", aktivoMemberId)
            val requestData = AktivoGetUserTokenModel(
                obj, preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            _progressBar.value = Event("")
            _aktivoGetUserToken.removeSource(aktivoGetUserTokenSource)
            withContext(Dispatchers.IO) {
                aktivoGetUserTokenSource =
                    aktivoManagementUseCase.invokeAktivoGetUserToken(data = requestData)
            }
            _aktivoGetUserToken.addSource(aktivoGetUserTokenSource) {
                try {
                    _aktivoGetUserToken.value = it
                    it.data?.let { data ->
                        when (it.status) {
                            Resource.Status.SUCCESS -> {
                                _progressBar.value = Event(Event.HIDE_PROGRESS)
                                Utilities.printLog("Response--->" + data)
                            }

                            Resource.Status.ERROR -> {
                                _progressBar.value = Event(Event.HIDE_PROGRESS)
                            }

                            else -> {}
                        }
                    }

                } catch (e: Exception) {
                    Utilities.printException(e)
                }

            }

        }

    fun callAktivoGetRefreshTokenApi(refreshToken: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val obj = JsonObject()
            obj.addProperty("refresh_token", refreshToken)
            obj.addProperty("grant_type", "refresh_token")
            val requestData = AktivoGetRefreshTokenModel(
                obj, preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            _progressBar.value = Event("")
            _aktivoGetRefreshToken.removeSource(aktivoGetRefreshTokenSource)
            withContext(Dispatchers.IO) {
                aktivoGetRefreshTokenSource =
                    aktivoManagementUseCase.invokeAktivoGetRefreshToken(data = requestData)
            }
            _aktivoGetRefreshToken.addSource(aktivoGetRefreshTokenSource) {
                try {
                    _aktivoGetRefreshToken.value = it
                    it.data?.let { data ->
                        when (it.status) {
                            Resource.Status.SUCCESS -> {
                                _progressBar.value = Event(Event.HIDE_PROGRESS)
                                Utilities.printLog("Response--->$data")
                            }

                            Resource.Status.ERROR -> {
                                _progressBar.value = Event(Event.HIDE_PROGRESS)
                                toastMessage(it.errorMessage)
                            }

                            else -> {}
                        }
                    }
                } catch (e: Exception) {
                    Utilities.printException(e)
                }
            }

        }

    fun callAktivoGetUserApi(aktivoMemberId: String) = viewModelScope.launch(Dispatchers.Main) {

        val obj = JsonObject()
        obj.addProperty("member_id", aktivoMemberId)
        val requestData =
            AktivoGetUserModel(obj, preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

        _progressBar.value = Event("")
        _aktivoGetUser.removeSource(aktivoGetUserSource)
        withContext(Dispatchers.IO) {
            aktivoGetUserSource = aktivoManagementUseCase.invokeAktivoGetUser(data = requestData)
        }
        _aktivoGetUser.addSource(aktivoGetUserSource) {
            try {
                _aktivoGetUser.value = it
                it.data?.let { data ->
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            Utilities.printLog("Response--->$data")
                        }

                        Resource.Status.ERROR -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            toastMessage(it.errorMessage)
                        }

                        else -> {}
                    }
                }
            } catch (e: Exception) {
                Utilities.printException(e)
            }


        }

    }

    fun callAktivoCheckUserApi() = viewModelScope.launch(Dispatchers.Main) {

        val obj = JsonObject()
        obj.addProperty(
            "externalId", preferenceUtils.getPreference(PreferenceConstants.PERSONID, "")
        )
        val requestData =
            AktivoCheckUserModel(obj, preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

        _progressBar.value = Event("")
        _aktivoCheckUser.removeSource(aktivoCheckUserSource)
        withContext(Dispatchers.IO) {
            aktivoCheckUserSource =
                aktivoManagementUseCase.invokeAktivoCheckUser(data = requestData)
        }
        _aktivoCheckUser.addSource(aktivoCheckUserSource) {
            try {
                _aktivoCheckUser.value = it

                it.data?.let { data ->
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            Utilities.printLog("Response--->$data")
                        }

                        Resource.Status.ERROR -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            toastMessage(it.errorMessage)
                        }

                        else -> {}
                    }
                }
            } catch (e: Exception) {
                Utilities.printException(e)
            }
        }

    }

    fun storeUserPreference(key: String, value: String) {
        Utilities.printLogError("Storing $key--->$value")
        preferenceUtils.storePreference(key, value)
    }

    fun getUserPreference(key: String): String {
        val userPreference = preferenceUtils.getPreference(key)
        Utilities.printLogError("$key--->$userPreference")
        return userPreference
    }

    fun showProgress() {
        _progressBar.value = Event("")
    }

    fun hideProgress() {
        _progressBar.value = Event(Event.HIDE_PROGRESS)
    }

}