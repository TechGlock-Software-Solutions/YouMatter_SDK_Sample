package com.techglock.health.app.track_parameter.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Event
import com.techglock.health.app.common.utils.PreferenceUtils
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.model.parameter.ParameterData
import com.techglock.health.app.model.parameter.SaveParameterModel
import com.techglock.health.app.repository.utils.Resource
import com.techglock.health.app.track_parameter.domain.ParameterManagementUseCase
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ParameterDetailViewModel @Inject constructor(
    application: Application,
    private val preferenceUtils: PreferenceUtils,
    private val useCase: ParameterManagementUseCase
) : BaseViewModel(application) {

    val paramList: MutableLiveData<List<TrackParameterMaster.Parameter>> by lazy {
        MutableLiveData<List<TrackParameterMaster.Parameter>>()
    }

    val paramHistory: MutableLiveData<List<TrackParameterMaster.History>> by lazy {
        MutableLiveData<List<TrackParameterMaster.History>>()
    }

    var saveParamUserSource: LiveData<Resource<SaveParameterModel.Response>> = MutableLiveData()
    private val _saveParam = MediatorLiveData<SaveParameterModel.Response>()
    val saveParam: LiveData<SaveParameterModel.Response> get() = _saveParam

    fun getParameterList(profileCode: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            Utilities.printLog(
                "TrackParamList=> " + useCase.invokeParameterListBaseOnCode(
                    profileCode
                ).size
            )
            paramList.postValue(useCase.invokeParameterListBaseOnCode(profileCode))
        }
    }

    fun getParameterHistory(parameterCode: String) = viewModelScope.launch(Dispatchers.Main) {
        withContext(Dispatchers.IO) {
            paramHistory.postValue(
                useCase.invokeParameterHisBaseOnCode(
                    parameterCode,
                    preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                )
            )
        }
    }

    fun saveParameter(parameterDataList: ArrayList<ParameterData>, recordDate: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val recordList = arrayListOf<SaveParameterModel.Record>()
            val personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")

            for (item in parameterDataList) {
                if (!item.value.isNullOrEmpty()) {
                    val record = SaveParameterModel.Record(
                        personId,
                        DateHelper.currentUTCDatetimeInMillisecAsString,
                        personId,
                        DateHelper.currentUTCDatetimeInMillisecAsString,
                        item.parameterCode,
                        personId,
                        item.profileCode,
                        recordDate,
                        item.unit,
                        item.value
                    )
                    recordList.add(record)
                }
            }

            if (!recordList.isEmpty()) {
                val requestData = SaveParameterModel(
                    Gson().toJson(
                        SaveParameterModel.JSONDataRequest(recordList),
                        SaveParameterModel.JSONDataRequest::class.java
                    ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
                )


                _saveParam.removeSource(saveParamUserSource)
                withContext(Dispatchers.IO) {
                    saveParamUserSource = useCase.invokeSaveParameter(data = requestData)
                }
                _saveParam.addSource(saveParamUserSource) {
                    try {
                        it?.data?.let { data ->
                            _saveParam.value = data
                        }
                        when (it.status) {
                            Resource.Status.SUCCESS -> {
                                //                        snackMessage("Parameter successfully updated")
                                snackMessage(R.string.PARAMETER_UPDATED)
                            }

                            Resource.Status.ERROR -> {
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
                snackMessage(R.string.NO_DATA)
            }
        }

    fun showMessage(msg: String) {
        _toastMessage.value = Event(msg)
    }


}