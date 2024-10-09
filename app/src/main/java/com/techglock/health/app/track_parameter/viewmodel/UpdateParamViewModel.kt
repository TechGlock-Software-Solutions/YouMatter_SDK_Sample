package com.techglock.health.app.track_parameter.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Event
import com.techglock.health.app.common.utils.PreferenceUtils
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.model.parameter.ParameterListModel
import com.techglock.health.app.model.parameter.SaveParameterModel
import com.techglock.health.app.repository.utils.Resource
import com.techglock.health.app.track_parameter.domain.ParameterManagementUseCase
import com.techglock.health.app.track_parameter.util.TrackParameterHelper
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UpdateParamViewModel @Inject constructor(
    private val useCase: ParameterManagementUseCase,
    application: Application,
    private val preferenceUtils: PreferenceUtils
) : BaseViewModel(application) {

    val selectParamLiveData: MutableLiveData<List<ParameterListModel.SelectedParameter>> =
        MutableLiveData()
    val parameterLiveData: MutableLiveData<List<TrackParameterMaster.Parameter>> = MutableLiveData()

    var allParamList: List<TrackParameterMaster.Parameter> = listOf()

    var paramSelectedSource: List<ParameterListModel.SelectedParameter> = listOf()
    private val _paramSelectedList = MediatorLiveData<ParameterListModel.SelectedParameter>()
    val paramSelectedList: LiveData<ParameterListModel.SelectedParameter> get() = _paramSelectedList

    private val _selectedParam = MediatorLiveData<List<ParameterListModel.SelectedParameter>>()
    val selectedParameter: LiveData<List<ParameterListModel.SelectedParameter>> get() = _selectedParam

    private val _inputParamList = MediatorLiveData<List<ParameterListModel.InputParameterModel>>()
    val inputParamList: LiveData<List<ParameterListModel.InputParameterModel>> get() = _inputParamList
//    val inputParamListLiveData: MutableLiveData<List<ParameterListModel.InputParameterModel>> = MutableLiveData()

    var saveParamUserSource: LiveData<Resource<SaveParameterModel.Response>> = MutableLiveData()
    private val _saveParam = MediatorLiveData<SaveParameterModel.Response?>()
    val saveParam: LiveData<SaveParameterModel.Response?> get() = _saveParam

    fun getTrackParameters() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            if (allParamList.isEmpty()) {
                val data = useCase.invokeGetDBParamList()
                allParamList = data
                Utilities.printLog("Size: ${data.size}")
                Utilities.printLog("Data :: $data")
            }
            parameterLiveData.postValue(allParamList)
        }
    }

