package com.techglock.health.app.home.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.showDialog
import com.techglock.health.app.databinding.ActivitySettingsNewBinding
import com.techglock.health.app.home.adapter.OptionSettingsAdapter
import com.techglock.health.app.home.common.DataHandler
import com.techglock.health.app.home.viewmodel.BackgroundCallViewModel
import com.techglock.health.app.home.viewmodel.SettingsViewModel
import com.techglock.health.app.repository.utils.Resource
import com.techglock.health.app.security.ui.SecurityActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsActivity : BaseActivity(), OptionSettingsAdapter.SettingsOptionListener,
    DefaultNotificationDialog.OnDialogValueListener {


    private val viewModel: SettingsViewModel by lazy {
        ViewModelProvider(this)[SettingsViewModel::class.java]
    }

    //private val dashboardViewModel: DashboardViewModel by
    private val backgroundCallViewModel: BackgroundCallViewModel by lazy {
        ViewModelProvider(this)[BackgroundCallViewModel::class.java]
    }
    private lateinit var binding: ActivitySettingsNewBinding

    private var optionSettingsAdapter: OptionSettingsAdapter? = null
    private val appColorHelper = AppColorHelper.instance!!

    override fun getViewModel(): BaseViewModel = viewModel

    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }

    }

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_settings_new)
        binding = ActivitySettingsNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
        setupToolbar()
        initialise()
    }

    private fun initialise() {
        Utilities.printLog("Language=> " + LocaleHelper.getLanguage(this))
        binding.rvOptions.layoutManager = LinearLayoutManager(this)

        optionSettingsAdapter = OptionSettingsAdapter(viewModel, this, this)
        binding.rvOptions.adapter = optionSettingsAdapter
        viewModel.getSettingsOptionList()

        if (Utilities.checkBiometricSupport(this)) {
            binding.cardBiometric.visibility = View.VISIBLE
        } else {
            binding.cardBiometric.visibility = View.GONE
        }

        binding.swBiometric.isChecked = viewModel.isBiometricAuthentication()

        binding.swBiometric.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setBiometricAuthentication(isChecked)
            Utilities.toastMessageShort(
                this,
                "${resources.getString(R.string.BIOMETRIC_AUTHENTICATION)} ${
                    if (isChecked) resources.getString(R.string.ENABLED)
                    else resources.getString(R.string.DISABLED)
                }"
            )
        }

        viewModel.personDelete.observe(this) {
            viewModel.hideProgressBar()
            lifecycleScope.launch(Dispatchers.Main) {
                delay(600)
                if (it.status == Resource.Status.SUCCESS) {
                    if (!Utilities.isNullOrEmptyOrZero(it.data!!.accountID)) {
                        CleverTapHelper.pushEvent(
                            this@SettingsActivity,
                            CleverTapConstants.DELETE_ACCOUNT
                        )
                        Utilities.toastMessageShort(
                            this@SettingsActivity,
                            resources.getString(R.string.DELETE_ACCOUNT_SUCCESS)
                        )

                        backgroundCallViewModel.logoutFromDB()
                        if (Utilities.logout(this@SettingsActivity, this@SettingsActivity)) {
                            val intent = Intent(this@SettingsActivity, SecurityActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        }
                    }
                }
            }
        }

        viewModel.settingsOptionListData.observe(this) {
            it?.let {
                optionSettingsAdapter!!.updateDashboardOptionsList(it)
            }
        }
    }

    override fun onSettingsOptionListener(position: Int, option: DataHandler.Option) {
        Utilities.printLogError("SelectedPosition=>$position")
        when (option.code) {
            Constants.LANGUAGE -> {
                openAnotherActivity(destination = NavigationConstants.LANGUAGE_SCREEN) {
                    putString(Constants.FROM, "")
                }
            }

            Constants.CHANGE_PASSWORD -> {
                startActivity(Intent(this, PasswordChangeActivity::class.java))
            }

            Constants.DELETE_ACCOUNT -> {
                showDialog(
                    listener = this,
                    title = this.resources.getString(R.string.DELETE_ACCOUNT_TITLE),
                    message = this.resources.getString(R.string.DELETE_ACCOUNT_CONFIRMATION),
                    leftText = this.resources.getString(R.string.CANCEL),
                    rightText = this.resources.getString(R.string.CONFIRM),
                    showLeftBtn = true,
                    hasErrorBtn = true
                )
            }

            /*            "RATE_US" -> {
                            Utilities.goToPlayStore(this)
                        }*/
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        binding.toolBarView.toolbarTitle.text = resources.getString(R.string.SETTINGS)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        binding.toolBarView.toolbarCommon.navigationIcon?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.textColor, BlendModeCompat.SRC_ATOP
            )

        binding.toolBarView.toolbarCommon.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
        if (isButtonRight) {
            viewModel.callPersonDeleteApi()
        }
    }


}