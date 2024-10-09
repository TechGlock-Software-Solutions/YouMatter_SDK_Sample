package com.techglock.health.app.track_parameter.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.utils.Event
import com.techglock.health.app.common.utils.PreferenceUtils
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.model.parameter.BMIHistoryModel
import com.techglock.health.app.model.parameter.BloodPressureHistoryModel
import com.techglock.health.app.model.parameter.LabRecordsListModel
import com.techglock.health.app.model.parameter.ParameterListModel
import com.techglock.health.app.model.parameter.WHRHistoryModel
import com.techglock.health.app.repository.utils.Resource
import com.techglock.health.app.track_parameter.domain.ParameterManagementUseCase
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ParameterHomeViewModel @Inject constructor(
    private val useCase: ParameterManagementUseCase,
    application: Application,
    private val preferenceUtils: PreferenceUtils
) : BaseViewModel(application) {

    private var personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")

    var paramUserSource: LiveData<Resource<ParameterListModel.Response>> = MutableLiveData()
    private val _paramList = MediatorLiveData<ParameterListModel.Response>()
    val paramList: LiveData<ParameterListModel.Response> get() = _paramList

    private var labRecordUserSource: LiveData<Resource<TrackParameterMaster.HistoryResponse>> =
        MutableLiveData()
    private val _labRecordList = MediatorLiveData<TrackParameterMaster.HistoryResponse>()
    val labRecordList: LiveData<TrackParameterMaster.HistoryResponse> get() = _labRecordList

    private var bmiUserSource: LiveData<Resource<BMIHistoryModel.Response>> = MutableLiveData()
    private val _bmiHistoryList = MediatorLiveData<BMIHistoryModel.Response>()
    val bmiHistoryList: LiveData<BMIHistoryModel.Response> get() = _bmiHistoryList

    private var whrUserSource: LiveData<Resource<WHRHistoryModel.Response>> = MutableLiveData()
    private val _whrHistoryList = MediatorLiveData<WHRHistoryModel.Response>()
    val whrHistoryList: LiveData<WHRHistoryModel.Response> get() = _whrHistoryList

    private var bloodPressureUserSource: LiveData<Resource<BloodPressureHistoryModel.Response>> =
        MutableLiveData()
    private val _bloodPressureHistoryList = MediatorLiveData<BloodPressureHistoryModel.Response>()
    val bloodPressureHistoryList: LiveData<BloodPressureHistoryModel.Response> get() = _bloodPressureHistoryList

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

        _progressBar.value = Event("Fetching Parameter List..")
        _paramList.removeSource(paramUserSource)
        withContext(Dispatchers.IO) {
            paramUserSource = useCase.invokeParamList(data = requestData)
        }
        _paramList.addSource(paramUserSource) {
            try {
                it?.data?.let { data ->
                    _paramList.value = data
                }
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
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
    }

    fun getLabRecordList() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = LabRecordsListModel(
            Gson().toJson(
                LabRecordsListModel.JSONDataRequest(
                    personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                ),
                LabRecordsListModel.JSONDataRequest::class.java
            ),
            preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _labRecordList.removeSource(labRecordUserSource)
        withContext(Dispatchers.IO) {
            labRecordUserSource = useCase.invokeLabRecordsList(
                data = requestData,
                personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
            )
        }
        _labRecordList.addSource(labRecordUserSource) {
            try {
                it?.data?.let { data ->
                    _labRecordList.value = data
                }
                when (it.status) {
                    Resource.Status.SUCCESS -> {
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
    }

    fun getBMIHistory() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = BMIHistoryModel(
            Gson().toJson(
                BMIHistoryModel.JSONDataRequest(
                    personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                ),
                BMIHistoryModel.JSONDataRequest::class.java
            ),
            preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _bmiHistoryList.removeSource(bmiUserSource)
        withContext(Dispatchers.IO) {
            bmiUserSource = useCase.invokeBMIHistory(data = requestData, personId)
        }
        _bmiHistoryList.addSource(bmiUserSource) {
            try {
                it?.data?.let { data ->
                    _bmiHistoryList.value = data
                }
                when (it.status) {
                    Resource.Status.SUCCESS -> {
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
    }

    fun getWHRHistory() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = WHRHistoryModel(
            Gson().toJson(
                WHRHistoryModel.JSONDataRequest(
                    personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                ),
                WHRHistoryModel.JSONDataRequest::class.java
            ),
            preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _whrHistoryList.removeSource(whrUserSource)
        withContext(Dispatchers.IO) {
            whrUserSource = useCase.invokeWHRHistory(data = requestData, personId)
        }
        _whrHistoryList.addSource(whrUserSource) {
            try {
                it?.data?.let { data ->
                    _whrHistoryList.value = data
                }
                when (it.status) {
                    Resource.Status.SUCCESS -> {
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
    }

    fun getBloodPressureHistory() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = BloodPressureHistoryModel(
            Gson().toJson(
                BloodPressureHistoryModel.JSONDataRequest(
                    personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                ),
                BloodPressureHistoryModel.JSONDataRequest::class.java
            ),
            preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _bloodPressureHistoryList.removeSource(bloodPressureUserSource)
        withContext(Dispatchers.IO) {
            bloodPressureUserSource =
                useCase.invokeBloodPressureHistory(data = requestData, personId)
        }
        _bloodPressureHistoryList.addSource(bloodPressureUserSource) {
            try {
                it?.data?.let { data ->
                    _bloodPressureHistoryList.value = data
                }
                when (it.status) {
                    Resource.Status.SUCCESS -> {
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
    }


    fun navigateParam(action: NavDirections) {
        navigate(action)
    }


}