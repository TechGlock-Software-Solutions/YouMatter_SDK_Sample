package com.example.sampleapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {
        var appContext: Application? = null
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        //YoumatterSDK.getInstance(appContext!!).checkDeepLink(appContext!!)
    }


}