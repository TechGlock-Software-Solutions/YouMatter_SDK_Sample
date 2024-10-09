package com.techglock.health.app.common.utils

import android.content.Context
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.clevertap.android.sdk.CleverTapAPI


object CleverTapHelper {

    /*lateinit var  Utilities.preferenceUtils: PreferenceUtils

    fun initPreferenceUtils(Utilities.preferenceUtils: PreferenceUtils){
        this.Utilities.preferenceUtils= Utilities.preferenceUtils
    }*/
    fun addUser(context: Context?) {
        val profileDataModel = HashMap<String, Any>()
        profileDataModel[CleverTapConstants.NAME] = Utilities.preferenceUtils.getPreference(PreferenceConstants.FIRSTNAME)
        profileDataModel[CleverTapConstants.IDENTITY] = Utilities.preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID)
        profileDataModel[CleverTapConstants.EMAIL] = Utilities.preferenceUtils.getPreference(PreferenceConstants.EMAIL)
        profileDataModel[CleverTapConstants.PHONE] = "+91" + Utilities.preferenceUtils.getPreference(PreferenceConstants.PHONE)
        profileDataModel[CleverTapConstants.GENDER] = Utilities.getGenderCodeForCleverTap(Utilities.preferenceUtils.getPreference(PreferenceConstants.GENDER))
        val dob = DateHelper.convertStringDateToDateForCleverTap(Utilities.preferenceUtils.getPreference(PreferenceConstants.DOB), DateHelper.SERVER_DATE_YYYYMMDD)
        Utilities.printLogError("ClevertapDob--->$dob")
        profileDataModel[CleverTapConstants.DOB] = dob
        //CleverTapAPI.getDefaultInstance(context)?.onUserLogin(profileDataModel)
    }

    fun updateUser(context: Context?) {
        val profileDataModel = HashMap<String, Any>()
        profileDataModel[CleverTapConstants.NAME] = Utilities.preferenceUtils.getPreference(PreferenceConstants.FIRSTNAME)
        profileDataModel[CleverTapConstants.IDENTITY] = Utilities.preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID)
        profileDataModel[CleverTapConstants.EMAIL] = Utilities.preferenceUtils.getPreference(PreferenceConstants.EMAIL)
        profileDataModel[CleverTapConstants.PHONE] = Utilities.preferenceUtils.getPreference(PreferenceConstants.PHONE)
        profileDataModel[CleverTapConstants.GENDER] = Utilities.getGenderCodeForCleverTap(Utilities.preferenceUtils.getPreference(PreferenceConstants.GENDER))
        profileDataModel[CleverTapConstants.DOB] = DateHelper.convertStringDateToDate(Utilities.preferenceUtils.getPreference(PreferenceConstants.DOB), DateHelper.SERVER_DATE_YYYYMMDD)
        //CleverTapAPI.getDefaultInstance(context)?.pushProfile(profileDataModel)
    }

    fun pushEvent(context: Context?, eventMessage: String) {
        val eventData = HashMap<String, Any>()
        eventData[CleverTapConstants.PERSON_ID] = Utilities.preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID)
        CleverTapAPI.getDefaultInstance(context)?.pushEvent(eventMessage, eventData)
        //CleverTapAPI.getDefaultInstance(context)?.pushEvent(eventMessage)
    }

    fun pushEventWithProperties(
        context: Context?,
        eventMessage: String,
        eventData: HashMap<String, Any>, addPersonId: Boolean = true
    ) {
        if (addPersonId) {
            eventData[CleverTapConstants.PERSON_ID] = Utilities.preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID)
        }
        //CleverTapAPI.getDefaultInstance(context)?.pushEvent(eventMessage, eventData)
    }

    fun recordScreen(context: Context?, screenName: String) {
        //CleverTapAPI.getDefaultInstance(context)?.recordScreen(screenName)
    }

    fun pushError(context: Context?, errorMessage: String, errorCode: Int) {
        //CleverTapAPI.getDefaultInstance(context)?.pushError(errorMessage, errorCode)
    }
}