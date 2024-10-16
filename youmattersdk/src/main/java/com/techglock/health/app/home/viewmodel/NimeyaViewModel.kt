package com.techglock.health.app.home.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Event
import com.techglock.health.app.common.utils.PreferenceUtils
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.home.common.NimeyaSingleton
import com.techglock.health.app.home.domain.HomeManagementUseCase
import com.techglock.health.app.home.ui.HomeScreenFragment
import com.techglock.health.app.model.home.NimeyaModel
import com.techglock.health.app.model.nimeya.GetProtectoMeterHistoryModel
import com.techglock.health.app.model.nimeya.GetRetiroMeterHistoryModel
import com.techglock.health.app.model.nimeya.GetRiskoMeterHistoryModel
import com.techglock.health.app.model.nimeya.GetRiskoMeterModel
import com.techglock.health.app.model.nimeya.SaveProtectoMeterModel
import com.techglock.health.app.model.nimeya.SaveRetiroMeterModel
import com.techglock.health.app.model.nimeya.SaveRiskoMeterModel
import com.techglock.health.app.repository.utils.Resource
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class NimeyaViewModel @Inject constructor(
    application: Application,
    private val homeManagementUseCase: HomeManagementUseCase,
    private val preferenceUtils: PreferenceUtils,
    val context: Context?
) : BaseViewModel(application) {

    var personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
    var accountID = preferenceUtils.getPreference(PreferenceConstants.ACCOUNTID, "0")
    val dob = preferenceUtils.getPreference(PreferenceConstants.DOB, "")

    private var getNimeyaUrlSource: LiveData<Resource<NimeyaModel.NimeyaResponse>> =
        MutableLiveData()
    private val _getNimeyaUrl = MediatorLiveData<Resource<NimeyaModel.NimeyaResponse>>()
    val getNimeyaUrl: LiveData<Resource<NimeyaModel.NimeyaResponse>> get() = _getNimeyaUrl

    private var getRiskoMeterSource: LiveData<Resource<GetRiskoMeterModel.RiskoMeterQuesResponse>> =
        MutableLiveData()
    private val _getRiskoMeter =
        MediatorLiveData<Resource<GetRiskoMeterModel.RiskoMeterQuesResponse>>()
    val getRiskoMeter: LiveData<Resource<GetRiskoMeterModel.RiskoMeterQuesResponse>> get() = _getRiskoMeter

    private var saveRiskoMeterSource: LiveData<Resource<SaveRiskoMeterModel.RiskoMeterSaveResponse>> =
        MutableLiveData()
    private val _saveRiskoMeter =
        MediatorLiveData<Resource<SaveRiskoMeterModel.RiskoMeterSaveResponse>>()
    val saveRiskoMeter: LiveData<Resource<SaveRiskoMeterModel.RiskoMeterSaveResponse>> get() = _saveRiskoMeter

    private var saveProtectoMeterSource: LiveData<Resource<SaveProtectoMeterModel.ProtectoMeterSaveResponse>> =
        MutableLiveData()
    private val _saveProtectoMeter =
        MediatorLiveData<Resource<SaveProtectoMeterModel.ProtectoMeterSaveResponse>>()
    val saveProtectoMeter: LiveData<Resource<SaveProtectoMeterModel.ProtectoMeterSaveResponse>> get() = _saveProtectoMeter

    private var saveRetiroMeterSource: LiveData<Resource<SaveRetiroMeterModel.SaveRetiroMeterResponse>> =
        MutableLiveData()
    private val _saveRetiroMeter =
        MediatorLiveData<Resource<SaveRetiroMeterModel.SaveRetiroMeterResponse>>()
    val saveRetiroMeter: LiveData<Resource<SaveRetiroMeterModel.SaveRetiroMeterResponse>> get() = _saveRetiroMeter

    private var riskoMeterHistorySource: LiveData<Resource<GetRiskoMeterHistoryModel.RiskoMeterHistoryResponse>> =
        MutableLiveData()
    private val _riskoMeterHistory =
        MediatorLiveData<Resource<GetRiskoMeterHistoryModel.RiskoMeterHistoryResponse>>()
    val riskoMeterHistory: LiveData<Resource<GetRiskoMeterHistoryModel.RiskoMeterHistoryResponse>> get() = _riskoMeterHistory

    private var protectoMeterHistorySource: LiveData<Resource<GetProtectoMeterHistoryModel.ProtectoMeterHistoryResponse>> =
        MutableLiveData()
    private val _protectoMeterHistory =
        MediatorLiveData<Resource<GetProtectoMeterHistoryModel.ProtectoMeterHistoryResponse>>()
    val protectoMeterHistory: LiveData<Resource<GetProtectoMeterHistoryModel.ProtectoMeterHistoryResponse>> get() = _protectoMeterHistory

    private var retiroMeterHistorySource: LiveData<Resource<GetRetiroMeterHistoryModel.RetiroMeterHistoryResponse>> =
        MutableLiveData()
    private val _retiroMeterHistory =
        MediatorLiveData<Resource<GetRetiroMeterHistoryModel.RetiroMeterHistoryResponse>>()
    val retiroMeterHistory: LiveData<Resource<GetRetiroMeterHistoryModel.RetiroMeterHistoryResponse>> get() = _retiroMeterHistory

    fun callGetNimeyaUrlApi() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = NimeyaModel(
            Gson().toJson(
                NimeyaModel.JSONDataRequest(
                    accountID = preferenceUtils.getPreference(PreferenceConstants.ACCOUNTID, "0")
                        .toInt(),
                    personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                        .toInt()
                ), NimeyaModel.JSONDataRequest::class.java
            ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        //_progressBar.value = Event("")
        _getNimeyaUrl.removeSource(getNimeyaUrlSource)
        withContext(Dispatchers.IO) {
            getNimeyaUrlSource =
                homeManagementUseCase.invokeGetNimeyaUrl(isForceRefresh = true, data = requestData)
        }
        _getNimeyaUrl.addSource(getNimeyaUrlSource) {
            _getNimeyaUrl.value = it

            if (it.status == Resource.Status.SUCCESS) {
                //_progressBar.value = Event(Event.HIDE_PROGRESS)
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

    fun callgetRiskoMeterApi() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = GetRiskoMeterModel(
            Gson().toJson(
                GetRiskoMeterModel.JSONDataRequest(
                    personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                        .toInt(),
                    accountID = preferenceUtils.getPreference(PreferenceConstants.ACCOUNTID, "0")
                        .toInt(),
                ), GetRiskoMeterModel.JSONDataRequest::class.java
            ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _progressBar.value = Event("")
        _getRiskoMeter.removeSource(getRiskoMeterSource)
        withContext(Dispatchers.IO) {
            getRiskoMeterSource =
                homeManagementUseCase.invokeGetRiskoMeter(isForceRefresh = true, data = requestData)
        }
        _getRiskoMeter.addSource(getRiskoMeterSource) {
            _getRiskoMeter.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
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

    fun callSaveRiskoMeterApi(view: View, quesAnsList: MutableList<GetRiskoMeterModel.Data>) =
        viewModelScope.launch(Dispatchers.Main) {
            val finalList: MutableList<SaveRiskoMeterModel.Request> = mutableListOf()
            for (i in quesAnsList) {
                var ansId = ""
                for (j in i.answers) {
                    if (j.isSelected) {
                        ansId = j.id!!
                    }
                }
                finalList.add(SaveRiskoMeterModel.Request(i.id, ansId))
            }
            val requestData = SaveRiskoMeterModel(
                Gson().toJson(
                    SaveRiskoMeterModel.JSONDataRequest(
                        personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                            .toInt(),
                        accountID = preferenceUtils.getPreference(
                            PreferenceConstants.ACCOUNTID,
                            "0"
                        ).toInt(),
                        dateTime = DateHelper.currentDateAsStringyyyyMMdd + "T" + DateHelper.currentTimeAs_hh_mm_ss,
                        request = finalList
                    ), SaveRiskoMeterModel.JSONDataRequest::class.java
                ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            //Utilities.printData("RequestData",requestData,true)
            _progressBar.value = Event("")
            _saveRiskoMeter.removeSource(saveRiskoMeterSource)
            withContext(Dispatchers.IO) {
                saveRiskoMeterSource = homeManagementUseCase.invokeSaveRiskoMeter(
                    isForceRefresh = true,
                    data = requestData
                )
            }
            _saveRiskoMeter.addSource(saveRiskoMeterSource) {
                _saveRiskoMeter.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    val riskoMeter = it.data!!.saveRiskoMeter
                    if (riskoMeter.status.equals("success")) {
                        val data = HashMap<String, Any>()
                        data[CleverTapConstants.FROM] = CleverTapConstants.SUBMIT
                        CleverTapHelper.pushEventWithProperties(
                            context,
                            CleverTapConstants.YM_NIMEYA_RISKO_METER_RESULT,
                            data
                        )
                        NimeyaSingleton.getInstance()!!.saveRiskoMeter = riskoMeter
                        view.findNavController()
                            .navigate(R.id.action_riskoMeterInputFragment_to_riskoMeterResultFragment)
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

    fun callSaveProtectoMeterApi(view: View, request: SaveProtectoMeterModel.JSONDataRequest) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = SaveProtectoMeterModel(
                Gson().toJson(
                    request, SaveProtectoMeterModel.JSONDataRequest::class.java
                ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            //Utilities.printData("requestData",requestData,true)
            _progressBar.value = Event("")
            _saveProtectoMeter.removeSource(saveProtectoMeterSource)
            withContext(Dispatchers.IO) {
                saveProtectoMeterSource = homeManagementUseCase.invokeSaveProtectooMeter(
                    isForceRefresh = true,
                    data = requestData
                )
            }
            _saveProtectoMeter.addSource(saveProtectoMeterSource) {
                _saveProtectoMeter.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    val saveProtectoMeter = it.data!!.saveProtectoMeter
                    if (saveProtectoMeter.status.equals("success")) {
                        val data = HashMap<String, Any>()
                        data[CleverTapConstants.FROM] = CleverTapConstants.SUBMIT
                        CleverTapHelper.pushEventWithProperties(
                            context,
                            CleverTapConstants.YM_NIMEYA_PROTECTO_METER_RESULT,
                            data
                        )
                        val abc = NimeyaSingleton.getInstance()!!
                        abc.saveProtectoMeter = saveProtectoMeter
                        abc.protectoMeterHistory.familyDetails.clear()
                        for (i in saveProtectoMeter.data.familyDetails) {
                            abc.protectoMeterHistory.familyDetails.add(
                                GetProtectoMeterHistoryModel.FamilyDetail(
                                    name = i.name,
                                    relation = i.relation,
                                    dob = i.dob,
                                    age = i.age,
                                    isDependent = i.isDependent,
                                    monthlyExpense = i.monthlyExpense
                                )
                            )
                        }
                        view.findNavController()
                            .navigate(R.id.action_protectoMeterInputFragment_to_protectoMeterResultFragment)
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

    fun callSaveRetiroMeterApi(view: View, request: SaveRetiroMeterModel.JSONDataRequest) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = SaveRetiroMeterModel(
                Gson().toJson(
                    request, SaveRetiroMeterModel.JSONDataRequest::class.java
                ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            //Utilities.printData("requestData",requestData,true)
            _progressBar.value = Event("")
            _saveRetiroMeter.removeSource(saveRetiroMeterSource)
            withContext(Dispatchers.IO) {
                saveRetiroMeterSource = homeManagementUseCase.invokeSaveRetiroMeterApi(
                    isForceRefresh = true,
                    data = requestData
                )
            }
            _saveRetiroMeter.addSource(saveRetiroMeterSource) {
                _saveRetiroMeter.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    val saveRetiroMeter = it.data!!.saveRetiroMeter
                    if (saveRetiroMeter.status.equals("success")) {
                        val data = HashMap<String, Any>()
                        data[CleverTapConstants.FROM] = CleverTapConstants.SUBMIT
                        CleverTapHelper.pushEventWithProperties(
                            context,
                            CleverTapConstants.YM_NIMEYA_RETIRO_METER_RESULT,
                            data
                        )
                        NimeyaSingleton.getInstance()!!.saveRetiroMeter = saveRetiroMeter
                        view.findNavController()
                            .navigate(R.id.action_retiroMeterInputFragment_to_retiroMeterResultFragment)
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

    fun callGetRiskoMeterHistoryApi(fragment: HomeScreenFragment) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = GetRiskoMeterHistoryModel(
                Gson().toJson(
                    GetRiskoMeterHistoryModel.JSONDataRequest(
                        personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                            .toInt(),
                        accountID = preferenceUtils.getPreference(
                            PreferenceConstants.ACCOUNTID,
                            "0"
                        ).toInt(),
                    ), GetRiskoMeterHistoryModel.JSONDataRequest::class.java
                ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            //Utilities.printData("requestData",requestData,true)
            //_progressBar.value = Event("")
            _riskoMeterHistory.removeSource(riskoMeterHistorySource)
            withContext(Dispatchers.IO) {
                riskoMeterHistorySource = homeManagementUseCase.invokeGetRiskoMeterHistory(
                    isForceRefresh = true,
                    data = requestData
                )
            }
            _riskoMeterHistory.addSource(riskoMeterHistorySource) {
                _riskoMeterHistory.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    NimeyaSingleton.getInstance()!!.riskoMeterHistory =
                        it.data!!.getRiskoMeterHistory
                    Utilities.printData(
                        "RiskoMeterHistory",
                        NimeyaSingleton.getInstance()!!.riskoMeterHistory,
                        true
                    )
                    if (!Utilities.isNullOrEmpty(it.data!!.getRiskoMeterHistory.riskMeter.toString())) {
                        val data = HashMap<String, Any>()
                        data[CleverTapConstants.FROM] = CleverTapConstants.VIEW_RESULT
                        CleverTapHelper.pushEventWithProperties(
                            context,
                            CleverTapConstants.YM_NIMEYA_RISKO_METER_RESULT,
                            data
                        )
                        fragment.gotoNimeyaActivity(Constants.NIMEYA_RISKO_METER_RESULT)
                    } else {
                        fragment.gotoNimeyaActivity(Constants.NIMEYA_RISKO_METER)
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

    fun callGetProtectoMeterHistoryApi(fragment: HomeScreenFragment) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = GetProtectoMeterHistoryModel(
                Gson().toJson(
                    GetProtectoMeterHistoryModel.JSONDataRequest(
                        personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                            .toInt(),
                        accountID = preferenceUtils.getPreference(
                            PreferenceConstants.ACCOUNTID,
                            "0"
                        ).toInt(),
                    ), GetProtectoMeterHistoryModel.JSONDataRequest::class.java
                ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            //Utilities.printData("requestData",requestData,true)
            //_progressBar.value = Event("")
            _protectoMeterHistory.removeSource(protectoMeterHistorySource)
            withContext(Dispatchers.IO) {
                protectoMeterHistorySource = homeManagementUseCase.invokeGetProtectoMeterHistory(
                    isForceRefresh = true,
                    data = requestData
                )
            }
            _protectoMeterHistory.addSource(protectoMeterHistorySource) {
                _protectoMeterHistory.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    NimeyaSingleton.getInstance()!!.protectoMeterHistory =
                        it.data!!.getProtectoMeterHistory
                    Utilities.printData(
                        "ProtectoMeterHistory",
                        NimeyaSingleton.getInstance()!!.protectoMeterHistory,
                        true
                    )
                    if (!Utilities.isNullOrEmpty(it.data!!.getProtectoMeterHistory.lifeInsuranceScoreText.toString())) {
                        val data = HashMap<String, Any>()
                        data[CleverTapConstants.FROM] = CleverTapConstants.VIEW_RESULT
                        CleverTapHelper.pushEventWithProperties(
                            context,
                            CleverTapConstants.YM_NIMEYA_PROTECTO_METER_RESULT,
                            data
                        )
                        fragment.gotoNimeyaActivity(Constants.NIMEYA_PROTECTO_METER_RESULT)
                    } else {
                        fragment.gotoNimeyaActivity(Constants.NIMEYA_PROTECTO_METER)
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

    fun callGetRetiroMeterHistoryApi(fragment: HomeScreenFragment) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = GetRetiroMeterHistoryModel(
                Gson().toJson(
                    GetRetiroMeterHistoryModel.JSONDataRequest(
                        personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                            .toInt(),
                        accountID = preferenceUtils.getPreference(
                            PreferenceConstants.ACCOUNTID,
                            "0"
                        ).toInt(),
                    ), GetRetiroMeterHistoryModel.JSONDataRequest::class.java
                ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            //Utilities.printData("requestData",requestData,true)
            //_progressBar.value = Event("")
            _retiroMeterHistory.removeSource(retiroMeterHistorySource)
            withContext(Dispatchers.IO) {
                retiroMeterHistorySource = homeManagementUseCase.invokeGetRetiroMeterHistory(
                    isForceRefresh = true,
                    data = requestData
                )
            }
            _retiroMeterHistory.addSource(retiroMeterHistorySource) {
                _retiroMeterHistory.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    NimeyaSingleton.getInstance()!!.retiroMeterHistory = it.data!!.data
                    Utilities.printData(
                        "RetiroMeterHistory",
                        NimeyaSingleton.getInstance()!!.retiroMeterHistory,
                        true
                    )
                    if (!Utilities.isNullOrEmptyOrZero(it.data!!.data.scoreText.toString())) {
                        val data = HashMap<String, Any>()
                        data[CleverTapConstants.FROM] = CleverTapConstants.VIEW_RESULT
                        CleverTapHelper.pushEventWithProperties(
                            context,
                            CleverTapConstants.YM_NIMEYA_RETIRO_METER_RESULT,
                            data
                        )
                        fragment.gotoNimeyaActivity(Constants.NIMEYA_RETIRO_METER_RESULT)
                    } else {
                        fragment.gotoNimeyaActivity(Constants.NIMEYA_RETIRO_METER)
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