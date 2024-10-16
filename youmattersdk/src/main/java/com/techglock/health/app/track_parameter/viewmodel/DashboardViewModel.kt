package com.techglock.health.app.track_parameter.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.utils.*
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.model.parameter.*
import com.techglock.health.app.repository.utils.Resource
import com.techglock.health.app.track_parameter.domain.ParameterManagementUseCase
import com.techglock.health.app.track_parameter.util.TrackParameterHelper
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class DashboardViewModel @Inject constructor(
    application: Application,
    private val useCase: ParameterManagementUseCase,
    private val preferenceUtils: PreferenceUtils,
    val context: Context?
) : BaseViewModel(application) {

    private val personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")

    //val listAllProfiles: MutableLiveData<List<ParameterProfile>> = MutableLiveData()
    var listAllProfiles = MutableLiveData<List<ParameterProfile>>()

    val selectParamLiveData: MutableLiveData<List<ParameterListModel.SelectedParameter>> =
        MutableLiveData()
    val parameterLiveData: MutableLiveData<List<TrackParameterMaster.Parameter>> = MutableLiveData()
    val dashboardLiveData: MutableLiveData<List<DashboardObservationData>> = MutableLiveData()

    var paramPreferenceUserSource: LiveData<Resource<ParameterPreferenceModel.Response>> =
        MutableLiveData()
    private val _paramPreferenceList = MediatorLiveData<ParameterPreferenceModel.Response?>()
    val paramPreferenceList: LiveData<ParameterPreferenceModel.Response?> get() = _paramPreferenceList

    var listHistoryWithLatestRecord = MutableLiveData<List<DashboardParamGridModel>>()

    fun listHistoryWithLatestRecord(fitnessData: FitnessData) = viewModelScope.launch {
        val localResource = LocaleHelper.getLocalizedResources(
            context!!,
            Locale(LocaleHelper.getLanguage(context))
        )!!

        withContext(Dispatchers.IO) {
            try {
                val steps = fitnessData.stepsCount
                val calories = fitnessData.calories
                var systolic = -1
                var diastolic = -1
                var color = R.color.light_gray
                val list: ArrayList<DashboardParamGridModel> = ArrayList()

                val paramWeight =
                    useCase.invokeListHistoryWithLatestRecord(personId, "BMI", "WEIGHT")
                val paramWaist = useCase.invokeListHistoryWithLatestRecord(personId, "WHR", "WAIST")
                val paramBMI = useCase.invokeListHistoryWithLatestRecord(personId, "BMI", "BMI")
                val paramWhr = useCase.invokeListHistoryWithLatestRecord(personId, "WHR", "WHR")
                val paramBp =
                    useCase.invokeListHistoryWithLatestRecord(personId, "BLOODPRESSURE", "")
                //val paramPulse = useCase.invokeListHistoryWithLatestRecord(personId, "BLOODPRESSURE", "BP_PULSE")
                val paramSugar =
                    useCase.invokeListHistoryWithLatestRecord(personId, "DIABETIC", "DIAB_RA")
                val paramChol =
                    useCase.invokeListHistoryWithLatestRecord(personId, "LIPID", "CHOL_TOTAL")
                val paramHb =
                    useCase.invokeListHistoryWithLatestRecord(personId, "HEMOGRAM", "HAEMOGLOBIN")

                val paramCreatinine =
                    useCase.invokeListHistoryWithLatestRecord(personId, "KIDNEY", "SERUMCREATININE")
                val paramBilirubin =
                    useCase.invokeListHistoryWithLatestRecord(personId, "LIVER", "TOTAL_BILIRUBIN")
                val paramTSH = useCase.invokeListHistoryWithLatestRecord(personId, "THYROID", "TSH")

                if (paramBp.isNotEmpty()) {
                    for (i in paramBp.indices) {
                        if (paramBp[i].parameterCode.equals("BP_SYS", ignoreCase = true)) {
                            systolic = i
                        } else if (paramBp[i].parameterCode.equals("BP_DIA", ignoreCase = true)) {
                            diastolic = i
                        }
                    }
                }

                if (paramWeight.isNotEmpty()) {
                    list.add(
                        DashboardParamGridModel(
                            R.drawable.img_weight,
                            R.color.almost_black,
                            localResource.getString(R.string.WEIGHT),
                            paramWeight[0].value!!.toString(),
                            "WEIGHT"
                        )
                    )
                } else {
                    list.add(
                        DashboardParamGridModel(
                            R.drawable.img_weight,
                            R.color.almost_black,
                            localResource.getString(R.string.WEIGHT),
                            " -- ",
                            "WEIGHT"
                        )
                    )
                }

                if (paramWaist.isNotEmpty()) {
                    list.add(
                        DashboardParamGridModel(
                            R.drawable.img_waist,
                            R.color.almost_black,
                            localResource.getString(R.string.WAIST),
                            paramWaist[0].value!!.toString(),
                            "WAIST"
                        )
                    )
                } else {
                    list.add(
                        DashboardParamGridModel(
                            R.drawable.img_waist,
                            R.color.almost_black,
                            localResource.getString(R.string.WAIST),
                            " -- ",
                            "WAIST"
                        )
                    )
                }

                if (paramBMI.isNotEmpty()) {
                    color = if (paramBMI[0].observation.isNullOrEmpty()) {
                        TrackParameterHelper.getObservationColor(
                            CalculateParameters.getBMIObservation(
                                paramBMI[0].value.toString(),
                                context
                            ), paramBMI[0].profileCode!!
                        )
                    } else {
                        TrackParameterHelper.getObservationColor(
                            paramBMI[0].observation,
                            paramBMI[0].profileCode!!
                        )
                    }
                    list.add(
                        DashboardParamGridModel(
                            R.drawable.img_bmi,
                            color,
                            localResource.getString(R.string.BMI),
                            paramBMI[0].value!!.toString(),
                            "BMI"
                        )
                    )
                } else {
                    list.add(
                        DashboardParamGridModel(
                            R.drawable.img_bmi,
                            R.color.almost_black,
                            localResource.getString(R.string.BMI),
                            " -- ",
                            "BMI"
                        )
                    )
                }

                if (paramWhr.isNotEmpty()) {
                    color = TrackParameterHelper.getObservationColor(
                        paramWhr[0].observation,
                        paramWhr[0].profileCode!!
                    )
                    list.add(
                        DashboardParamGridModel(
                            R.drawable.img_whr,
                            color,
                            localResource.getString(R.string.WHR),
                            paramWhr[0].value!!.toString(),
                            "WHR"
                        )
                    )
                } else {
                    list.add(
                        DashboardParamGridModel(
                            R.drawable.img_whr,
                            R.color.almost_black,
                            localResource.getString(R.string.WHR),
                            " -- ",
                            "WHR"
                        )
                    )
                }


                if (systolic != -1 && diastolic != -1) {

                    if (!Utilities.isNullOrEmpty(paramBp[systolic].value.toString())
                        && !Utilities.isNullOrEmpty(paramBp[diastolic].value.toString())
                    ) {
                        color = TrackParameterHelper.getObservationColor(
                            CalculateParameters.getBPObservation(
                                paramBp[systolic].value!!.toInt(),
                                paramBp[diastolic].value!!.toInt(), context
                            ), "BLOODPRESSURE"
                        )
                        list.add(
                            DashboardParamGridModel(
                                R.drawable.img_blood_pressure,
                                color,
                                localResource.getString(R.string.BP),
                                paramBp[systolic].value!!.toDouble().toInt()
                                    .toString() + " / " + paramBp[diastolic].value!!.toDouble()
                                    .toInt().toString(),
                                "BP"
                            )
                        )
                    } else {
                        list.add(
                            DashboardParamGridModel(
                                R.drawable.img_blood_pressure,
                                R.color.almost_black,
                                localResource.getString(R.string.BP),
                                " -- ",
                                "BP"
                            )
                        )
                    }
                } else {
                    list.add(
                        DashboardParamGridModel(
                            R.drawable.img_blood_pressure,
                            R.color.almost_black,
                            localResource.getString(R.string.BP),
                            " -- ",
                            "BP"
                        )
                    )
                }

//                if ( !paramPulse.isNullOrEmpty() ) {
//                    val pulseValue = paramPulse[0].value!!.toString()
//                    color = TrackParameterHelper.getObservationColor(TrackParameterHelper.getPulseObservation(pulseValue), "")
//                    list.add(DashboardParamGridModel(R.drawable.img_heart_risk,color,context.resources.getString(R.string.pulse), pulseValue, "PULSE"))
//                } else {
//                    list.add(DashboardParamGridModel(R.drawable.img_heart_risk,R.color.hlmt_warm_grey,context.resources.getString(R.string.pulse), " -- ", "PULSE"))
//                }

                if (paramSugar.isNotEmpty()) {
                    color = TrackParameterHelper.getObservationColor(
                        paramSugar[0].observation,
                        paramSugar[0].profileCode!!
                    )
                    list.add(
                        DashboardParamGridModel(
                            R.drawable.img_blood_sugar,
                            color,
                            localResource.getString(R.string.BLOOD_SUGAR),
                            paramSugar[0].value!!.toString(),
                            "SUGAR"
                        )
                    )
                } else {
                    list.add(
                        DashboardParamGridModel(
                            R.drawable.img_blood_sugar,
                            R.color.almost_black,
                            localResource.getString(R.string.BLOOD_SUGAR),
                            " -- ",
                            "SUGAR"
                        )
                    )
                }

                if (paramChol.isNotEmpty()) {
                    color = TrackParameterHelper.getObservationColor(
                        paramChol[0].observation,
                        paramChol[0].profileCode!!
                    )
                    list.add(
                        DashboardParamGridModel(
                            R.drawable.img_cholesteroal,
                            color,
                            localResource.getString(R.string.CHOLESTEROL),
                            paramChol[0].value!!.toString(),
                            "CHOL"
                        )
                    )
                } else {
                    list.add(
                        DashboardParamGridModel(
                            R.drawable.img_cholesteroal,
                            R.color.almost_black,
                            localResource.getString(R.string.CHOLESTEROL),
                            " -- ",
                            "CHOL"
                        )
                    )
                }

                if (paramHb.isNotEmpty()) {
                    color = TrackParameterHelper.getObservationColor(
                        paramHb[0].observation,
                        paramHb[0].profileCode!!
                    )
                    list.add(
                        DashboardParamGridModel(
                            R.drawable.img_profile_hemogram,
                            color,
                            localResource.getString(R.string.HEMOGLOBIN),
                            paramHb[0].value!!.toString(),
                            "HEMOGLOBIN"
                        )
                    )
                } else {
                    list.add(
                        DashboardParamGridModel(
                            R.drawable.img_profile_hemogram,
                            R.color.almost_black,
                            localResource.getString(R.string.HEMOGLOBIN),
                            " -- ",
                            "HEMOGLOBIN"
                        )
                    )
                }

                /*                if (!paramCreatinine.isNullOrEmpty()) {
                                    color = TrackParameterHelper.getObservationColor(paramCreatinine[0].observation, paramCreatinine[0].profileCode!!)
                                    list.add(DashboardParamGridModel(R.drawable.img_profile_kidney, color, localResource.getString(R.string.CREATININE), paramCreatinine[0].value!!.toString(), "SERUMCREATININE"))

                                } else {
                                    list.add(DashboardParamGridModel(R.drawable.img_profile_kidney, R.color.almost_black, localResource.getString(R.string.CREATININE), " -- ", "SERUMCREATININE"))
                                }

                                if (!paramBilirubin.isNullOrEmpty()) {
                                    color = TrackParameterHelper.getObservationColor(paramBilirubin[0].observation, paramBilirubin[0].profileCode!!)
                                    list.add(DashboardParamGridModel(R.drawable.img_profile_liver, color, localResource.getString(R.string.TOT_BILIRUBIN), paramBilirubin[0].value!!.toString(), "TOTAL_BILIRUBIN"))
                                } else {
                                    list.add(DashboardParamGridModel(R.drawable.img_profile_liver, R.color.almost_black, localResource.getString(R.string.TOT_BILIRUBIN),
                                            " -- ", "TOTAL_BILIRUBIN"))
                                }

                                if (!paramTSH.isNullOrEmpty()) {
                                    color = TrackParameterHelper.getObservationColor(paramTSH[0].observation, paramTSH[0].profileCode!!)
                                    list.add(DashboardParamGridModel(R.drawable.img_profile_thyroid, color, localResource.getString(R.string.TSH), paramTSH[0].value!!.toString(), "TSH"))
                                } else {
                                    list.add(DashboardParamGridModel(R.drawable.img_profile_thyroid, R.color.almost_black, localResource.getString(R.string.TSH), " -- ", "TSH"))
                                }*/

                if (isSelfUser()) {
                    if (!Utilities.isNullOrEmptyOrZero(steps)) {
                        list.add(
                            DashboardParamGridModel(
                                R.drawable.img_step,
                                R.color.almost_black,
                                localResource.getString(R.string.STEPS),
                                steps,
                                "STEPS"
                            )
                        )
                    } else {
                        list.add(
                            DashboardParamGridModel(
                                R.drawable.img_step,
                                R.color.almost_black,
                                localResource.getString(R.string.STEPS),
                                "0",
                                "STEPS"
                            )
                        )
                    }

                    if (!Utilities.isNullOrEmptyOrZero(calories)) {
                        list.add(
                            DashboardParamGridModel(
                                R.drawable.img_calories,
                                R.color.almost_black,
                                localResource.getString(R.string.CALORIES),
                                calories,
                                "CAL"
                            )
                        )
                    } else {
                        list.add(
                            DashboardParamGridModel(
                                R.drawable.img_calories,
                                R.color.almost_black,
                                localResource.getString(R.string.CALORIES),
                                "0",
                                "CAL"
                            )
                        )
                    }
                }

//                list.add(DashboardParamGridModel(R.drawable.add,R.color.white,context.resources.getString(R.string.add_more), "", "ADD"))

                listHistoryWithLatestRecord.postValue(list)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getSelectParameterList() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            selectParamLiveData.postValue(
                useCase.invokeGetSelectParamList(
                    preferenceUtils.getPreference(
                        PreferenceConstants.SELECTION_PARAM,
                        ""
                    )
                )
            )
        }
    }

    fun showProgressBar() {
        _progressBar.value = Event("")
    }

    fun getAllProfileCodes() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            listAllProfiles.postValue(useCase.invokeGetAllProfileCodes())
        }
    }

    fun getAllProfilesWithRecentSelectionList() = viewModelScope.launch {
        _progressBar.value = Event("Profiles List..")
        withContext(Dispatchers.IO) {
            listAllProfiles.postValue(useCase.invokeGetAllProfilesWithRecentSelectionList(personId))
        }
    }

    fun updateSelection(adapterPosition: Int) {
        selectParamLiveData.value?.get(adapterPosition)?.selectionStatus =
            !selectParamLiveData.value?.get(adapterPosition)?.selectionStatus!!
        selectParamLiveData.value = selectParamLiveData.value
    }

    fun saveSelectedParameter(dataList: MutableList<ParameterProfile>) {
        val selectedParameter = StringBuilder("")
        for (item in dataList) {
            if (item.isSelection) {
                selectedParameter.append(item.profileCode + "|")
            }
        }
        Utilities.printLog("SelectedParam--->$selectedParameter")
        preferenceUtils.storePreference(
            PreferenceConstants.SELECTION_PARAM,
            selectedParameter.toString()
        )
    }

    fun getTrackParameters() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            parameterLiveData.postValue(useCase.invokeGetDBParamList())
        }
    }

    fun getDashboardParamList() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            Utilities.printLog("DashboardData=> " + useCase.invokeGetDashboardData())
            dashboardLiveData.postValue(useCase.invokeGetDashboardData())
        }
    }

    fun getPreferenceList() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = ParameterPreferenceModel(
            Gson().toJson(
                ParameterPreferenceModel.JSONDataRequest(
                    personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                ),
                ParameterPreferenceModel.JSONDataRequest::class.java
            ),
            preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _paramPreferenceList.removeSource(paramPreferenceUserSource)
        withContext(Dispatchers.IO) {
            paramPreferenceUserSource = useCase.invokeParameterPreference(data = requestData)
        }
        _paramPreferenceList.addSource(paramPreferenceUserSource) {
            try {
                _paramPreferenceList.value = it.data
                when (it.status) {
                    Resource.Status.SUCCESS -> {
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
    }

    fun navigateParam(action: NavDirections) {
        navigate(action)
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

}