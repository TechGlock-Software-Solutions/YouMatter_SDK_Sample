package com.techglock.health.app.common.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.preference.PreferenceManager
import java.util.Locale

object LocaleHelper {

    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"
    fun onAttach(context: Context): Context {
        val lang = getPersistedData(context, "en")
        //Log.e("","LocaleHelper: DefaultLocal---> $lang")
        return setLocale(context, lang)
    }

    fun onAttach(context: Context, defaultLanguage: String): Context {
        val lang = getPersistedData(context, defaultLanguage)
        return setLocale(context, lang)
    }

    fun getLanguage(context: Context): String {
        return getPersistedData(context, "en")
    }

    // the method is used to set the language at runtime
    fun setLocale(context: Context?, language: String): Context {
        persist(context!!, language)

        // updating the language for devices above android nougat
        return updateResources(context, language)
        /*if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {

            //Log.e("","setLocale : updateResources")
            updateResources(context, language)
        } else {

            // for devices having lower version of android os
            //Log.e("","setLocale : updateResourcesLegacy")
            updateResourcesLegacy(context, language)
        }*/
    }

    fun setFragLocale(context: Context?, language: String): Context {
        persist(context!!, language)
        return updateResourcesLegacy(context, language)
    }

    private fun getPersistedData(context: Context, defaultLanguage: String): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage)!!
    }

    private fun persist(context: Context, language: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString(SELECTED_LANGUAGE, language)
        editor.apply()
    }

    // the method is used update the language of application by creating
    // object of inbuilt Locale class and passing language argument to it
    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)
    }

    private fun updateResourcesLegacy(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        configuration.setLayoutDirection(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }

    fun getLocalizedResources(context: Context, desiredLocale: Locale?): Resources? {
        var conf: Configuration = context.resources.configuration
        conf = Configuration(conf)
        conf.setLocale(desiredLocale)
        val localizedContext = context.createConfigurationContext(conf)
        return localizedContext.resources
    }

}