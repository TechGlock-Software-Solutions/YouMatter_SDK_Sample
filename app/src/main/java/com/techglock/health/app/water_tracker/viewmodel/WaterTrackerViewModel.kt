package com.techglock.health.app.water_tracker.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.Event
import com.techglock.health.app.common.utils.PreferenceUtils
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.model.hra.HraMedicalProfileSummaryModel
import com.techglock.health.app.model.waterTracker.*
import com.techglock.health.app.repository.utils.Resource
import com.techglock.health.app.water_tracker.common.WaterTrackerHelper
import com.techglock.health.app.water_tracker.domain.WaterTrackerManagementUseCase
import com.techglock.health.app.water_tracker.ui.AddWaterIntakeBottomSheet
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class WaterTrackerViewModel @Inject constructor(
    application: Application,
    private val waterTrackerManagementUseCase: WaterTrackerManagementUseCase,
    private val preferenceUtils: PreferenceUtils,
    val waterTrackerHelper: WaterTrackerHelper,
    private val context: Context?
) : BaseViewModel(application) {

    var joiningDate = preferenceUtils.getPreference(PreferenceConstants.JOINING_DATE, "")

    private var saveWaterIntakeGoalSource: LiveData<Resource<SaveWaterIntakeGoalModel.SaveWaterIntakeGoalResponse>> =
        MutableLiveData()
    private val _saveWaterIntakeGoal =
        MediatorLiveData<SaveWaterIntakeGoalModel.SaveWaterIntakeGoalResponse?>()
    val saveWaterIntakeGoal: LiveData<SaveWaterIntakeGoalModel.SaveWaterIntakeGoalResponse?> get() = _saveWaterIntakeGoal

    private var saveDailyWaterIntakeSource: LiveData<Resource<SaveDailyWaterIntakeModel.SaveDailyWaterIntakeResponse>> =
        MutableLiveData()
    private val _saveDailyWaterIntake =
        MediatorLiveData<SaveDailyWaterIntakeModel.SaveDailyWaterIntakeResponse?>()
    val saveDailyWaterIntake: LiveData<SaveDailyWaterIntakeModel.SaveDailyWaterIntakeResponse?> get() = _saveDailyWaterIntake

    private var getDailyWaterIntakeSource: LiveData<Resource<GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse>> =
        MutableLiveData()
    private val _getDailyWaterIntake =
        MediatorLiveData<GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse?>()
    val getDailyWaterIntake: LiveData<GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse?> get() = _getDailyWaterIntake

    private var getWaterIntakeHistoryByDateSource: LiveData<Resource<GetWaterIntakeHistoryByDateModel.GetWaterIntakeHistoryByDateResponse>> =
        MutableLiveData()
    private val _getWaterIntakeHistoryByDate =
        MediatorLiveData<GetWaterIntakeHistoryByDateModel.GetWaterIntakeHistoryByDateResponse?>()
    val getWaterIntakeHistoryByDate: LiveData<GetWaterIntakeHistoryByDateModel.GetWaterIntakeHistoryByDateResponse?> get() = _getWaterIntakeHistoryByDate

    private var getWaterIntakeSummarySource: LiveData<Resource<GetWaterIntakeSummaryModel.GetWaterIntakeSummaryResponse>> =
        MutableLiveData()
    private val _getWaterIntakeSummary =
        MediatorLiveData<GetWaterIntakeSummaryModel.GetWaterIntakeSummaryResponse?>()
    val getWaterIntakeSummary: LiveData<GetWaterIntakeSummaryModel.GetWaterIntakeSummaryResponse?> get() = _getWaterIntakeSummary

    private var medicalProfileSummarySource: LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> =
        MutableLiveData()
    private val _medicalProfileSummary =
        MediatorLiveData<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse?>()
    val medicalProfileSummary: LiveData<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse?> get() = _medicalProfileSummary

    fun callSaveWaterIntakeGoalApi(
        weight: String,
        isExercise: String,
        duration: String,
        notification: String,
        date: String,
        goal: String,
        goalType: String,
        txtWaterIntakeTarget: TextView
    ) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = SaveWaterIntakeGoalModel(
            Gson().toJson(
                SaveWaterIntakeGoalModel.JSONDataRequest(
                    SaveWaterIntakeGoalModel.Request(
                        personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                            .toInt(),
                        weight = weight,
                        exercise = isExercise,
                        exerciseDuration = duration,
                        isNotification = notification,
                        recordDate = date,
                        waterGoal = goal,
                        type = goalType
                    )
                ), SaveWaterIntakeGoalModel.JSONDataRequest::class.java
            ),
            preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _progressBar.value = Event("")
        _saveWaterIntakeGoal.removeSource(saveWaterIntakeGoalSource)
        withContext(Dispatchers.IO) {
            saveWaterIntakeGoalSource = waterTrackerManagementUseCase.invokeSaveWaterIntakeGoal(
                isForceRefresh = true,
                data = requestData
            )
        }
        _saveWaterIntakeGoal.addSource(saveWaterIntakeGoalSource) {
            try {
                _saveWaterIntakeGoal.value = it.data
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        if (it.data != null) {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            /*                    val result = it.data!!.result
                                Utilities.printData("result",result,true)
                                var dailyIntake = ""
                                when(result.type) {
                                    "DEFAULT" -> {
                                        dailyIntake = "Your Daily Water Intake <br/> should be : <a><B><font color='#00ceff'>${result.waterGoal} ml</font></B></a>"
                                    }
                                    "CUSTOM" -> {
                                        dailyIntake = "You have set Daily Water Intake <br/> target to : <a><B><font color='#00ceff'>${result.waterGoal} ml</font></B></a>"
                                    }
                                }
                                txtWaterIntakeTarget.text = Html.fromHtml(dailyIntake)*/
                        }
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

    fun callSaveDailyWaterIntakeApi(
        bs: AddWaterIntakeBottomSheet,
        listener: AddWaterIntakeBottomSheet.OnIntakeClickListener,
        code: String,
        value: String,
        date: String,
        isCurrentDate: String
    ) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = SaveDailyWaterIntakeModel(
            Gson().toJson(
                SaveDailyWaterIntakeModel.JSONDataRequest(
                    SaveDailyWaterIntakeModel.Request(
                        personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                            .toInt(),
                        beverageCode = code,
                        waterValue = value,
                        recordDate = date,
                        isCurrentDateLog = isCurrentDate
                    )
                ), SaveDailyWaterIntakeModel.JSONDataRequest::class.java
            ),
            preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _progressBar.value = Event("")
        _saveDailyWaterIntake.removeSource(saveDailyWaterIntakeSource)
        withContext(Dispatchers.IO) {
            saveDailyWaterIntakeSource = waterTrackerManagementUseCase.invokeSaveDailyWaterIntake(
                isForceRefresh = true,
                data = requestData
            )
        }
        _saveDailyWaterIntake.addSource(saveDailyWaterIntakeSource) {
            try {
                _saveDailyWaterIntake.value = it.data
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        if (it.data != null) {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            val result = it.data.result
                            Utilities.printData("result", result, true)
                            if (!Utilities.isNullOrEmptyOrZero(result.waterID)) {
                                bs.dismiss()
                                Utilities.toastMessageShort(context, "Intake Added Successfully")
                                val data = HashMap<String, Any>()
                                data[CleverTapConstants.QUANTITY] = value
                                CleverTapHelper.pushEventWithProperties(
                                    context,
                                    CleverTapConstants.LOG_WATER_INTAKE,
                                    data
                                )
                                if (isCurrentDate == Constants.TRUE) {
                                    listener.onIntakeClick(code, value)
                                }
                            }
                        }
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

    fun callGetDailyWaterIntakeApi(date: String) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = GetDailyWaterIntakeModel(
            Gson().toJson(
                GetDailyWaterIntakeModel.JSONDataRequest(
                    GetDailyWaterIntakeModel.Request(
                        personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                            .toInt(),
                        recordDate = date
                    )
                ), GetDailyWaterIntakeModel.JSONDataRequest::class.java
            ),
            preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        //_progressBar.value = Event("")
        _getDailyWaterIntake.removeSource(getDailyWaterIntakeSource)
        withContext(Dispatchers.IO) {
            getDailyWaterIntakeSource = waterTrackerManagementUseCase.invokeGetDailyWaterIntake(
                isForceRefresh = true,
                data = requestData
            )
        }
        _getDailyWaterIntake.addSource(getDailyWaterIntakeSource) {
            try {
                _getDailyWaterIntake.value = it.data
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        if (it.data != null) {
                            //_progressBar.value = Event(Event.HIDE_PROGRESS)
                            /*                    val result = it.data!!.result.result[0]
                                Utilities.printData("result",result,true)
                                fragment.loadData(result)*/
                        }
                    }

                    Resource.Status.ERROR -> {
                        //_progressBar.value = Event(Event.HIDE_PROGRESS)
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

    fun callGetWaterIntakeHistoryByDateApi(startDate: String, endDate: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = GetWaterIntakeHistoryByDateModel(
                Gson().toJson(
                    GetWaterIntakeHistoryByDateModel.JSONDataRequest(
                        GetWaterIntakeHistoryByDateModel.Request(
                            personID = preferenceUtils.getPreference(
                                PreferenceConstants.PERSONID,
                                "0"
                            ).toInt(),
                            fromDate = startDate,
                            toDate = endDate,
                            type = "MONTHLY"
                        )
                    ), GetWaterIntakeHistoryByDateModel.JSONDataRequest::class.java
                ),
                preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            //_progressBar.value = Event("")
            _getWaterIntakeHistoryByDate.removeSource(getWaterIntakeHistoryByDateSource)
            withContext(Dispatchers.IO) {
                getWaterIntakeHistoryByDateSource =
                    waterTrackerManagementUseCase.invokeGetWaterIntakeHistoryByDate(
                        isForceRefresh = true,
                        data = requestData
                    )
            }
            _getWaterIntakeHistoryByDate.addSource(getWaterIntakeHistoryByDateSource) {
                try {
                    _getWaterIntakeHistoryByDate.value = it.data
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            if (it.data != null) {
                                //_progressBar.value = Event(Event.HIDE_PROGRESS)
                                /*                    val History = it.data!!.result
                                    Utilities.printData("History",result,true)*/
                            }
                        }

                        Resource.Status.ERROR -> {
                            //_progressBar.value = Event(Event.HIDE_PROGRESS)
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

    fun callGetWaterIntakeSummaryApi(startDate: String, endDate: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = GetWaterIntakeSummaryModel(
                Gson().toJson(
                    GetWaterIntakeSummaryModel.JSONDataRequest(
                        GetWaterIntakeSummaryModel.Request(
                            personID = preferenceUtils.getPreference(
                                PreferenceConstants.PERSONID,
                                "0"
                            ).toInt(),
                            fromDate = startDate,
                            toDate = endDate
                        )
                    ), GetWaterIntakeSummaryModel.JSONDataRequest::class.java
                ),
                preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            //_progressBar.value = Event("")
            _getWaterIntakeSummary.removeSource(getWaterIntakeSummarySource)
            withContext(Dispatchers.IO) {
                getWaterIntakeSummarySource =
                    waterTrackerManagementUseCase.invokeGetWaterIntakeSummary(
                        isForceRefresh = true,
                        data = requestData
                    )
            }
            _getWaterIntakeSummary.addSource(getWaterIntakeSummarySource) {
                try {
                    _getWaterIntakeSummary.value = it.data
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            if (it.data != null) {
                                //_progressBar.value = Event(Event.HIDE_PROGRESS)
                                /*                    val History = it.data!!.result
                                    Utilities.printData("History",result,true)*/
                            }
                        }

                        Resource.Status.ERROR -> {
                            //_progressBar.value = Event(Event.HIDE_PROGRESS)
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

    fun getMedicalProfileSummary() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = HraMedicalProfileSummaryModel(
            Gson().toJson(
                HraMedicalProfileSummaryModel.JSONDataRequest(
                    PersonID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                ),
                HraMedicalProfileSummaryModel.JSONDataRequest::class.java
            ),
            preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _progressBar.value = Event("")
        _medicalProfileSummary.removeSource(medicalProfileSummarySource)
        withContext(Dispatchers.IO) {
            medicalProfileSummarySource = waterTrackerManagementUseCase.invokeMedicalProfileSummary(
                isForceRefresh = true,
                data = requestData,
                personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
            )
        }
        _medicalProfileSummary.addSource(medicalProfileSummarySource) {
            try {
                _medicalProfileSummary.value = it.data
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.data != null) {
                            //val summary = it.data!!.MedicalProfileSummary!!
                            //Utilities.printLog("MedicalProfileSummary :- ${it.data}")
                            try {
                                //val userInfo = UserInfoModel.getInstance()!!
                                //userInfo.setWeight(summary.weight.toString())
                                //userInfo.isDataLoaded = true
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
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

    fun isFirstWaterTrackerVisit(): Boolean {
        return preferenceUtils.getBooleanPreference(
            PreferenceConstants.IS_FIRST_WATER_TRACKER_VISIT,
            true
        )
    }

    fun setFirstWaterTrackerVisitFlag(flag: Boolean) {
        preferenceUtils.storeBooleanPreference(
            PreferenceConstants.IS_FIRST_WATER_TRACKER_VISIT,
            flag
        )
    }

    fun getPreference(type: String): String {
        if (type.equals("HEIGHT", true)) {
            return preferenceUtils.getPreference(PreferenceConstants.HEIGHT_PREFERENCE, "feet")
        } else if (type.equals("WEIGHT", true)) {
            return preferenceUtils.getPreference(PreferenceConstants.WEIGHT_PREFERENCE, "kg")
        }
        return ""
    }

    fun updateUserPreference(unit: String?) {
        if (!unit.isNullOrEmpty()) {
            when (unit.lowercase()) {
                /*"cm" -> {
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

}