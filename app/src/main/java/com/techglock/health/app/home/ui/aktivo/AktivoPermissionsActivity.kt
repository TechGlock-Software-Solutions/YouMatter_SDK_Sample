package com.techglock.health.app.home.ui.aktivo

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.fitness.FitnessDataManager
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.PermissionUtil
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ActivityAktivoPermissionsBinding
import com.techglock.health.app.home.viewmodel.AktivoViewModel
import com.techglock.health.app.repository.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable

@AndroidEntryPoint
class AktivoPermissionsActivity : BaseActivity() {

    private lateinit var binding: ActivityAktivoPermissionsBinding
    private val viewModel: AktivoViewModel by lazy {
        ViewModelProvider(this)[AktivoViewModel::class.java]
    }
    private val appColorHelper = AppColorHelper.instance!!

    private var code = ""
    private val pwaExitCode = 9000
    private val permissionUtil = PermissionUtil
    private var fitnessDataManager: FitnessDataManager? = null

    //    private var aktivoManager: AktivoManager? = null
    private var compositeDisposable: CompositeDisposable? = null

    override fun getViewModel(): BaseViewModel = viewModel

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityAktivoPermissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            if (intent.hasExtra(Constants.CODE)) {
                code = intent.getStringExtra(Constants.CODE)!!
            }
            Utilities.printLogError("ScreenCode--->$code")
            setupToolbar()
            initialise()
            registerObserver()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initialise() {
        binding.layoutPermissions.visibility = View.GONE
        fitnessDataManager = FitnessDataManager(this)
//        aktivoManager = AktivoManager.getInstance(this)
        compositeDisposable = CompositeDisposable()

        if (Utilities.isNullOrEmpty(viewModel.getUserPreference(PreferenceConstants.AKTIVO_MEMBER_ID)) && Utilities.isNullOrEmpty(
                viewModel.getUserPreference(PreferenceConstants.AKTIVO_ACCESS_TOKEN)
            ) && Utilities.isNullOrEmpty(viewModel.getUserPreference(PreferenceConstants.AKTIVO_REFRESH_TOKEN))
        ) {
            /*FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                // Get new FCM registration token
                val token = task.result!!
                Utilities.printLogError("\nDeviceToken--->$token")
                viewModel.callAktivoCreateUserApi(token)
            }*/
        } else {
//            authenticateUserUsingToken()
        }

        binding.btnAllowPermission.setOnClickListener {
//            checkAllGoogleFitPermission()
        }
    }

