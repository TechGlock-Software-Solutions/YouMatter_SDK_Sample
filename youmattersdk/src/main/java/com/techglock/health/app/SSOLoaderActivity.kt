package com.techglock.health.app

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ActivitySsoloaderBinding
import com.techglock.health.app.security.viewmodel.StartupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SSOLoaderActivity : BaseActivity() {

    private lateinit var binding: ActivitySsoloaderBinding

    private val viewModel: StartupViewModel by lazy {
        ViewModelProvider(this)[StartupViewModel::class.java]
    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivitySsoloaderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            initialise()
        } catch ( e:Exception ) {
            e.printStackTrace()
        }
    }

    private fun initialise() {
        if (intent.hasExtra(Constants.DATA)) {
            viewModel.logoutFromDB()
            if (Utilities.logout(this, this)) {
                Utilities.printLogError("Cleared Previous Session Data")
                val data = intent.getStringExtra(Constants.DATA)!!
                Utilities.printData("SSO_Data",data,true)
                viewModel.callSSO(data,this)
            }
        } else {
            Utilities.toastMessageShort(this,"SSO details not found")
        }

        viewModel.ssoLoginRegister.observe(this) { }
    }

}