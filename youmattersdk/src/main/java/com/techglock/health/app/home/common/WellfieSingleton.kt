package com.techglock.health.app.home.common

import android.content.Context
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.model.home.WellfieGetVitalsModel
import com.techglock.health.app.model.home.WellfieSaveVitalsModel

class WellfieSingleton {

    var dateTime = ""
    private var bmiVitalsList: List<TrackParameterMaster.History> = ArrayList()
    private var wellfieResultList: List<DataHandler.WellfieResultModel> = ArrayList()

    fun getBmiVitalsList(): List<TrackParameterMaster.History> {
        return bmiVitalsList
    }

    fun setBmiVitalsList(bmiVitalsList: List<TrackParameterMaster.History>) {
        this.bmiVitalsList = bmiVitalsList
    }

    fun getWellfieResultList(): List<DataHandler.WellfieResultModel> {
        return wellfieResultList
    }

    fun setWellfieResultList(wellfieResultList: List<DataHandler.WellfieResultModel>) {
        this.wellfieResultList = wellfieResultList
    }

    fun logCleverTapScanVitalsInfo(context: Context, list:MutableList<WellfieGetVitalsModel.Report>, date:String) {
        val saltData = HashMap<String, Any>()
        saltData[CleverTapConstants.DATE] = date
        for (i in list) {
            when (i.paramCode) {
                "BP_SYS" -> saltData[CleverTapConstants.SYSTOLIC_BP] = i.value!!
                "BP_DIA" -> saltData[CleverTapConstants.DIASTOLIC_BP] = i.value!!
                "STRESS_INDEX" -> {
                    saltData[CleverTapConstants.STRESS] = i.observation!!
                }
                "HEART_RATE" -> saltData[CleverTapConstants.HEART_RATE] = i.value!!
                "BREATHING_RATE" -> saltData[CleverTapConstants.BREATHING_RATE] = i.value!!
                "BLOOD_OXYGEN" -> saltData[CleverTapConstants.OXYGEN] = i.value!!
                "BMI" -> saltData[CleverTapConstants.BMI] = i.value!!
            }
        }
        CleverTapHelper.pushEventWithProperties(context,
            CleverTapConstants.YM_SCAN_VITALS_DATA_INFO,saltData)
    }

    fun logCleverTapScanVitalsInfo2(context: Context, list:MutableList<WellfieSaveVitalsModel.WellfieParameter>, date:String) {
        val saltData = HashMap<String, Any>()
        saltData[CleverTapConstants.DATE] = date
        for (i in list) {
            when (i.parameterCode) {
                "BP_SYS" -> saltData[CleverTapConstants.SYSTOLIC_BP] = i.value
                "BP_DIA" -> saltData[CleverTapConstants.DIASTOLIC_BP] = i.value
                "STRESS_INDEX" -> {
                    saltData[CleverTapConstants.STRESS] = i.observation
                }
                "HEART_RATE" -> saltData[CleverTapConstants.HEART_RATE] = i.value
                "BREATHING_RATE" -> saltData[CleverTapConstants.BREATHING_RATE] = i.value
                "BLOOD_OXYGEN" -> saltData[CleverTapConstants.OXYGEN] = i.value
                "BMI" -> saltData[CleverTapConstants.BMI] = i.value
            }
        }
        CleverTapHelper.pushEventWithProperties(context,
            CleverTapConstants.YM_SCAN_VITALS_DATA_INFO,saltData)
    }

    fun clearData() {
        instance = null
        Utilities.printLogError("Cleared Wellfie Data")
    }

    companion object {
        private var instance: WellfieSingleton? = null
        fun getInstance(): WellfieSingleton? {
            if (instance == null) {
                instance = WellfieSingleton()
            }
            return instance
        }
    }

}
