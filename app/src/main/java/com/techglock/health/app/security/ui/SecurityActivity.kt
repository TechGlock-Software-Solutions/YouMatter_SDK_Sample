package com.techglock.health.app.security.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ActivitySecurityBinding
import com.techglock.health.app.model.entity.AppVersion
import com.techglock.health.app.model.home.LanguageModel
import com.techglock.health.app.security.ui_dialog.DialogLanguage
import com.techglock.health.app.security.ui_dialog.DialogUpdateAppSecurity
import com.techglock.health.app.security.viewmodel.LoginNewViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecurityActivity : BaseActivity(), DialogUpdateAppSecurity.OnSkipUpdateListener,
    DialogLanguage.OnLanguageClickListener {

    private val appColorHelper = AppColorHelper.instance!!
    private lateinit var navController: NavController
    private lateinit var binding: ActivitySecurityBinding
    private val viewModel: LoginNewViewModel by lazy {
        ViewModelProvider(this)[LoginNewViewModel::class.java]
    }

    private var dialogLanguage: DialogLanguage? = null

    override fun getViewModel(): BaseViewModel = viewModel

    @SuppressLint("ResourceType")
    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivitySecurityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateStatusBarColor(R.drawable.gradient_login, false)

        binding.toolBarView.toolbarTitle
        setSupportActionBar(binding.toolBarView.toolBar)

        // Setting up a back button
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_security) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            when (destination.id) {
                R.id.loginFragment -> updateStatusBarColor(R.drawable.gradient_login, false)
                R.id.signUpStep1Fragment -> updateStatusBarColor(R.drawable.gradient_step1, false)
                R.id.signUpStep2Fragment -> updateStatusBarColor(R.drawable.gradient_step2, false)
                R.id.signUpStep3Fragment -> updateStatusBarColor(R.drawable.gradient_step3, false)
                R.id.signUpStep4Fragment -> updateStatusBarColor(R.drawable.gradient_step4, false)
                else -> updateStatusBarColor(R.drawable.gradient_login, true)

            }

            binding.toolBarView.tabLanguage.visibility = when (destination.id) {
                R.id.loginFragment -> View.VISIBLE
                else -> View.GONE
            }

            if (destination.id == controller.graph.startDestinationId) {
                supportActionBar!!.setDisplayShowTitleEnabled(false)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                supportActionBar?.setHomeButtonEnabled(true)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
                binding.toolBarView.toolBar.navigationIcon?.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        ContextCompat.getColor(this, R.color.transparent), BlendModeCompat.SRC_ATOP
                    )
            } else {
                supportActionBar!!.setDisplayShowTitleEnabled(false)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeButtonEnabled(true)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
                binding.toolBarView.toolBar.navigationIcon?.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        appColorHelper.textColor, BlendModeCompat.SRC_ATOP
                    )
            }
        }

        if (LocaleHelper.getLanguage(this) == "hi") {
            binding.toolBarView.txtLanguage.text = resources.getString(R.string.HINDI)
        } else {
            binding.toolBarView.txtLanguage.text = resources.getString(R.string.ENGLISH)
        }

        binding.toolBarView.tabLanguage.setOnClickListener {
            showLanguageSelectionDialog()
        }

        try {
            if (intent.hasExtra(Constants.FROM) && intent.getStringExtra(Constants.FROM) == Constants.LOGOUT) {
                val versionDetails = AppVersion(
                    forceUpdate = intent.getBooleanExtra(Constants.FORCEUPDATE, false),
                    description = intent.getStringExtra(Constants.DESCRIPTION)
                )
                val dialogUpdateApp = DialogUpdateAppSecurity(this, versionDetails, this)
                dialogUpdateApp.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*    override fun onSupportNavigateUp(): Boolean {
            return Navigation.findNavController(this, R.id.nav_host_fragment_security).navigateUp()
        }*/

    fun setTitle(title: String) {
        binding.toolBarView.toolbarTitle.text = title
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // API 5+ solution
                onBackPressed()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSkipUpdate() {}

    private fun showLanguageSelectionDialog() {
        dialogLanguage = DialogLanguage(this, this)
        dialogLanguage!!.show()
        /*        this.showLanguageDialog { it, data ->
                    Utilities.printData("LanguageModel", data!!, true)
                    Utilities.changeLanguage(data, this)
                    Utilities.logCleverTapChangeLanguage(data, this)
                    binding.toolBarView.txtLanguage.text = Utilities.getLanguageNameConverted(data, this)
                    recreate()
                }*/
    }


    override fun onLanguageSelection(data: LanguageModel) {
        Utilities.printData("LanguageModel", data, true)
        Utilities.changeLanguage(data.languageCode, this)
        Utilities.logCleverTapChangeLanguage(data.languageCode, this)
        recreate()
        binding.toolBarView.txtLanguage.text =
            Utilities.getLanguageNameConverted(data.languageCode, this)
    }
}
