package com.techglock.health.app.fitness_tracker.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.fitness.FitnessDataManager
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Event
import com.techglock.health.app.common.utils.PreferenceUtils
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.fitness_tracker.common.StepsDataSingleton
import com.techglock.health.app.fitness_tracker.domain.FitnessManagementUseCase
import com.techglock.health.app.fitness_tracker.ui.FitnessDataActivity
import com.techglock.health.app.fitness_tracker.ui.TodayFragment
import com.techglock.health.app.fitness_tracker.util.FitnessHelper
import com.techglock.health.app.fitness_tracker.util.StepCountHelper
import com.techglock.health.app.model.fitness.GetStepsGoalModel
import com.techglock.health.app.model.fitness.SetGoalModel
import com.techglock.health.app.model.fitness.StepsHistoryModel
import com.techglock.health.app.model.fitness.StepsSaveListModel
import com.techglock.health.app.repository.utils.Resource
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class ActivityTrackerViewModel @Inject constructor(
    application: Application,
    private val preferenceUtils: PreferenceUtils,
    private val useCase: FitnessManagementUseCase,
    val fitnessHelper: FitnessHelper,
    val stepCountHelper: StepCountHelper,
    private val context: Context?
) : BaseViewModel(application) {

    //var personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
    //var adminPersonId = preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0")
    var joiningDate = preferenceUtils.getPreference(PreferenceConstants.JOINING_DATE, "")
    private val stepsDataSingleton = StepsDataSingleton.instance!!

    private var stepsHistorySource: LiveData<Resource<StepsHistoryModel.Response>> =
        MutableLiveData()
    private val _stepsHistoryList = MediatorLiveData<StepsHistoryModel.Response?>()
    val stepsHistoryList: LiveData<StepsHistoryModel.Response?> get() = _stepsHistoryList

    private var stepsHistoryBetweenSource: LiveData<Resource<StepsHistoryModel.Response>> =
        MutableLiveData()
    private val _stepsHistoryBetweenList = MediatorLiveData<StepsHistoryModel.Response?>()
    val stepsHistoryBetweenList: LiveData<StepsHistoryModel.Response?> get() = _stepsHistoryBetweenList

    private var getLatestStepsGoalSource: LiveData<Resource<GetStepsGoalModel.Response>> =
        MutableLiveData()
    private val _getLatestStepsGoal = MediatorLiveData<GetStepsGoalModel.Response?>()
    val getLatestStepsGoal: LiveData<GetStepsGoalModel.Response?> get() = _getLatestStepsGoal

    private var saveStepsGoalSource: LiveData<Resource<SetGoalModel.Response>> = MutableLiveData()
    private val _saveStepsGoal = MediatorLiveData<SetGoalModel.Response?>()
    val saveStepsGoal: LiveData<SetGoalModel.Response?> get() = _saveStepsGoal

    private var saveStepsListSource: LiveData<Resource<StepsSaveListModel.StepsSaveListResponse>> =
        MutableLiveData()
    private val _saveStepsList = MediatorLiveData<StepsSaveListModel.StepsSaveListResponse?>()
    val saveStepsList: LiveData<StepsSaveListModel.StepsSaveListResponse?> get() = _saveStepsList

    fun getLatestStepGoal(
        mActivity: FitnessDataActivity,
        latestGoalListener: FitnessDataActivity.OnLatestGoalListener,
        stepCountHelper: StepCountHelper
    ) = viewModelScope.launch(Dispatchers.Main) {
        try {
            val requestData = GetStepsGoalModel(
                Gson().toJson(
                    GetStepsGoalModel.JSONDataRequest(
                        personID = preferenceUtils.getPreference(
                            PreferenceConstants.ADMIN_PERSON_ID,
                            "0"
                        )
                    ),
                    GetStepsGoalModel.JSONDataRequest::class.java
                ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            if (!mActivity.screen.equals("STEPS_DAILY_TARGET", ignoreCase = true)) {
                _progressBar.value = Event("Loading...")
            }

            _getLatestStepsGoal.removeSource(getLatestStepsGoalSource)
            withContext(Dispatchers.IO) {
                getLatestStepsGoalSource = useCase.invokeFetchStepsGoal(requestData)
            }
            _getLatestStepsGoal.addSource(getLatestStepsGoalSource) {
                try {
                    _getLatestStepsGoal.value = it.data
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            val latestGoal = it.data!!.latestGoal
                            latestGoal.date = latestGoal.date.split("T").toTypedArray()[0]
                            stepsDataSingleton.latestGoal = latestGoal
                            latestGoalListener.onLatestGoalReceived(latestGoal)
                            //getFitnessHistory(stepCountHelper,mActivity)
                            if (getIsFitnessDataNotSync()) {
                                StepsDataSingleton.instance!!.stepHistoryList.clear()
                                stepCountHelper.synchronize(context, mActivity)
                            } else {
                                getFitnessHistory(stepCountHelper, mActivity)
                            }
                        }

                        Resource.Status.ERROR -> {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getFitnessHistory(
        stepCountHelper: StepCountHelper,
        mActivity: FitnessDataActivity
    ) =
        viewModelScope.launch(Dispatchers.Main) {

            val df = SimpleDateFormat(DateHelper.SERVER_DATE_YYYYMMDD, Locale.getDefault())
            val calEndTime = Calendar.getInstance()
            calEndTime.add(Calendar.DATE, -30)
            val fromDate = df.format(calEndTime.time)

            val searchCriteria: StepsHistoryModel.SearchCriteria = StepsHistoryModel.SearchCriteria(
                fromDate = fromDate,
                toDate = DateHelper.currentDateAsStringyyyyMMdd,
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

            if (!mActivity.screen.equals("STEPS_DAILY_TARGET", ignoreCase = true)) {
                _progressBar.value = Event("Loading...")
            }

            _stepsHistoryList.removeSource(stepsHistorySource)
            withContext(Dispatchers.IO) {
                stepsHistorySource = useCase.invokeStepsHistory(requestData)
            }
            _stepsHistoryList.addSource(stepsHistorySource) {
                try {
                    _stepsHistoryList.value = it.data

                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            //Utilities.printLog("History---> ${it.data}")
                            val history = it.data!!.stepGoalHistory.toMutableList()
                            Utilities.printData("ApiHistory", history)
                            if (!history.isNullOrEmpty()) {
                                stepsDataSingleton.stepHistoryList.clear()
                                stepsDataSingleton.stepHistoryList.addAll(history)
                            }
                            if (FitnessDataManager.getInstance(context)!!
                                    .oAuthPermissionsApproved()
                            ) {
                                stepCountHelper.synchronize(context, mActivity)
                                /*                        if ( mActivity.isForceSync ) {
                                                                        stepCountHelper.synchronizeForce(context)
                                                                    } else {
                                                                        stepCountHelper.synchronize(context,mActivity)
                                                                    }*/
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

    fun saveStepsGoal(fragment: TodayFragment, goal: Int) =
        viewModelScope.launch(Dispatchers.Main) {

            val request: SetGoalModel.StepsGoalsReq = SetGoalModel.StepsGoalsReq(
                date = DateHelper.currentUTCDatetimeInMillisecAsString,
                goal = goal,
                personID = preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0"),
                type = "WAL"
            )

            val requestData = SetGoalModel(
                Gson().toJson(
                    SetGoalModel.JSONDataRequest(
                        stepsGoals = request,
                        message = "Getting List.."
                    ),
                    SetGoalModel.JSONDataRequest::class.java
                ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            _progressBar.value = Event("Loading...")
            _saveStepsGoal.removeSource(saveStepsGoalSource)
            withContext(Dispatchers.IO) {
                saveStepsGoalSource = useCase.invokeSaveStepsGoal(requestData)
            }
            _saveStepsGoal.addSource(saveStepsGoalSource) {

                try {
                    _saveStepsGoal.value = it.data
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            fragment.saveGoalResp(it.data!!.stepsGoals)
                            Utilities.toastMessageShort(
                                context,
                                context!!.resources.getString(R.string.MSG_GOALS_UPDATED)
                            )
                            CleverTapHelper.pushEvent(context, CleverTapConstants.UPDATE_GOAL)
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

    fun saveStepsList(fitnessDataJSONArray: JSONArray, mActivity: FitnessDataActivity) =
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val stepsDetailsList = ArrayList<StepsSaveListModel.StepsDetail>()
                for (i in 0 until fitnessDataJSONArray.length()) {
                    if (fitnessDataJSONArray.getJSONObject(i) != null) {
                        //StepsCount,Calories,Distance,ActiveTime
                        val stepsDataObj = fitnessDataJSONArray.getJSONObject(i)
                        val stepsDetail = StepsSaveListModel.StepsDetail(
                            personID = preferenceUtils.getPreference(
                                PreferenceConstants.ADMIN_PERSON_ID,
                                "0"
                            ),
                            recordDate = DateHelper.convertDateTimeValue(
                                stepsDataObj.getString(
                                    Constants.RECORD_DATE
                                ), DateHelper.SERVER_DATE_YYYYMMDD, DateHelper.SERVER_DATE_YYYYMMDD
                            )!!,
                            count = stepsDataObj.getString(Constants.STEPS_COUNT),
                            calories = stepsDataObj.getString(Constants.CALORIES),
                            distance = stepsDataObj.getString(Constants.DISTANCE)
                        )
                        stepsDetailsList.add(stepsDetail)
                    }
                }
                val requestData = StepsSaveListModel(
                    Gson().toJson(
                        StepsSaveListModel.JSONDataRequest(
                            stepsDetails = stepsDetailsList
                        ),
                        StepsSaveListModel.JSONDataRequest::class.java
                    ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
                )

                if (!mActivity.screen.equals("STEPS_DAILY_TARGET", ignoreCase = true)) {
                    _progressBar.value = Event("Loading...")
                }

                _saveStepsList.removeSource(saveStepsListSource)
                withContext(Dispatchers.IO) {
                    saveStepsListSource = useCase.invokeSaveStepsList(requestData)
                }
                _saveStepsGoal.addSource(saveStepsListSource) {
                    try {
                        _saveStepsList.value = it.data
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

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    fun getStepsHistoryBetweenDates(startDate: String, endtDate: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val searchCriteria: StepsHistoryModel.SearchCriteria = StepsHistoryModel.SearchCriteria(
                fromDate = startDate, toDate = endtDate,
                personID = preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0")
            )

            val requestData = StepsHistoryModel(
                Gson().toJson(
                    StepsHistoryModel.JSONDataRequest(
                        searchCriteria = searchCriteria, message = "Getting List.."
                    ),
                    StepsHistoryModel.JSONDataRequest::class.java
                ),
                preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            _progressBar.value = Event("Loading...")
            _stepsHistoryBetweenList.removeSource(stepsHistoryBetweenSource)
            withContext(Dispatchers.IO) {
                stepsHistoryBetweenSource = useCase.invokeStepsHistoryBetweenDates(requestData)
            }
            _stepsHistoryBetweenList.addSource(stepsHistoryBetweenSource) {
                try {
                    _stepsHistoryBetweenList.value = it.data
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

    fun showProgressBar() {
        _progressBar.value = Event("")
    }

    fun getIsFitnessDataNotSync(): Boolean {
        val isFitnessDataNotSync =
            preferenceUtils.getBooleanPreference(PreferenceConstants.IS_FITNESS_DATA_NOT_SYNC, true)
        Utilities.printLogError("isFitnessDataNotSync--->$isFitnessDataNotSync")
        return isFitnessDataNotSync
    }

    fun setIsFitnessDataNotSync(flag: Boolean) {
        preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_FITNESS_DATA_NOT_SYNC, flag)
    }

}