    private fun checkFitnessPermissionsAndProceed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (fitnessDataManager!!.aktivoAuthPermissionsApproved() && permissionUtil.isActivityRecognitionPermission(
                    this
                )
            ) {
//                syncFitnessData()
            } else {
                viewModel.hideProgress()
                binding.layoutPermissions.visibility = View.VISIBLE
            }
        } else {
            if (fitnessDataManager!!.aktivoAuthPermissionsApproved()) {
//                syncFitnessData()
            } else {
                viewModel.hideProgress()
                binding.layoutPermissions.visibility = View.VISIBLE
            }
        }
    }

    private fun registerObserver() {

        viewModel.aktivoCreateUser.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                if (!Utilities.isNullOrEmpty(it.data!!.resultData.member.id)) {
                    viewModel.storeUserPreference(
                        PreferenceConstants.AKTIVO_MEMBER_ID, it.data.resultData.member.id!!
                    )
                    viewModel.callAktivoGetUserTokenApi(it.data.resultData.member.id)
                }
            }
        }

        viewModel.aktivoGetUserToken.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                if (!Utilities.isNullOrEmpty(it.data!!.accessToken) && !Utilities.isNullOrEmpty(it.data.refreshToken)) {
                    viewModel.storeUserPreference(
                        PreferenceConstants.AKTIVO_ACCESS_TOKEN, it.data.accessToken!!
                    )
                    viewModel.storeUserPreference(
                        PreferenceConstants.AKTIVO_REFRESH_TOKEN, it.data.refreshToken!!
                    )
//                    authenticateUserUsingToken()
                }
            }
        }

        /*        viewModel.aktivoGetRefreshToken.observe(this) {
                    if (it.status == Resource.Status.SUCCESS) {
                        if ( !Utilities.isNullOrEmpty(it.data!!.accessToken) && !Utilities.isNullOrEmpty(it.data!!.refreshToken) ) {
                            viewModel.storeUserPreference(PreferenceConstants.AKTIVO_ACCESS_TOKEN,it.data!!.accessToken!!)
                            viewModel.storeUserPreference(PreferenceConstants.AKTIVO_REFRESH_TOKEN,it.data!!.refreshToken!!)
                            authenticateUserUsingToken()
                        }
                    }
                }*/

        viewModel.aktivoGetUser.observe(this) { }
        viewModel.aktivoCheckUser.observe(this) { }
    }

    /*    private fun authenticateUserUsingToken() {
            val userId = "64bfcfb8d2f793d668213613"
            val token = "79e8e22cbf6da94a3b3d815a965daaf83560356c"
            val refreshToken = "4a3c83074f6b63d85347f4618a25b85cf9388520"
            aktivoManager!!.setClientId(Constants.strAktivoClientId)
            aktivoManager!!.setUserTokens(token,refreshToken)
            if ( !Utilities.isNullOrEmpty(userId) ) {
                localRepository!!.putUserId(userId)
            }
            authenticateUser(userId)
        }

    private fun authenticateUserUsingToken() {
        val userId = viewModel.getUserPreference(PreferenceConstants.AKTIVO_MEMBER_ID)
        val token = viewModel.getUserPreference(PreferenceConstants.AKTIVO_ACCESS_TOKEN)
        val refreshToken = viewModel.getUserPreference(PreferenceConstants.AKTIVO_REFRESH_TOKEN)
        aktivoManager!!.setClientId(Constants.strAktivoClientId)
        aktivoManager!!.setUserTokens(token, refreshToken)
        authenticateUser(userId)
    }

    private fun authenticateUser(userId: String) {
        try {
            viewModel.showProgress()
            aktivoManager!!.authenticateUser(User(userId), this).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onComplete() {
                        //viewModel.hideProgress()
                        Utilities.printLogError("User Authenticated")
                        checkFitnessPermissionsAndProceed()
                        //callAktivoLiteDashboard(code)
                    }

                    override fun onError(e: Throwable) {
                        viewModel.hideProgress()
                        e.printStackTrace()
                        Utilities.toastMessageShort(
                            this@AktivoPermissionsActivity, "Auth error: " + e.message
                        )
                        viewModel.callAktivoGetUserTokenApi(
                            viewModel.getUserPreference(
                                PreferenceConstants.AKTIVO_MEMBER_ID
                            )
                        )
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun syncFitnessData() {
        try {
            //viewModel.showProgress()
            compositeDisposable!!.add(
                aktivoManager!!.syncFitnessData().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableCompletableObserver() {
                        override fun onComplete() {
                            //viewModel.hideProgress()
                            Utilities.printLogError("Data Synced")
                            callAktivoLiteDashboard(code)
                        }

                        override fun onError(e: Throwable) {
                            viewModel.hideProgress()
                            e.printStackTrace()
                            Utilities.toastMessageShort(
                                this@AktivoPermissionsActivity, "Data Sync error: " + e.message
                            )
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callAktivoLiteDashboard(screenCode: String) {
        try {
            //val toolbarHeight = DensityUtil.px2dip(this,binding.toolBarView.toolbarTitle.height.toFloat())
            //viewModel.showProgress()
            aktivoManager!!.launchPWA(
                screenCode, pwaExitCode, this, AktivoLiteToolbarTheme(
                    "#FFFFFF", "#000000", R.drawable.app_logo_small, null, 55
                )
            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onComplete() {
                        Utilities.printLogError("launch pwa success")
                        viewModel.hideProgress()
                    }

                    override fun onError(e: Throwable) {
                        Utilities.printLogError("error in launch PWA\n$e")
                        viewModel.hideProgress()
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

      private fun launchPWA(displayCode: String) {
            try {
                aktivoManager!!.launchPWA(displayCode, this,
                    AktivoLiteToolbarTheme("#FFFFFF","#000000",R.drawable.app_logo,null,60)
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CompletableObserver {
                        override fun onSubscribe(d: Disposable) {}
                        override fun onComplete() {
                            Utilities.printLogError("launch pwa success")
                        }

                        override fun onError(e: Throwable) {
                            Utilities.printLogError("error in launch PWA\n$e")
                        }
                    })
            } catch ( e:Exception ) {
                e.printStackTrace()
            }
        }

    private fun checkAllGoogleFitPermission() {
        try {
            aktivoManager!!.isGoogleFitPermissionGranted.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Boolean> {
                    override fun onSubscribe(d: Disposable) {}

                    @RequiresApi(Build.VERSION_CODES.Q)
                    override fun onSuccess(aBoolean: Boolean) {
                        if (aBoolean) {
                            Utilities.printLogError("All Google fit permissions granted")
                            checkPhysicalActivityPermission()
                        } else {
                            requestAllGoogleFitPermission()
                        }
                    }

                    override fun onError(e: Throwable) {}
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun requestAllGoogleFitPermission() {
        try {
            Utilities.printLogError("Requesting All Google fit permissions")
            aktivoManager!!.requestGoogleFitPermissions(
                this,
                Constants.REQ_CODE_AKTIVO_GOOGLE_FIT_PERMISSIONS
            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onError(e: Throwable) {}
                    override fun onComplete() {
                        Utilities.printLogError("Permission requested")
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

       private fun checkGoogleFitActivityPermission() {
            try {
                Utilities.printLogError("Checking Google fit Activity permissions")
                aktivoManager!!
                    .isGoogleFitActivityPermissionGranted
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Boolean> {
                        override fun onSubscribe(d: Disposable) {}
                        override fun onSuccess(aBoolean: Boolean) {
                            if (aBoolean) {
                                Utilities.printLogError("Google fit Activity permissions granted")
                                checkGoogleFitSleepPermission()
                            } else {
                                requestGoogleFitActivityPermission()
                            }
                        }
                        override fun onError(e: Throwable) {}
                    })
            } catch ( e:Exception ) {
                e.printStackTrace()
            }
        }

        private fun requestGoogleFitActivityPermission() {
            try {
                Utilities.printLogError("Requesting Google fit Activity permissions")
                aktivoManager!!
                    .requestGoogleFitActivityPermission(this,Constants.REQ_CODE_AKTIVO_GOOGLE_FIT_ACTIVITY_PERMISSIONS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CompletableObserver {
                        override fun onSubscribe(d: Disposable) {}
                        override fun onError(e: Throwable) {}
                        override fun onComplete() {
                            Utilities.printLogError("Permission requested")
                        }
                    })
            } catch ( e:Exception ) {
                e.printStackTrace()
            }
        }

        private fun checkGoogleFitSleepPermission() {
            try {
                Utilities.printLogError("Checking Google fit Sleep permissions")
                aktivoManager!!
                    .isGoogleFitSleepPermissionGranted
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Boolean> {
                        override fun onSubscribe(d: Disposable) {}
                        @RequiresApi(Build.VERSION_CODES.Q)
                        override fun onSuccess(aBoolean: Boolean) {
                            if (aBoolean) {
                                Utilities.printLogError("Google fit Sleep permissions granted")
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    checkPhysicalActivityPermission()
                                } else {
                                    byPassAuthenticateUserUsingToken()
                                }
                            } else {
                                Utilities.printLogError("Google fit Sleep permissions not granted")
                                requestGoogleFitSleepPermission()
                            }
                        }

                        override fun onError(e: Throwable) {}
                    })
            } catch ( e:Exception ) {
                e.printStackTrace()
            }
        }

        private fun requestGoogleFitSleepPermission() {
            try {
                Utilities.printLogError("Requesting Google fit Sleep permissions")
                aktivoManager!!
                    .requestGoogleFitSleepPermission(this,Constants.REQ_CODE_AKTIVO_GOOGLE_FIT_SLEEP_PERMISSIONS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CompletableObserver {
                        override fun onSubscribe(d: Disposable) {}
                        override fun onError(e: Throwable) {}
                        override fun onComplete() {
                            Utilities.printLogError("Permission requested")
                        }
                    })
            } catch ( e:Exception ) {
                e.printStackTrace()
            }
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPhysicalActivityPermission() {
        try {
            Utilities.printLogError("Checking Physical Activity permissions")
            aktivoManager!!.isActivityRecognitionPermissionGranted(this)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Boolean> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onSuccess(aBoolean: Boolean) {
                        if (aBoolean) {
                            Utilities.printLogError("Physical Activity permissions granted")
                            syncFitnessData()
                        } else {
                            requestPhysicalActivityPermission()
                        }
                    }

                    override fun onError(e: Throwable) {}
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private fun requestPhysicalActivityPermission() {
        try {
            Utilities.printLogError("Requesting Physical Activity permissions")
            aktivoManager!!.requestActivityRecognitionPermission(
                this, Constants.REQ_PHYSICAL_ACTIVITY_PERMISSIONS
            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onError(e: Throwable) {}
                    override fun onComplete() {
                        Utilities.printLogError("Permission requested")
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    */
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.REQ_CODE_AKTIVO_GOOGLE_FIT_PERMISSIONS -> {
                if (resultCode == RESULT_OK) {
                    Utilities.printLogError("All Google fit permissions granted from 1")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        checkPhysicalActivityPermission()
                    } else {
//                        syncFitnessData()
                    }
                } else {
                    Utilities.toastMessageShort(
                        this, resources.getString(R.string.ERROR_GOOGLE_FIT_PERMISSION)
                    )
                }
            }
            //********************************************************************
            /*            Constants.REQ_CODE_AKTIVO_GOOGLE_FIT_ACTIVITY_PERMISSIONS -> {
                            if (resultCode == RESULT_OK) {
                                Utilities.printLogError("Google fit Activity permission granted from 1")
                                checkGoogleFitSleepPermission()
                            } else {
                                Utilities.toastMessageShort(this,resources.getString(R.string.ERROR_GOOGLE_FIT_PERMISSION))
                            }
                        }

                        Constants.REQ_CODE_AKTIVO_GOOGLE_FIT_SLEEP_PERMISSIONS -> {
                            if (resultCode == RESULT_OK) {
                                Utilities.printLogError("Google fit Sleep permission granted")
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    checkPhysicalActivityPermission()
                                } else {
                                    byPassAuthenticateUserUsingToken()
                                }
                            } else {
                                Utilities.toastMessageShort(this,"Google fit Sleep permission denied")
                            }
                        }*/
            //********************************************************************
            pwaExitCode -> {
                Utilities.printLogError("PWA Dashboard exit")
                routeToHomeScreen()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.REQ_CODE_AKTIVO_GOOGLE_FIT_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Utilities.printLogError("All Google fit permissions granted from 2")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        checkPhysicalActivityPermission()
                    } else {
//                        syncFitnessData()
                    }
                } else {
                    Utilities.toastMessageShort(
                        this, resources.getString(R.string.ERROR_GOOGLE_FIT_PERMISSION)
                    )
                }
            }
            //********************************************************************
            /*            Constants.REQ_CODE_AKTIVO_GOOGLE_FIT_ACTIVITY_PERMISSIONS -> {
                            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                Utilities.printLogError("Google fit Activity permission granted from 1")
                                checkGoogleFitSleepPermission()
                            } else {
                                Utilities.toastMessageShort(this,"Google fit Activity permission denied")
                            }
                        }

                        Constants.REQ_CODE_AKTIVO_GOOGLE_FIT_SLEEP_PERMISSIONS -> {
                            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                Utilities.printLogError("Google fit Sleep permission granted")
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    checkPhysicalActivityPermission()
                                } else {
                                    byPassAuthenticateUserUsingToken()
                                }
                            } else {
                                Utilities.toastMessageShort(this,"Google fit Sleep permission denied")
                            }
                        }*/
            //********************************************************************
            Constants.REQ_PHYSICAL_ACTIVITY_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Utilities.printLogError("Physical Activity permissions granted")
//                    syncFitnessData()
                } else {
                    Utilities.toastMessageShort(
                        this, resources.getString(R.string.ERROR_PHYSICAL_ACTIVITY_PERMISSION)
                    )
                }
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        binding.toolBarView.toolbarTitle.text = ""
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        binding.toolBarView.toolbarCommon.navigationIcon?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.textColor, BlendModeCompat.SRC_ATOP
            )

        binding.toolBarView.toolbarCommon.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
//        routeToHomeScreen()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (compositeDisposable != null) {
            compositeDisposable!!.dispose()
        }
    }

    private fun routeToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

}