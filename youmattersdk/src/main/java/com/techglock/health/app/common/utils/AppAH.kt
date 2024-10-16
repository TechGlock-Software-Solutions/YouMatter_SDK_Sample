package com.techglock.health.app.common.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import com.google.firebase.analytics.FirebaseAnalytics

class AppAH private constructor() : Application() {

    var mContext: Context? = null
    var currentActivity: Activity? = null

    fun setCurrActivity(activity: Activity?) {
        currentActivity = activity
    }

    fun getCurrActivity(): Activity? {
        //return currentActivity
        return instance.getCurrentActivityInstance()
    }

    fun getCurrentActivityInstance(): Activity? {
        return currentActivity
    }

    fun getActivityContextInstance(): Context {
        return currentActivity!!
    }

    override fun getApplicationContext(): Context {
        return if (mContext != null) {
            mContext!!
        } else {
            (mAppContext)!!
        }
    }

    fun initialize(app: Context?) {
        // System.gc();
        if (app != null) {
            mContext = app
        }
    }

    fun clearReferences(activity: Activity) {
        if (currentActivity != null && currentActivity == activity) currentActivity = null
    }

    fun handleUncaughtException(thread: Thread?, e: Throwable) {
        e.printStackTrace() // not all Android versions will print the stack trace automatically
        val intent = Intent()
        intent.action = "com.mydomain.SEND_LOG" // see step 5.
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // required when starting from Application
        startActivity(intent)
        System.exit(1) // kill off the crashed app
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        //MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
        mAppContext = this
    }

    companion object {
        var mAppContext: Context? = null
        private var sAnalytics: FirebaseAnalytics? = null

        val instance = AppAH()

        fun getCurrentActivity(): Activity? {
            return instance.getCurrentActivityInstance()
        }

        val activityContext: Context
            get() = instance.getActivityContextInstance()
        val context: Context?
            get() = if (instance.mContext != null) {
                instance.mContext
            } else {
                mAppContext
            }

    }
}