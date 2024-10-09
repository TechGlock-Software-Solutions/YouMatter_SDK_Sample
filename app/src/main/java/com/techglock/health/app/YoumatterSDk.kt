package com.techglock.health.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.utils.AppAH
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.config_sdk.ConfigurationUtils
import com.techglock.health.app.di.DIModule
import com.techglock.health.app.di.DIModule.providePreferenceUtils
import com.techglock.health.app.home.ui.HomeMainActivity
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber
import java.util.Objects

@AndroidEntryPoint
class YoumatterSDK private constructor(private val context: Context) : AppCompatActivity() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: YoumatterSDK? = null
        fun getInstance(context: Context): YoumatterSDK {
            return instance ?: synchronized(this) {
                instance ?: YoumatterSDK(context).also { instance = it }
            }
        }
    }

    fun launchSdk(ssoData:JSONObject) {
        setSdkConfiguration()
        AndroidThreeTen.init(context)
        Timber.plant(Timber.DebugTree())
        AppAH.instance.initialize(context)
        DIModule.provideViewModelFactory()
        Utilities.initPreferenceUtils(providePreferenceUtils(context))
        val intent = Intent(context,SSOLoaderActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(Constants.DATA,ssoData.toString())
        context.startActivity(intent)
        finish()
    }

    fun launchSdkDirectly() {
        enableLogging()
        val intent = Intent(context, HomeMainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        finish()
    }

    private fun setSdkConfiguration() {
        ConfigurationUtils.getInstance().clearData()
        ConfigurationUtils.getInstance().setConfigData(context)
    }

/*      private fun enableLogging(token: String = "YXe3kMq25XX0yWNp0TUmK83Emid86ZgoemVoJz5AS9MmquqEDlMA8pNLFN2RgsRhMshatVSlUeQjZG4k4E0rGENx7T+ghDMmgjdqDVog3IiICWuAu3KyjLVSCwvTuByXxgR7YSUqjoIJ7LcIyQLDWMDGAhFkkwYqNxm8S8uzdQkWRq0vEiKbv1bhgNAmrOkDbWqai6wMoTRjmLYKEt+LFxdrUvpVk4/PofJoZy5xEwiHBMe5gJSgU3/7KjvAB9Xy+qCO45KwgOT0GyU2F18HrMcaRhLLU6hpqD3Df2Tgs8Wlte+Q9LJUIf6rH5X9vogl5LDcjJQRxWXh8FGSys3MQXHBZuREImWAfsfT2VFxzFqN//lYoNGhe62K5QjTjeLKkzed8Ium/22XBt81YYbEzR6JuTTAK1W+Zh7c5JpmMuL3QuNDmXq07keuoGGNn8+RCCH8L69SV9M3BQFpgijFV2idyoy4QLyD1evaTigN70kF9Vvxu03u1nVfyRnV4PZ92SeiFwrfde8b5mYPEGckyzN4tLOipg4KFMLHQlL9AV4zeLSzoqYOClFHmquQiCcMwmUZH70uEw2IrmeOxWmGixlN+jytOIwmyS4/UQqOoS+OX+rRtU8UKXF1giAqcHkol1YZvzo5rnd0zAlNi1PZTE1XMJC16+5MCJfepLrH1DE5werwqFOCml2UuQblFVioOcHq8KhTgprdQhO5wEAoQkcjiI/D7CdJlnLjMqz1eEQJ6ukiV0yhxRpPSPzAYpUdPbAPiOHTu2HUZ61mMH6z2gdVLaZFMI1UBe9oPo4GYEJIec7J+xoMF6mIIt/qJrzIsKApFSPZ0xE=") {
        Utilities.initPreferenceUtils(providePreferenceUtils(context))
        providePreferenceUtils(context).storePreference(PreferenceConstants.TOKEN, token)
        providePreferenceUtils(context).storePreference(PreferenceConstants.EMAIL, "rajan.gupta@techglock.com")
        providePreferenceUtils(context).storePreference(PreferenceConstants.PHONE, "7404888461")
        providePreferenceUtils(context).storePreference(PreferenceConstants.ACCOUNTID, "86833")
        providePreferenceUtils(context).storePreference(PreferenceConstants.FIRSTNAME, "Rajan Gupta")
        providePreferenceUtils(context).storePreference(PreferenceConstants.PROFILE_IMAGE_ID, "")
        providePreferenceUtils(context).storePreference(PreferenceConstants.GENDER, "1")
        providePreferenceUtils(context).storePreference(PreferenceConstants.ORG_NAME, "")
        providePreferenceUtils(context).storePreference(PreferenceConstants.ORG_EMPLOYEE_ID, "")
        providePreferenceUtils(context).storePreference(PreferenceConstants.PERSONID, "86656")
        providePreferenceUtils(context).storePreference(PreferenceConstants.ADMIN_PERSON_ID, "86656")
        providePreferenceUtils(context).storePreference(PreferenceConstants.RELATIONSHIPCODE, Constants.SELF_RELATIONSHIP_CODE)
        providePreferenceUtils(context).storePreference(PreferenceConstants.JOINING_DATE, "2024-02-05")
        providePreferenceUtils(context).storePreference(PreferenceConstants.DOB, "1994-10-30")
        providePreferenceUtils(context).storeBooleanPreference(PreferenceConstants.IS_LOGIN, true)
        providePreferenceUtils(context).storeBooleanPreference(PreferenceConstants.IS_FIRST_VISIT, false)
        providePreferenceUtils(context).storeBooleanPreference(PreferenceConstants.IS_BASEURL_CHANGED, true)
        AndroidThreeTen.init(context)
        Timber.plant(Timber.DebugTree())
        AppAH.instance.initialize(context)
        DIModule.provideViewModelFactory()
    }*/

    //PROD
/*    private fun enableLogging(token: String = "YXe3kMq25XXJucch8N8heJNdf+cKh+2GaZ5jZN1rF19tmM8I4HepkToQYQsB9zmcyIGhKenHIm5wzlHafq1onJv0za9IW//2b/X7lxpoEzLnwBvvh0PEOzN4tLOipg4KSG3upJBEqU3pYu7V9Fgxxlfd23Hh2NgCY6YdUuGbACwWRq0vEiKbv1bhgNAmrOkDbWqai6wMoTRjmLYKEt+LFxdrUvpVk4/PofJoZy5xEwiHBMe5gJSgU3/7KjvAB9Xy+qCO45KwgOT0GyU2F18HrMcaRhLLU6hpqD3Df2Tgs8Wlte+Q9LJUIf6rH5X9vogl5LDcjJQRxWXh8FGSys3MQXHBZuREImWAfsfT2VFxzFqN//lYoNGhe62K5QjTjeLKkzed8Ium/23O2c6M/G/NjBOc3qJNjC6HfA6iDziMvIr3QuNDmXq07keuoGGNn8+RLzn4AYAZNY03BQFpgijFV/v1+mubZn1W+L0s6Zn97pQF9Vvxu03u1nVfyRnV4PZ92SeiFwrfde8b5mYPEGckyzN4tLOipg4KFMLHQlL9AV4zeLSzoqYOClFHmquQiCcMRb3tq2jlr1GBwj0xlLYenBlN+jytOIwmyS4/UQqOoS+OX+rRtU8UKXF1giAqcHkol1YZvzo5rndtXX7hviUxZJETjRn1wqxf7h3cMsxCOtGgchLuI+Pb8ldJBM3Gu6inXZS5BuUVWKg5werwqFOCmt1CE7nAQChCRyOIj8PsJ0mWcuMyrPV4RAnq6SJXTKHFGk9I/MBilR09sA+I4dO7YX5d8BgjDxYwH06ROkE6HH+wJgrsEgHrTIIBiF/NVc6k1UsiSxBSujeh5jqTrZHtRyfc9/CHx3C4") {
        Utilities.initPreferenceUtils(providePreferenceUtils(context))
        providePreferenceUtils(context).storePreference(PreferenceConstants.TOKEN, token)
        providePreferenceUtils(context).storePreference(PreferenceConstants.ADMIN_PERSON_ID, "81183")
        providePreferenceUtils(context).storePreference(PreferenceConstants.PERSONID, "81183")
        providePreferenceUtils(context).storePreference(PreferenceConstants.ACCOUNTID, "81293")
        providePreferenceUtils(context).storePreference(PreferenceConstants.EMAIL, "Rohit.Gawande@sudlife.in")
        providePreferenceUtils(context).storePreference(PreferenceConstants.PHONE, "8087778555")
        providePreferenceUtils(context).storePreference(PreferenceConstants.FIRSTNAME, "Rohit Gawande")
        providePreferenceUtils(context).storePreference(PreferenceConstants.PROFILE_IMAGE_ID, "6975")
        providePreferenceUtils(context).storePreference(PreferenceConstants.GENDER, "1")
        //providePreferenceUtils(context).storePreference(PreferenceConstants.ORG_NAME, "SUDLIFE")
        //providePreferenceUtils(context).storePreference(PreferenceConstants.ORG_EMPLOYEE_ID, "37842")
        providePreferenceUtils(context).storePreference(PreferenceConstants.ORG_NAME, "")
        providePreferenceUtils(context).storePreference(PreferenceConstants.ORG_EMPLOYEE_ID, "")
        providePreferenceUtils(context).storePreference(PreferenceConstants.RELATIONSHIPCODE, Constants.SELF_RELATIONSHIP_CODE)
        providePreferenceUtils(context).storePreference(PreferenceConstants.JOINING_DATE, "2024-01-03")
        providePreferenceUtils(context).storePreference(PreferenceConstants.DOB, "1994-09-25")
        providePreferenceUtils(context).storeBooleanPreference(PreferenceConstants.IS_LOGIN, true)
        providePreferenceUtils(context).storeBooleanPreference(PreferenceConstants.IS_FIRST_VISIT, false)
        providePreferenceUtils(context).storeBooleanPreference(PreferenceConstants.IS_BASEURL_CHANGED, true)
        Utilities.setEmployeeType("BOI")
        //preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_OTP_AUTHENTICATED, true)
        //preferenceUtils.storePreference(PreferenceConstants.POLICY_MOBILE_NUMBER,"8087778555")
        AndroidThreeTen.init(context)
        Timber.plant(Timber.DebugTree())
        AppAH.instance.initialize(context)
        DIModule.provideViewModelFactory()
    }*/

    private fun enableLogging(token:String = "YXe3kMq25XXJucch8N8heJNdf+cKh+2GaZ5jZN1rF19tmM8I4HepkSD53XXvxRXeMSTiy+w4CSZB1sN+OFk4DSKeExIsxbTcx0InIUJl8GRzzxTv7zebujnB6vCoU4KaeaWrSJmfKhZ9xuuy6e6YEPMfozukeYrVE3URWU+8LRBjYrkiVLsrEdqiXFE8XFPRzZ4CakKLBJlJRG8Azw9GL/G7bLw5sRnHA7thwo05TcdZR2qfdmbxtmpjGROZi9e4+b/zOFa8eJqHFfjTOHUmgAkJEg8oN/i91mPBwDfIhehOzN9eO66GCUhi1aOvYMiUOIs1w/uWwEW0YT48dGv+Zsrmc8bsvVXjmKCATy66ZZclFnF98WRHSejf4oSzGLq6Oe+SwdDtJcz0k//9vwNz9f4FDyW7To1OLz9yCRFq601HwlppQQpKNbelBQ5RuJjyWGeJcn2WCBE70mNJDAIAEafwiJfK+2NaaL9ERTaREUBAeI3hFMeE1UefFEt0qoAhz5I38Feh9BQfyBk8bqLhSSkrD4i6F4ssQcq1qUiuKEkiZAjhS/757k064F4d1JdtdLrYk0G/7iDOlEhUc/WqFQF7sIm25kp09vi4Ne7QBTenaFUyw8HOk6IVCPJgea2wjjn1xoeN651YMO6jRZwa6qL4FO2QtXXKTVcwkLXr7kx5xWIvFSszR89g1F5aATTD/4U0VJcEqPB0zAlNi1PZTDUBniUX0IknbV1+4b4lMWSRE40Z9cKsX6QwZgB1qgXrlVa6WiFzS4bZGCyEEhLLHLVmHL4gyqduGk9I/MBilR0V1+M3ZTglAScuPf6RdQXfaL/9vsXg+/+G2sRnaCZPS4C/DeYyPxVRFjuy9BwjO0w=") {
        setSdkConfiguration()
        Utilities.initPreferenceUtils(providePreferenceUtils(context))
        providePreferenceUtils(context).storePreference(PreferenceConstants.TOKEN, token)
        providePreferenceUtils(context).storePreference(PreferenceConstants.ADMIN_PERSON_ID, "548")
        providePreferenceUtils(context).storePreference(PreferenceConstants.PERSONID, "548")
        providePreferenceUtils(context).storePreference(PreferenceConstants.ACCOUNTID, "545")
        providePreferenceUtils(context).storePreference(PreferenceConstants.EMAIL, "Rohit.Gawande@sudlife.in")
        providePreferenceUtils(context).storePreference(PreferenceConstants.PHONE, "8087778555")
        providePreferenceUtils(context).storePreference(PreferenceConstants.FIRSTNAME, "Rohit Gawande")
        providePreferenceUtils(context).storePreference(PreferenceConstants.PROFILE_IMAGE_ID, "")
        providePreferenceUtils(context).storePreference(PreferenceConstants.GENDER, "1")
        //providePreferenceUtils(context).storePreference(PreferenceConstants.ORG_NAME, "SUDLIFE")
        //providePreferenceUtils(context).storePreference(PreferenceConstants.ORG_EMPLOYEE_ID, "37842")
        providePreferenceUtils(context).storePreference(PreferenceConstants.ORG_NAME, "")
        providePreferenceUtils(context).storePreference(PreferenceConstants.ORG_EMPLOYEE_ID, "")
        providePreferenceUtils(context).storePreference(PreferenceConstants.RELATIONSHIPCODE, Constants.SELF_RELATIONSHIP_CODE)
        providePreferenceUtils(context).storePreference(PreferenceConstants.JOINING_DATE, "2024-06-18")
        providePreferenceUtils(context).storePreference(PreferenceConstants.DOB, "1995-10-25")
        providePreferenceUtils(context).storeBooleanPreference(PreferenceConstants.IS_LOGIN, true)
        providePreferenceUtils(context).storeBooleanPreference(PreferenceConstants.IS_FIRST_VISIT, false)
        providePreferenceUtils(context).storeBooleanPreference(PreferenceConstants.IS_BASEURL_CHANGED, true)
        Utilities.setEmployeeType("BOI")
        //preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_OTP_AUTHENTICATED, true)
        //preferenceUtils.storePreference(PreferenceConstants.POLICY_MOBILE_NUMBER,"8087778555")
        AndroidThreeTen.init(context)
        Timber.plant(Timber.DebugTree())
        AppAH.instance.initialize(context)
        DIModule.provideViewModelFactory()
    }

    private fun checkDeepLink(appContext: Context) {
        Utilities.printLogError("checkDeepLink")
        val appsflyer = AppsFlyerLib.getInstance()

        appsflyer.subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(deepLinkResult: DeepLinkResult) {
                when (deepLinkResult.status) {
                    DeepLinkResult.Status.FOUND -> {
                        Utilities.printLogError("DeepLinkResult Found")
                    }
                    DeepLinkResult.Status.NOT_FOUND -> {
                        Utilities.printLogError("DeepLinkResult Not Found")
                        return
                    }
                    else -> {
                        val dlError = deepLinkResult.error
                        Utilities.printLogError("There was an error getting Deep Link data: $dlError")
                        if ( deepLinkResult.error.toString() == "NETWORK" ) {
                            // Retry the deep link operation after a delay
                            // You can implement a retry mechanism here
                        } else {
                            // Handle other error scenarios
                        }
                        return
                    }
                }
                val deepLinkObj: DeepLink = deepLinkResult.deepLink

                // An example for using is_deferred
                if (deepLinkObj.isDeferred == true) {
                    Utilities.printLogError("This is a deferred deep link")
                } else {
                    Utilities.printLogError("This is a direct deep link")
                }

                try {
                    val deepLinkValue = deepLinkObj.deepLinkValue
                    Utilities.printLogError("DeepLinkValue====> $deepLinkValue")
                    if(!deepLinkValue.isNullOrEmpty() ) {
                        val dlObject = deepLinkObj.clickEvent
                        var deepLinkSub1 = ""
                        var deepLinkSub2 = ""
                        if( dlObject.has(Constants.DEEP_LINK_SUB1) ) {
                            deepLinkSub1 = dlObject.getString(Constants.DEEP_LINK_SUB1)
                        }
                        if( dlObject.has(Constants.DEEP_LINK_SUB2) ) {
                            deepLinkSub2 = dlObject.getString(Constants.DEEP_LINK_SUB2)
                        }
                        val campaignData = HashMap<String, Any>()
                        campaignData[CleverTapConstants.CAMPAIGN_NAME] = "$deepLinkValue"
                        if( !Utilities.isNullOrEmpty(deepLinkSub1) ) {
                            campaignData[CleverTapConstants.ADDITIONAL_PARAMETER_1] = deepLinkSub1
                        }
                        if( !Utilities.isNullOrEmpty(deepLinkSub2) ) {
                            campaignData[CleverTapConstants.ADDITIONAL_PARAMETER_2] = deepLinkSub2
                        }
                        CleverTapHelper.pushEventWithProperties(appContext,CleverTapConstants.AF_CAMPAIGN,campaignData,false)

                        if( deepLinkValue == Constants.DEEP_LINK_REFERRAL ) {
                            var referralName = ""
                            var referralPID = ""
                            if ( !Utilities.isNullOrEmpty(deepLinkSub1) && !Utilities.isNullOrEmpty(deepLinkSub2) ) {
                                referralName = deepLinkSub1
                                referralPID = deepLinkSub2
                                Utilities.setReferralDetails(referralName,referralPID)
                                Utilities.printLogError("Referral Name ====> $referralName")
                                Utilities.printLogError("Referral PID ====> $referralPID")
                                if( !Utilities.getLoginStatus() ) {
                                    val data = HashMap<String, Any>()
                                    data[CleverTapConstants.REFERRAL_NAME] = referralName
                                    data[CleverTapConstants.REFERRAL_PID] = referralPID
                                    CleverTapHelper.pushEventWithProperties(appContext,CleverTapConstants.INSTALLATION_BY_REFERRAL,data,false)
                                }
                            } else {
                                Utilities.printLogError("======= Referral Details Not Found =========")
                            }
                        } else if( deepLinkValue == Constants.DEEP_LINK_DARWINBOX ) {
                            if ( !Utilities.isNullOrEmpty(deepLinkSub1) ) {
                                val darwinBoxUrl =  deepLinkSub1
                                Utilities.printLogError("From Darwinbox")
                                Utilities.printLogError("darwinBoxUrl--->\n$darwinBoxUrl")
                                Utilities.setDarwinBoxDetails(darwinBoxUrl)
                            }
                        } else if( !Utilities.isNullOrEmpty(deepLinkSub1)
                            && deepLinkSub1 == Constants.DEEP_LINK_APP_FEATURE_CAMPAIGN ) {
                            Utilities.printLogError("From Campaign : $deepLinkValue")
                            if( !Utilities.isNullOrEmpty(deepLinkSub2) ) {
                                Utilities.setCampaignFeatureDetails(deepLinkSub2)
                            }
                            Utilities.printLogError("deep_link_sub1 : $deepLinkSub1")
                            Utilities.printLogError("deep_link_sub2 : $deepLinkSub2")
                        }
                    }
                } catch (e:Exception) {
                    return
                }
            }
        })

        val conversionListener: AppsFlyerConversionListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(conversionData: Map<String, Any>) {
                for (attrName in conversionData.keys) {
                    Utilities.printLogError("Conversion attribute: " + attrName + " = " + conversionData[attrName])
                }
                val status: String = Objects.requireNonNull(conversionData["af_status"]).toString()
                if (status == "Non-organic") {
                    if (Objects.requireNonNull(conversionData["is_first_launch"]).toString().equals("true")) {
                        Utilities.printLogError("Conversion: First Launch")
                    } else {
                        Utilities.printLogError("Conversion: Not First Launch")
                    }
                } else {
                    Utilities.printLogError("Conversion: This is an organic install.")
                }
            }

            override fun onConversionDataFail(errorMessage: String) {
                Utilities.printLogError("error getting conversion data: $errorMessage")
            }

            override fun onAppOpenAttribution(attributionData: Map<String, String>) {
                Utilities.printLogError("onAppOpenAttribution: This is fake call.")
            }

            override fun onAttributionFailure(errorMessage: String) {
                Utilities.printLogError("error onAttributionFailure : $errorMessage")
            }
        }

        appsflyer.init(getString(R.string.AppsFlyerDevKey),conversionListener, appContext)
        appsflyer.setAppInviteOneLink("UtYy")
        appsflyer.start(appContext)
    }

}


