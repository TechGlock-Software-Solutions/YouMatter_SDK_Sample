package com.techglock.health.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ActivityMainAppBinding
import com.techglock.health.app.security.ui.SplashScreenActivity
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.security.ProviderInstaller
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainAppActivity : AppCompatActivity(), ProviderInstaller.ProviderInstallListener {

    private lateinit var binding: ActivityMainAppBinding
    private var retryProviderInstall: Boolean = false
    private val ERROR_DIALOG_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main_app)
        binding = ActivityMainAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ProviderInstaller.installIfNeededAsync(this, this)
    }

    /**
     * This method is only called if the provider is successfully updated
     * (or is already up to date).
     */
    override fun onProviderInstalled() {
        Utilities.printLogError("onProviderInstalled")
        startActivity(Intent(this, SplashScreenActivity::class.java))
        finish()
    }

    /**
     * This method is called if updating fails. The error code indicates
     * whether the error is recoverable.
     */
    override fun onProviderInstallFailed(errorCode: Int, recoveryIntent: Intent?) {
        Utilities.printLogError("onProviderInstallFailed")
        GoogleApiAvailability.getInstance().apply {
            Utilities.printLogError("errorCode--->$errorCode")
            if (isUserResolvableError(errorCode)) {
                // Recoverable error. Show a dialog prompting the user to
                // install/update/enable Google Play services.
                showErrorDialogFragment(
                    this@MainAppActivity,
                    errorCode,
                    ERROR_DIALOG_REQUEST_CODE
                ) {
                    // The user chose not to take the recovery action.
                    onProviderInstallerNotAvailable()
                }
            } else {
                onProviderInstallerNotAvailable()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ERROR_DIALOG_REQUEST_CODE) {
            // Adding a fragment via GoogleApiAvailability.showErrorDialogFragment
            // before the instance state is restored throws an error. So instead,
            // set a flag here, which causes the fragment to delay until
            // onPostResume.
            retryProviderInstall = true
        }
    }

    /**
     * On resume, check whether a flag indicates that the provider needs to be
     * reinstalled.
     */
    override fun onPostResume() {
        super.onPostResume()
        if (retryProviderInstall) {
            // It's safe to retry installation.
            ProviderInstaller.installIfNeededAsync(this, this)
        }
        retryProviderInstall = false
    }

    private fun onProviderInstallerNotAvailable() {
        // This is reached if the provider can't be updated for some reason.
        // App should consider all HTTP communication to be vulnerable and take
        // appropriate action.
        Utilities.printLogError("onProviderInstallerNotAvailable")
        finish()
    }

}