//    fun getParameterByProfileCode(profileCode: String) = viewModelScope.launch(Dispatchers.Main) {
//        withContext(Dispatchers.IO){
//            val data = useCase.invokeParameterListBaseOnCode(profileCode)
////            Utilities.printLog("Data :: " + data)
//            var filterData = getFilterData(data)
////            Utilities.printLog("Data :: " + filterData)
//            _inputParamList.postValue(filterData)
//        }
//
//    }

    fun updateUserPreference(unit: String?) {
        if (!unit.isNullOrEmpty()) {
            when (unit.lowercase()) {
                /* "cm" -> {
                     sharedPref.edit().putString(PreferenceConstants.HEIGHT_PREFERENCE, "cm").apply()
                 }
                 "kg" -> {
                     sharedPref.edit().putString(PreferenceConstants.WEIGHT_PREFERENCE, "kg").apply()
                 }
                 "lbs" -> {
                     sharedPref.edit().putString(PreferenceConstants.WEIGHT_PREFERENCE, "lib")
                         .apply()
                 }
                 "feet/inch" -> {
                     sharedPref.edit().putString(PreferenceConstants.HEIGHT_PREFERENCE, "feet")
                         .apply()
                 }*/
            }
        }
    }

    fun getPreference(type: String): String {
        if (type.equals("HEIGHT", true)) {
            return preferenceUtils.getPreference(PreferenceConstants.HEIGHT_PREFERENCE, "feet")
        } else if (type.equals("WEIGHT", true)) {
            return preferenceUtils.getPreference(PreferenceConstants.WEIGHT_PREFERENCE, "kg")
        }
        return ""
    }

    fun getParameterByProfileCodeAndDate(profileCode: String, serverDate: String) =
        viewModelScope.launch(Dispatchers.Main) {
            Utilities.printLog("Server Date :: " + serverDate)
            withContext(Dispatchers.IO) {
                if (serverDate.equals(DateHelper.currentDateAsStringddMMMyyyy) && profileCode.equals(
                        "BMI",
                        true
                    )
                ) {
                    val data = useCase.invokeGetLatestParametersData(
                        profileCode,
                        preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                    )
                    Utilities.printLog("Data :: " + data)
                    _inputParamList.postValue(data)
                } else {
                    val data = useCase.invokeGetParameterDataBasedOnRecordDate(
                        profileCode,
                        preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"),
                        serverDate
                    )
                    Utilities.printLog("Data :: " + data)
                    _inputParamList.postValue(data)
                }
            }

        }

    private fun getFilterData(data: List<TrackParameterMaster.Parameter>?): List<ParameterListModel.InputParameterModel> {
        val list: MutableList<ParameterListModel.InputParameterModel> = mutableListOf()
        for (item in data!!) {
            Utilities.printLog("DataItem=> " + item)
            val dataItem = ParameterListModel.InputParameterModel(
                parameterCode = item.code,
                parameterType = item.parameterType,
                description = item.description,
                profileCode = item.profileCode,
                profileName = item.profileName,
                parameterUnit = item.unit,
                minPermissibleValue = if (item.minPermissibleValue.isNullOrEmpty()) "" else item.minPermissibleValue,
                maxPermissibleValue = if (item.maxPermissibleValue.isNullOrEmpty()) "" else item.maxPermissibleValue,
                parameterTextVal = "",
                parameterVal = ""
            )
            list.add(dataItem)
        }
        return list
    }

    fun refreshSelectedParamList(showAllProfile: String) = viewModelScope.launch(Dispatchers.Main) {
        withContext(Dispatchers.IO) {
            _selectedParam.postValue(
                useCase.invokeGetSelectParamList(
                    preferenceUtils.getPreference(
                        PreferenceConstants.SELECTION_PARAM,
                        ""
                    ), showAllProfile
                )
            )
        }
    }

    fun navigateParam(action: NavDirections) {
        navigate(action)
    }

    fun saveParameter(
        parameterDataList: ArrayList<ParameterListModel.InputParameterModel>,
        recordDate: String
    ) = viewModelScope.launch(Dispatchers.Main) {

        val recordList = arrayListOf<SaveParameterModel.Record>()
        val personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")

        for (item in parameterDataList) {
            if (!item.parameterVal.isNullOrEmpty() && !TrackParameterHelper.isNullOrEmptyOrZero(item.parameterVal)) {
                val record = SaveParameterModel.Record(
                    personId,
                    DateHelper.currentUTCDatetimeInMillisecAsString,
                    personId,
                    DateHelper.currentUTCDatetimeInMillisecAsString,
                    item.parameterCode!!,
                    personId,
                    item.profileCode!!,
                    recordDate,
                    item.parameterUnit,
                    item.parameterVal!!,
                    ""
                )
                recordList.add(record)
            } else if (!item.parameterTextVal.isNullOrEmpty()
                && !TrackParameterHelper.isNullOrEmptyOrZero(item.parameterTextVal)
            ) {
                val record = SaveParameterModel.Record(
                    personId,
                    DateHelper.currentUTCDatetimeInMillisecAsString,
                    personId,
                    DateHelper.currentUTCDatetimeInMillisecAsString,
                    item.parameterCode!!,
                    personId,
                    item.profileCode!!,
                    recordDate,
                    "",
                    "",
                    item.parameterTextVal!!
                )
                recordList.add(record)

            }
        }

        if (!recordList.isEmpty()) {
            val requestData = SaveParameterModel(
                Gson().toJson(
                    SaveParameterModel.JSONDataRequest(recordList),
                    SaveParameterModel.JSONDataRequest::class.java
                ),
                preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            _progressBar.value = Event("Saving Parameter values...")
            _saveParam.removeSource(saveParamUserSource)
            withContext(Dispatchers.IO) {
                saveParamUserSource = useCase.invokeSaveParameter(data = requestData)
            }
            _saveParam.addSource(saveParamUserSource) {
                try {
                    _saveParam.value = it.data
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            //                    _toastMessage.value = Event("Parameter successfully updated")
                            //navigate(UpdateParameterFragmentDirections.actionUpdateParameterFragmentToHomeFragment())
                            toastMessage(R.string.PARAMETER_UPDATED)
//                            FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.HEALTH_PARAM_UPLOAD_EVENT)
                        }

                        Resource.Status.ERROR -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.errorNumber.equals("1100014", true)) {
                                _sessionError.value = Event(true)
                            } else {
                                snackMessage(it.errorMessage)
                            }
                        }

                        else -> {}
                    }
                } catch (e: Exception) {
                    Utilities.printException(e)
                }
            }
        } else {
//            _snackbarMessage.value = Event("No Data ")
            toastMessage(R.string.NO_DATA)
        }
    }

    fun showMessage(validationMessage: String) {
        _toastMessage.value = Event(validationMessage)
    }


}