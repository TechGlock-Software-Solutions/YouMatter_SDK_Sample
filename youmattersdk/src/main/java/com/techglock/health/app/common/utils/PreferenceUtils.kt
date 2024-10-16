package com.techglock.health.app.common.utils

import android.content.SharedPreferences
import javax.inject.Inject


class PreferenceUtils @Inject constructor(private val sharedPreference: SharedPreferences) {

    fun storePreference(key: String, value: String) {
        sharedPreference.edit().putString(key, EncryptionUtility.encrypt(value)).apply()
    }

    fun getPreference(key: String, defValue: String = ""): String {
        var value = ""
        value = sharedPreference.getString(key, "")!!
        if (value.trim().isNotEmpty()) {
            value = EncryptionUtility.decrypt(value)
        }
        return value
    }

    fun storeBooleanPreference(key: String, value: Boolean) {
        sharedPreference.edit().putBoolean(key, value).apply()
    }

    fun getBooleanPreference(key: String, defValue: Boolean = false): Boolean {
        return sharedPreference.getBoolean(key, defValue)
    }

    fun clearPreference(key: String) {
        sharedPreference.edit().putString(key, "").apply()
    }

    fun resetPreferenceWithZero(key: String) {
        sharedPreference.edit().putString(key, "0").apply()
    }

    /*    fun storeBooleanPreference(key: String, value: Boolean) {
            sharedPreference.edit().putString(key, EncryptionUtility.encrypt(BuildConfig.SECURITY_KEY, value.toString(), BuildConfig.SECURITY_KEY)).apply()
        }*/

    /*    fun getBooleanPreference(key: String, defValue: Boolean = false): Boolean {
            var value = ""
            value = sharedPreference.getString(key, "")!!

            if (value.trim().isNotEmpty()) {
                value = EncryptionUtility.decrypt(value)
            }
            return value.toBoolean()
        }*/

    /*    fun storePreference(key:String,value: String ) {
        sharedPreference.edit().putString(key,value).apply()
    }

    fun getPreference(key:String): String {
        return sharedPreference.getString(key, "")!!
    }*/

}