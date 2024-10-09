package com.techglock.health.app.config_sdk

import android.annotation.SuppressLint
import android.content.Context
import com.techglock.health.app.common.utils.Utilities
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ConfigurationUtils private constructor() {

    var sdkConfigData = SdkConfig()

    init {
        sdkConfigData = SdkConfig()
    }

    fun getConfigData() : SdkConfig {
        Utilities.printData("getConfigData",sdkConfigData,true)
        return sdkConfigData
    }

    fun setConfigData(context: Context): SdkConfig {
        sdkConfigData = SdkConfig()
        val jsonString: String? = loadConfigFileFromAsset(context)
        Utilities.printLogError("loadConfigFileFromAsset--->$jsonString")
        if (jsonString.isNullOrEmpty()) {
            return sdkConfigData
        } else {
            try {
                val obj = JSONObject(jsonString)
                val configObject = obj.getJSONObject("config")
                if (configObject.has("sdk_environment")) {
                    val env = configObject.getString("sdk_environment")
                    if ( !Utilities.isNullOrEmpty(env) ) {
                        when(env.uppercase()) {
                            "PROD" -> {
                                sdkConfigData.environment = "PROD"
                                sdkConfigData.apiBaseUrl = "https://prodapi.youmatterhealth.com/"
                                sdkConfigData.staticBaseUrl = "https://youmatterhealth.com/"
                                sdkConfigData.saltUrl = "https://app.salt.one/external/sud/personality/v1/sud"
                                sdkConfigData.partnerCode = "YOUMATTER"
                                sdkConfigData.ivKey = byteArrayOf(0x53, 0x33, 0x44, 0x53, 0x38, 0x43, 0x30, 0x52, 0x31, 0x54, 0x59, 0x40, 0x32, 0x30, 0x32, 0x32)
                            }
                            "UAT" -> {
                                sdkConfigData.environment = "UAT"
                                sdkConfigData.apiBaseUrl = "https://coreuat.youmatterhealth.com/"
                                sdkConfigData.staticBaseUrl = "https://webuat.youmatterhealth.com/"
                                sdkConfigData.saltUrl = "https://app.mysaltapp.net/external/sud/personality/v1/sud"
                                sdkConfigData.partnerCode = "SUDLIFE"
                                sdkConfigData.ivKey = byteArrayOf(0x41, 0x31, 0x48, 0x53, 0x38, 0x43, 0x55, 0x52, 0x31, 0x54, 0x59, 0x40, 0x39, 0x38, 0x31, 0x32)
                            }
                        }
                    }
                }
                Utilities.printData("setConfigData",sdkConfigData,true)
                return sdkConfigData
            } catch (e: JSONException) {
                e.printStackTrace()
                return sdkConfigData
            }
        }
    }

    fun loadConfigFileFromAsset(context: Context): String? {
        val json: String? = try {
            val inputStream = context.assets.open("youmatter-services.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, charset("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    /*    companion object {
            private var instance: ConfigurationUtils? = null
            fun getInstance(): ConfigurationUtils? {
                if (instance == null) {
                    instance = ConfigurationUtils()
                }
                return instance
            }
        }*/

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: ConfigurationUtils? = null
        fun getInstance(): ConfigurationUtils {
            return instance ?: synchronized(this) {
                instance ?: ConfigurationUtils().also { instance = it }
            }
        }
    }

    fun clearData() {
        instance = null
        Utilities.printLogError("Cleared ConfigData")
    }

}