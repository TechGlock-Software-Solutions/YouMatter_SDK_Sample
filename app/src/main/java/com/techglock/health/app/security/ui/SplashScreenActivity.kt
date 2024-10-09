package com.techglock.health.app.security.ui

//import com.caressa.allizhealth.app.model.tempconst.Configuration
//import com.caressa.allizhealth.app.model.tempconst.Configuration.EntityID
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Configuration
import com.techglock.health.app.common.constants.Configuration.EntityID
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.showDialog
import com.techglock.health.app.databinding.ActivitySplashScreenBinding
import com.techglock.health.app.repository.utils.Resource
import com.techglock.health.app.security.adapter.EmployerAdapter
import com.techglock.health.app.security.model.EmployerModel
import com.techglock.health.app.security.ui_dialog.DialogEmployee
import com.techglock.health.app.security.ui_dialog.DialogUpdateAppSecurity
import com.techglock.health.app.security.util.RootUtil.isDeviceRooted
import com.techglock.health.app.security.viewmodel.StartupViewModel
import com.google.firebase.FirebaseApp
import com.scottyab.rootbeer.RootBeer
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class SplashScreenActivity : BaseActivity(), DefaultNotificationDialog.OnDialogValueListener,
    DialogUpdateAppSecurity.OnSkipUpdateListener, EmployerAdapter.EmployerSelectionListener {

    private lateinit var binding: ActivitySplashScreenBinding

    private val viewModel: StartupViewModel by lazy {
        ViewModelProvider(this)[StartupViewModel::class.java]
    }

    private lateinit var rootBeer: RootBeer
    private var isRooted = false
    private var from = ""

    private var dialogUpdateApp: DialogUpdateAppSecurity? = null
    private var dialogEmployee: DialogEmployee? = null
    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
        get() =
            @RequiresApi(Build.VERSION_CODES.P)
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    viewModel.callCheckAppUpdateApi()
                }


                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errorCode, errString)
                    finish()
                }
            }

    override fun getViewModel(): BaseViewModel = viewModel


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateEvent(savedInstanceState: Bundle?) {
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (intent.hasExtra(Constants.FROM)) {
            from = intent.getStringExtra(Constants.FROM)!!
        }
        Utilities.printLogError("SplashScreenActivity:from--->$from")
        //subscribeForUDL()

        //val animation = AnimationUtils.loadAnimation(this, R.anim.anim_pulse)
        //binding.appLogo.startAnimation(animation)
        //binding.appLogo.visibility = View.VISIBLE

        val logoDesc =
            "<a>" + "<a><B><font color='#81A684'>${resources.getString(R.string.SPLASH_DESC1)}</font></B><br/><B><font color='#E8988D'>${
                resources.getString(R.string.SPLASH_DESC2)
            } - 2023</font></B>" + "</a>"
        binding.txtLogoDesc.text = Html.fromHtml(logoDesc)

        rootBeer = RootBeer(this)


        if (Constants.environment.equals("PROD")) {
            if (rootBeer.isRooted || rootBeer.isRootedWithBusyBoxCheck || this.isDeviceRooted) {
                //we found indication of root
                isRooted = true/*showDialog(
                    listener = this,
                    title = resources.getString(R.string.WARNING),
                    message = resources.getString(R.string.WARNING_DESC),
                    showDismiss = false,
                    showLeftBtn = false
                )*/
//                showToast(resources.getString(R.string.WARNING_DESC))
                showWebDialog(
                    resources.getString(R.string.WARNING),
                    resources.getString(R.string.WARNING_DESC)
                )
                finishAffinity()
            } /*else if (this.isDeveloperModeEnabled()) {
                isRooted = true*//*showDialog(
                    listener = this,
                    title = this.getString(R.string.KINDLY_DISABLE_THE_DEVELOPER_MODE_ON_YOUR_DEVICE),
                    message = this.getString(R.string.FOR_SECURITY_REASONS_YOU_CANNOT_USE_OUR_APPLICATION_IF_THE_DEVELOPER_MODE_IS_ENABLED_ON_YOUR_DEVICE),
                    showDismiss = false,
                    showLeftBtn = false
                )*//*
//                showToast(this.getString(R.string.FOR_SECURITY_REASONS_YOU_CANNOT_USE_OUR_APPLICATION_IF_THE_DEVELOPER_MODE_IS_ENABLED_ON_YOUR_DEVICE))
                showWebDialog(this.getString(R.string.KINDLY_DISABLE_THE_DEVELOPER_MODE_ON_YOUR_DEVICE),this.getString(R.string.FOR_SECURITY_REASONS_YOU_CANNOT_USE_OUR_APPLICATION_IF_THE_DEVELOPER_MODE_IS_ENABLED_ON_YOUR_DEVICE))
                finishAffinity()
            }*/ else {
                initFirebase()
            }
        } else {
            initFirebase()
        }
    }

    private fun showWebDialog(title: String, message: String) {
        val htmlContent = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Disable Developer Mode</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            background-color: #f4f4f4;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "\n" +
                "        .container {\n" +
                "            width: 80%;\n" +
                "            max-width: 1000px;\n" +
                "            margin: 50px auto;\n" +
                "            padding: 30px;\n" +
                "            background-color: #fff;\n" +
                "            border-radius: 10px;\n" +
                "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n" +
                "        }\n" +
                "\n" +
                "        .logo {\n" +
                "            text-align: center;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .logo img {\n" +
                "            max-width: 200px;\n" +
                "            height: auto;\n" +
                "            padding: 10px;\n" +
                "        }\n" +
                "\n" +
                "        h1 {\n" +
                "            color: #333;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "\n" +
                "        p {\n" +
                "            color: #666;\n" +
                "            text-align: center;\n" +
                "            font-size: 24px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<div class=\"container\">\n" +
                "    <div class=\"logo\">\n" +
                "        <img src=\"https://youmatterhealth.com/dcf4b32b85fc8754e5cee22017006868.jpg\" alt=\"Logo\">\n" +
                "    </div>\n" +
                "    <h1>$title</h1>\n" +
                "    <p>$message</p>\n" +
                "</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>\n".trimIndent()


        val file = File.createTempFile("Disable Developer Mode", ".html", cacheDir)
        file.writeText(htmlContent)

        val uri = FileProvider.getUriForFile(this, this.packageName + ".provider", file)

        val browserIntent = Intent(Intent.ACTION_VIEW)
        browserIntent.setDataAndType(uri, "text/html")
        browserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(browserIntent)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initFirebase() {
        isRooted = false
        FirebaseApp.initializeApp(applicationContext)
        Configuration.EntityID = viewModel.getMainUserPersonID().ifEmpty { "0" }
        if (viewModel.getLoginStatus()) {
            //if (viewModel.getBaseurlChangedStatus() || !viewModel.isFirstTimeUser() ) {
            if (viewModel.getBaseurlChangedStatus()) {
                //viewModel.callCheckAppUpdateApi()
                if (Utilities.checkBiometricSupport(this)) {
                    if (viewModel.isBiometricAuthentication()) {
                        when (from) {
                            Constants.LOGIN, Constants.LOGIN_WITH_OTP -> {
                                //viewModel.callCheckAppUpdateApi()
                                if (viewModel.getEmployeeId()
                                        .isNotEmpty() && viewModel.getOrgName() == Constants.SUD_ORG_NAME
                                ) {
                                    Utilities.setEmployeeType(Constants.SUD_LIFE)
                                    Utilities.logCleverTapEmployeeEventLogin(
                                        this,
                                        Constants.SUD_LIFE
                                    )
                                    viewModel.callCheckAppUpdateApi()
                                } else {
                                    showIsEmployeeDialog()
                                }
                            }

                            Constants.SIGN_UP_NEW -> {
                                viewModel.callCheckAppUpdateApi()
                            }

                            else -> showBiometricLock()
                        }
                    } else {
                        //viewModel.callCheckAppUpdateApi()
                        when (from) {
                            Constants.LOGIN, Constants.LOGIN_WITH_OTP -> {
                                if (viewModel.getEmployeeId()
                                        .isNotEmpty() && viewModel.getOrgName() == Constants.SUD_ORG_NAME
                                ) {
                                    Utilities.setEmployeeType(Constants.SUD_LIFE)
                                    Utilities.logCleverTapEmployeeEventLogin(
                                        this,
                                        Constants.SUD_LIFE
                                    )
                                    viewModel.callCheckAppUpdateApi()
                                } else {
                                    showIsEmployeeDialog()
                                }
                            }

                            else -> viewModel.callCheckAppUpdateApi()
                        }
                    }
                } else {
                    //viewModel.callCheckAppUpdateApi()
                    when (from) {
                        Constants.LOGIN, Constants.LOGIN_WITH_OTP -> {
                            if (viewModel.getEmployeeId()
                                    .isNotEmpty() && viewModel.getOrgName() == Constants.SUD_ORG_NAME
                            ) {
                                Utilities.setEmployeeType(Constants.SUD_LIFE)
                                Utilities.logCleverTapEmployeeEventLogin(this, Constants.SUD_LIFE)
                                viewModel.callCheckAppUpdateApi()
                            } else {
                                showIsEmployeeDialog()
                            }
                        }

                        else -> viewModel.callCheckAppUpdateApi()
                    }
                }
            } else {
                viewModel.setBaseurlChangedStatus(true)
                viewModel.logoutFromDB()
                if (Utilities.logout(this, this)) {
                    openAnotherActivity(destination = NavigationConstants.LOGIN, clearTop = true)
                }
            }
        } else {
            viewModel.callCheckAppUpdateApi()
//            proceedInApp()
        }
        registerObserver()
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun registerObserver() {
        viewModel.checkAppUpdate.observe(this) {

            if (it.status == Resource.Status.SUCCESS) {
                if (it.data != null) {
                    Utilities.printData("UpdateData", it.data, true)
                    if (it.data.result.appVersion.isNotEmpty()) {
                        val versionDetails = it.data.result.appVersion[0]
                        val currentVersion = versionDetails.currentVersion!!.toDouble().toInt()
                        val forceUpdate = versionDetails.forceUpdate

                        val existingVersion = Utilities.getAppVersion(this)
                        Utilities.printLogError("CurrentVersion---->$currentVersion")
                        Utilities.printLogError("ExistingVersion--->$existingVersion")
                        if (existingVersion < currentVersion) {
                            if (forceUpdate) {
                                viewModel.logoutFromDB()
                                if (Utilities.logout(this, this)) {
                                    openAnotherActivity(
                                        destination = NavigationConstants.LOGIN,
                                        clearTop = true
                                    ) {
                                        putString(Constants.FROM, Constants.LOGOUT)
                                        putBoolean(
                                            Constants.FORCEUPDATE,
                                            versionDetails.forceUpdate
                                        )
                                        putString(
                                            Constants.DESCRIPTION,
                                            versionDetails.description!!
                                        )
                                    }
                                }
                            } else {
                                dialogUpdateApp =
                                    DialogUpdateAppSecurity(this, versionDetails, this)
                                dialogUpdateApp!!.show()
                            }
                        } else {
                            Handler(Looper.getMainLooper()).postDelayed({
                                proceedInApp()
                            }, (Constants.SPLASH_ANIM_DELAY_IN_MS).toLong())
                        }
                    }
                }
            }


            if (it.status == Resource.Status.ERROR) {
                //Utilities.printLog("ERROR--->${it.errorMessage} :: ${it.errorNumber}")
                if (it.errorNumber.equals("0", true)) {
                    showDialog(
                        listener = this,
                        title = resources.getString(R.string.AWW_SNAP),
                        message = resources.getString(R.string.UNEXPECTED_ERROR),
                        showLeftBtn = false
                    )
                }
            }

        }

        viewModel.darwinBoxData.observe(this) { }
        viewModel.isLogin.observe(this) {}
        viewModel.isRegister.observe(this) {}
        viewModel.addFeatureAccessLog.observe(this) {}
    }

    private fun proceedInApp() {
        Utilities.preferenceUtils.storeBooleanPreference(Constants.BANNER_AD, false)
        try {
            //Configuration.LanguageCode = LocaleHelper.getLanguage(this)
            EntityID = viewModel.getMainUserPersonID().ifEmpty { "0" }
//            LocaleHelper.setLocale(this,Configuration.LanguageCode)

            if (intent.hasExtra(Constants.SCREEN)) {
                val screen = intent.getStringExtra(Constants.SCREEN)
                Utilities.printLogError("Screen(SplashActivity)--->$screen")
                val launchIntent = Intent()
                launchIntent.putExtra(Constants.SCREEN, screen)
                launchIntent.putExtra(Constants.NOTIFICATION_ACTION, intent.getStringExtra(Constants.NOTIFICATION_ACTION))
                launchIntent.putExtra(Constants.NOTIFICATION_TITLE, intent.getStringExtra(Constants.NOTIFICATION_TITLE))
                launchIntent.putExtra(Constants.NOTIFICATION_MESSAGE, intent.getStringExtra(Constants.NOTIFICATION_MESSAGE))
                launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                //onClick.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                when (screen) {

                    "HRA", "FAMILY_HRA", "HRA_THREE" -> {
                        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HRA_HOME)
                    }

                    "STEPS", "STEPS_DAILY_TARGET", "STEPS_WEEKLY_SYNOPSIS" -> {
                        if (viewModel.isSelfUser()) {
                            launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.FITNESS_HOME)
                        } else {
                            launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
                        }
                    }

                    "SMART_PHONE", "HEART_AGE_CALC", "DIABETES_CALCULATOR", "DASS_21", "HTN_CALC" -> {
                        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.TOOLS_CALCULATORS_HOME)
                    }

                    "VITALS" -> {
                        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.TRACK_PARAMETER_HOME)
                    }

                    "BLOG" -> {
                        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.BLOGS_HOME)
                    }

                    "FAMILY_MEMBER_ADD" -> {
                        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.FAMILY_PROFILE)
                    }

                    "HEALTHTIPS" -> {
                        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
                    }

                    "WATER_REMINDER", "WATER_REMINDER_21_POSITIVE", "WATER_REMINDER_21_NEGATIVE" -> {
                        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.WATER_TRACKER_HOME)
                    }

                    Constants.SCREEN_FEATURE_CAMPAIGN -> {
                        if (intent.hasExtra(Constants.DEEP_LINK)) {
                            val deepLink = intent.getStringExtra(Constants.DEEP_LINK)
                            val deepLinkUri = Uri.parse(deepLink)
                            var deepLinkValue = ""
                            var deepLinkSub1 = ""
                            var deepLinkSub2 = ""
                            deepLinkValue = deepLinkUri.getQueryParameter(Constants.DEEP_LINK_VALUE)!!
                            deepLinkSub1 = deepLinkUri.getQueryParameter(Constants.DEEP_LINK_SUB1)!!
                            deepLinkSub2 = deepLinkUri.getQueryParameter(Constants.DEEP_LINK_SUB2)!!
                            Utilities.printLogError("deepLinkValue : $deepLinkValue")
                            Utilities.printLogError("deep_link_sub1 : $deepLinkSub1")
                            Utilities.printLogError("deep_link_sub2 : $deepLinkSub2")
                            if ( !Utilities.isNullOrEmpty(deepLinkValue) ) {
                                val campaignData = HashMap<String, Any>()
                                campaignData[CleverTapConstants.CAMPAIGN_NAME] = deepLinkValue
                                if( !Utilities.isNullOrEmpty(deepLinkSub1) ) {
                                    campaignData[CleverTapConstants.ADDITIONAL_PARAMETER_1] = deepLinkSub1
                                }
                                if( !Utilities.isNullOrEmpty(deepLinkSub2) ) {
                                    campaignData[CleverTapConstants.ADDITIONAL_PARAMETER_2] = deepLinkSub2
                                }
                                campaignData[CleverTapConstants.FROM_NOTIFICATION] = CleverTapConstants.YES
                                CleverTapHelper.pushEventWithProperties(applicationContext,
                                    CleverTapConstants.AF_CAMPAIGN,campaignData,false)
                            }
                            if ( !Utilities.isNullOrEmpty(deepLinkSub1)
                                && !Utilities.isNullOrEmpty(deepLinkSub2)
                                && deepLinkSub1 == Constants.DEEP_LINK_APP_FEATURE_CAMPAIGN ) {
                                Utilities.setCampaignFeatureDetails(deepLinkSub2)
                            }
                        }
                        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
                    }

                    "DASHBOARD" -> {
                        launchIntent.putExtra(Constants.REDIRECT_LINK, intent.getStringExtra(Constants.REDIRECT_LINK))
                        launchIntent.putExtra(Constants.MODULE_CODE, intent.getStringExtra(Constants.REDIRECT_LINK)!!.uppercase())
                        launchIntent.putExtra(Constants.TITLE, resources.getString(R.string.CONSULT_WITH_THERAPIST))
                        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.AMAHA_WEB_VIEW_SCREEN)
                    }

                    "APP_UPDATE" -> {
                        Utilities.goToPlayStore(this)
                        finishAffinity()
                    }

                    "SPIRITUAL_WELLNESS" -> {
                        launchIntent.putExtra(Constants.SCREEN, intent.getStringExtra(Constants.SCREEN))
                        launchIntent.putExtra(Constants.DATA_ID, intent.getStringExtra(Constants.DATA_ID))
                        launchIntent.putExtra(Constants.WEB_URL, intent.getStringExtra(Constants.WEB_URL))
                        launchIntent.putExtra(Constants.NOTIFICATION_TITLE, title)
                        launchIntent.putExtra(Constants.NOTIFICATION_MESSAGE, intent.getStringExtra(Constants.NOTIFICATION_MESSAGE))
                        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.FEED_UPDATE_SCREEN)
                    }

                    "WEB_URL_NOTIFICATION" -> {
                        when (intent.getStringExtra(Constants.REDIRECT_TYPE)) {
                            "EXTERNAL" -> {
                                Utilities.redirectToChrome(intent.getStringExtra(Constants.WEB_URL)!!, this)
                                return
                            }

                            "INTERNAL" -> {
                                launchIntent.putExtra(Constants.SCREEN, intent.getStringExtra(Constants.SCREEN))
                                launchIntent.putExtra(Constants.REDIRECT_TYPE, intent.getStringExtra(Constants.REDIRECT_TYPE))
                                launchIntent.putExtra(Constants.WEB_URL, intent.getStringExtra(Constants.WEB_URL))
                                launchIntent.putExtra(Constants.NOTIFICATION_TITLE, intent.getStringExtra(Constants.NOTIFICATION_TITLE))
                                launchIntent.putExtra(Constants.NOTIFICATION_MESSAGE, intent.getStringExtra(Constants.NOTIFICATION_MESSAGE))
                                launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.GENERAL_WEB_NOTIFICATION_SCREEN)
                            }

                            else -> {
                                launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
                            }
                        }
                    }

                    else -> {
                        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
                    }
                }
                startActivity(launchIntent)
            } else if (intent.hasExtra(Constants.FROM) && intent.getStringExtra(Constants.FROM)
                    .equals(Constants.NOTIFICATION_ACTION, ignoreCase = true)) {
                /*FirebaseHelper.logCustomFirebaseEventWithData(
                    FirebaseConstants.NOTIFICATION_CLICK,
                    Constants.MEDICATION + " :: " + intent.getStringExtra(Constants.MEDICATION_ID)!!
                            + " :: " + intent.getStringExtra(Constants.SCHEDULE_TIME)!!
                            + " :: " + intent.getStringExtra(Constants.PERSON_ID)!!
                )*/
                val launchIntent = Intent()
                launchIntent.putExtra(Constants.FROM, Constants.NOTIFICATION_ACTION)
                launchIntent.putExtra(Constants.DATE, intent.getStringExtra(Constants.DATE))
                launchIntent.putExtra(Constants.NOTIFICATION_ACTION, Constants.MEDICATION)
                launchIntent.putExtra(Constants.PERSON_ID, intent.getStringExtra(Constants.PERSON_ID))
                launchIntent.putExtra(Constants.MEDICINE_NAME, intent.getStringExtra(Constants.MEDICINE_NAME))
                launchIntent.putExtra(Constants.DOSAGE, intent.getStringExtra(Constants.DOSAGE))
                launchIntent.putExtra(Constants.INSTRUCTION, intent.getStringExtra(Constants.INSTRUCTION))
                launchIntent.putExtra(Constants.SCHEDULE_TIME, intent.getStringExtra(Constants.SCHEDULE_TIME))
                launchIntent.putExtra(Constants.MEDICATION_ID, intent.getStringExtra(Constants.MEDICATION_ID))
                launchIntent.putExtra(Constants.MEDICINE_IN_TAKE_ID, 0)
                launchIntent.putExtra(Constants.SERVER_SCHEDULE_ID, intent.getStringExtra(Constants.SERVER_SCHEDULE_ID))
                launchIntent.putExtra(Constants.DRUG_TYPE_CODE, intent.getStringExtra(Constants.DRUG_TYPE_CODE))
                launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.MEDICINE_TRACKER)
                startActivity(launchIntent)
                cancelNotification(this, intent.getIntExtra(Constants.NOTIFICATION_ID, -1))
            } else if (intent.hasExtra(Constants.NOTIFICATION_TYPE)) {
                when (intent.getStringExtra(Constants.NOTIFICATION_TYPE)) {
                    "home" -> {
                        openAnotherActivity(destination = NavigationConstants.AKTIVO_PERMISSION_SCREEN) {
                            putString(Constants.CODE, Constants.AKTIVO_DASHBOARD_CODE)
                        }
                    }
                    "homeSedentary", "homeExercise" -> {
                        openAnotherActivity(destination = NavigationConstants.AKTIVO_PERMISSION_SCREEN) {
                            putString(Constants.CODE, Constants.AKTIVO_SCORE_CODE)
                        }
                    }
                    "challenge", "challengeDetail" -> {
                        openAnotherActivity(destination = NavigationConstants.AKTIVO_PERMISSION_SCREEN) {
                            putString(Constants.CODE, Constants.AKTIVO_CHALLENGES_CODE)
                        }
                    }
                    else -> {
                        openAnotherActivity(destination = NavigationConstants.AKTIVO_PERMISSION_SCREEN) {
                            putString(Constants.CODE, Constants.AKTIVO_DASHBOARD_CODE)
                        }
                    }
                }
            } else {
                val isDarwinBoxDetailsAvailable = Utilities.getBooleanPreference(PreferenceConstants.IS_DARWINBOX_DETAILS_AVAILABLE)
                val darwinBoxUrl = Utilities.getUserPreference(Constants.DARWINBOX_URL)
                if (viewModel.isFirstTimeUser()) {
                    Utilities.printLogError("First_Time_User")
                    if (isDarwinBoxDetailsAvailable) {
                        viewModel.callGetLoginInfoWithDarwinBoxApi(darwinBoxUrl, this)
                        Utilities.clearDarwinBoxDetails()
                    } else {
                        startActivity(Intent(this, AppIntroductionActivity::class.java))
                        finish()
                    }
                } else {
                    if (viewModel.getLoginStatus()) {
                        Utilities.printLogError("User_Logged_In")
                        if (isDarwinBoxDetailsAvailable) {
                            viewModel.logoutFromDB()
                            Utilities.logout(this, this)
                            Utilities.printLogError("Logout for Darwinbox")
                            viewModel.callGetLoginInfoWithDarwinBoxApi(darwinBoxUrl, this)
                            Utilities.clearDarwinBoxDetails()
                        } else {
                            openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true, animate = false)
                            finish()
                        }
                    } else {
                        Utilities.printLogError("User_not_Logged_In")
                        if (isDarwinBoxDetailsAvailable) {
                            viewModel.callGetLoginInfoWithDarwinBoxApi(darwinBoxUrl, this)
                            Utilities.clearDarwinBoxDetails()
                        } else {
                            openAnotherActivity(destination = NavigationConstants.LOGIN, clearTop = true)
                            finish()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSkipUpdate() {
        proceedInApp()
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
        if (isButtonRight && isRooted) {
            finishAffinity()
        }
    }

    private fun cancelNotification(context: Context, notificationId: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notificationId)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showBiometricLock() {
        try {
            val biometricPrompt = BiometricPrompt.Builder(this@SplashScreenActivity)
                //.setDeviceCredentialAllowed(true) // not supported on Android 10 & below devices
                .setTitle(resources.getString(R.string.BIOMETRIC_AUTHENTICATION))
                .setSubtitle(resources.getString(R.string.BIOMETRIC_AUTHENTICATION_DESC))
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                biometricPrompt.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            }
            biometricPrompt.build().authenticate(
                Utilities.getCancellationSignal(),
                mainExecutor,
                authenticationCallback
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showIsEmployeeDialog() {
        dialogEmployee = DialogEmployee(this, this, viewModel)
        dialogEmployee!!.show()
    }

    override fun onEmployerSelect(employer: EmployerModel) {
        Utilities.printData("EmployerModel", employer, true)
        dialogEmployee!!.dismiss()
        when (employer.employerCode) {
            Constants.SUD_LIFE -> {
                viewModel.logoutFromDB()
                Utilities.logout(this, this)
                val dialogData = DefaultNotificationDialog.DialogData()
                dialogData.message = resources.getString(R.string.MSG_LOGIN_SUD)
                dialogData.btnRightName = resources.getString(R.string.OK)
                dialogData.showLeftButton = false
                dialogData.showDismiss = false
                val defaultNotificationDialog = DefaultNotificationDialog(
                    this@SplashScreenActivity,
                    object : DefaultNotificationDialog.OnDialogValueListener {
                        override fun onDialogClickListener(
                            isButtonLeft: Boolean,
                            isButtonRight: Boolean
                        ) {
                            if (isButtonRight) {
                                finishAffinity()
                            }
                        }
                    }, dialogData
                )
                defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                defaultNotificationDialog.show()
            }

            else -> {
                Utilities.setEmployeeType(employer.employerCode)
                Utilities.logCleverTapEmployeeEventLogin(this, employer.employerCode)
                viewModel.callCheckAppUpdateApi()
            }
        }
    }

    /*    override fun onEmployerSelect(employer: EmployerModel) {
            dialogEmployee!!.dismiss()
            Utilities.printData("EmployerModel",employer,true)
            Utilities.setEmployeeType(employer.employerCode)
            Utilities.logCleverTapEmployeeEventLogin(this,employer.employerCode)
            viewModel.callCheckAppUpdateApi()
        }*/

}