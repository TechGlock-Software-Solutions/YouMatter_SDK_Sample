package com.techglock.health.app.common.utils

import android.os.Bundle
import com.techglock.health.app.common.constants.Constants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object FirebaseHelper {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    init {
        firebaseAnalytics = Firebase.analytics
    }

    fun getInstance() {

    }

    fun logCustomFirebaseEvent(eventName: String) {
//        if(addPrefix) {
        firebaseAnalytics.logEvent(eventName, Bundle())
//        }else{
//            firebaseAnalytics.logEvent(eventName, Bundle())
//        }
    }

    fun logCustomFirebaseEventWithData(eventName: String, data: String) {
//        if(addPrefix) {
        var bundle = Bundle()
        bundle.putString(Constants.DATA, data)
        firebaseAnalytics.logEvent(eventName, bundle)
//        }else{
//            firebaseAnalytics.logEvent(eventName, Bundle())
//        }
    }

    fun logScreenEvent(eventName: String) {
        logCustomFirebaseEvent(eventName)
    }
}