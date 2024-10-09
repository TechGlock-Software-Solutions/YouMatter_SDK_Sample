package com.techglock.health.app.common.base

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.utils.AppAH
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.Event
import com.techglock.health.app.common.utils.LocaleHelper.onAttach
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.navigation.NavigationCommand
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


abstract class BaseActivity : AppCompatActivity() {


    private val progressDialog by lazy {
        //ProgressDialog(this)
        CustomProgressBar(this)
    }

    private val sessionExpiryDialog by lazy {
        val dialogData = DefaultNotificationDialog.DialogData()
        dialogData.title = resources.getString(R.string.SESSION)
        dialogData.message = resources.getString(R.string.MSG_SESSION_EXPIRED)
        dialogData.showLeftButton = false
        dialogData.showDismiss = false
        DefaultNotificationDialog(
            this, object : DefaultNotificationDialog.OnDialogValueListener {
                override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                    if (isButtonRight) {
                        if (Utilities.logout(this@BaseActivity, this@BaseActivity)) {
                            openAnotherActivity(
                                destination = NavigationConstants.LOGIN,
                                clearTop = true
                            )
                        }
                    }
                }
            }, dialogData
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            //LocaleHelper.onAttach(this, LocaleHelper.getLanguage(this))
            AppAH.instance.setCurrActivity(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        onCreateEvent(savedInstanceState)
        initBase()
    }

    private fun initBase() {
        Utilities.printLog("initBase()=> ")
        observeSession(getViewModel())
        setupSnackBar(this, getViewModel().snackBarError, Snackbar.LENGTH_LONG)
        setupSnackBarMessenger(this, getViewModel().snackMessenger, Snackbar.LENGTH_LONG)
        setupToast(this, getViewModel().toastMessage)
        setupToastMessageError(this, getViewModel().toastError, Toast.LENGTH_LONG)
        setUpProgressBar(this, getViewModel().progressBar)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }

    private fun setupToast(lifecycleOwner: LifecycleOwner, toastEvent: LiveData<Event<String>>) {
        toastEvent.observe(lifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { data ->
                showToast(data)
            }
        }
    }

    fun showToast(data: String) {
        Utilities.toastMessageShort(this, data)
    }

    private fun setUpProgressBar(
        lifecycleOwner: LifecycleOwner,
        progressBar: LiveData<Event<String>>,
        message: String = "Loading..."
    ) {
        progressBar.observe(lifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                if (it.equals(Event.HIDE_PROGRESS, true)) showProgress(false, it)
                else showProgress(true, it)
            }
        }
    }

    private fun showProgress(showProgress: Boolean, message: String) {
        try {
            if (showProgress) {
                if (progressDialog.isShowing) {
                    progressDialog.cancel()
                    progressDialog.dismiss()
                }
                //progressDialog.setMessage(message)
                //progressDialog.isIndeterminate = false
                progressDialog.setCancelable(false)
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.setLoaderType(message)
                progressDialog.show()
            } else {
                if (progressDialog.isShowing) {
                    //progressDialog.cancel()
                    //progressDialog.dismiss()
                    lifecycleScope.launch(Dispatchers.Main) {
                        delay(
                            if (message.equals(
                                    Constants.LOADER_DELETE,
                                    ignoreCase = true
                                )
                            ) (Constants.DELETE_LOADER_ANIM_DELAY_IN_MS).toLong() else (Constants.LOADER_ANIM_DELAY_IN_MS).toLong()
                        )
                        progressDialog.cancel()
                        progressDialog.dismiss()
                    }
                    /*if (message.equals(Constants.LOADER_DELETE, ignoreCase = true)) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            progressDialog.cancel()
                            progressDialog.dismiss()
                        }, (Constants.DELETE_LOADER_ANIM_DELAY_IN_MS).toLong())
                    } else {
                        Handler(Looper.getMainLooper()).postDelayed({
                            progressDialog.cancel()
                            progressDialog.dismiss()

                        }, (Constants.LOADER_ANIM_DELAY_IN_MS).toLong())
                    }*/
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupSnackBar(
        lifecycleOwner: LifecycleOwner, snackBarEvent: LiveData<Event<Int>>, timeLength: Int
    ) {
        snackBarEvent.observe(lifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { res ->
                showSnackBar(this.getString(res), timeLength)
            }
        }
    }

    private fun setupToastMessageError(
        lifecycleOwner: LifecycleOwner, toastEvent: LiveData<Event<Int>>, timeLength: Int
    ) {
        toastEvent.observe(lifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { res ->
                showToast(this.getString(res))
            }
        }
    }

    private fun setupSnackBarMessenger(
        lifecycleOwner: LifecycleOwner, snackBarEvent: LiveData<Event<String>>, timeLength: Int
    ) {
        snackBarEvent.observe(lifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                showSnackBar(it, timeLength)
            }
        }
    }

    private fun showSnackBar(message: String, timeLength: Int) {
        Snackbar.make(this.findViewById(android.R.id.content), message, timeLength).show()
    }

    private fun observeSession(viewModel: BaseViewModel) {
        viewModel.sessionError.observe(this) {
            sessionExpiryDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            sessionExpiryDialog.show()
        }
    }

    abstract fun getViewModel(): BaseViewModel

    protected abstract fun onCreateEvent(savedInstanceState: Bundle?)

    fun navigationController(navController: NavController) {
        getViewModel().navigation.observe(this) {
            it?.getContentIfNotHandled()?.let { command ->
                when (command) {
                    is NavigationCommand.To -> navController.navigate(
                        command.directions
                    )

                    is NavigationCommand.Back -> navController.navigateUp()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        AppAH.instance.setCurrActivity(this)
    }

    override fun onResume() {
        AppAH.instance.setCurrActivity(this)
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        AppAH.instance.setCurrActivity(this)
        showProgress(false, "")
    }


    override fun attachBaseContext(base: Context?) {
        //Log.e("","BaseActivity : attachBaseContext")
        super.attachBaseContext(onAttach(base!!))
    }

    fun setSystemBarTheme(isStatusBarFontDark: Boolean) {

        window.decorView.systemUiVisibility = if (isStatusBarFontDark) {
            0
        } else {
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        }

    }


    fun updateStatusBarColor(
        @ColorRes colorId: Int,
        isStatusBarFontDark: Boolean = true,
        @ColorRes statusBarColor: Int = R.color.transparent
    ) {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, statusBarColor)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.transparent)
        window.setBackgroundDrawable(ContextCompat.getDrawable(this, colorId))
        setSystemBarTheme(isStatusBarFontDark)
    }


}