package com.techglock.health.app.home.ui.sudLifePolicy

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ActivitySudLifePolicyBinding
import com.techglock.health.app.home.viewmodel.SudLifePolicyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SudLifePolicyActivity : BaseActivity() {

    private lateinit var navController: NavController
    private val appColorHelper = AppColorHelper.instance!!
    private val viewModel: SudLifePolicyViewModel by lazy {
        ViewModelProvider(this)[SudLifePolicyViewModel::class.java]
    }
    private lateinit var binding: ActivitySudLifePolicyBinding

    private var onHelpClickListener: OnHelpClickListener? = null

    override fun getViewModel(): BaseViewModel = viewModel
    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivitySudLifePolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
        try {
            initialise()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initialise() {

        setSupportActionBar(binding.toolBarView.toolbarCommon)
        // Setting up a back button
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_sud_life_policy) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        val bundle = Bundle()
        if (intent.hasExtra(Constants.PHONE_NUMBER)) {
            bundle.putString(Constants.PHONE_NUMBER, intent.getStringExtra(Constants.PHONE_NUMBER))
            Utilities.printLog("PHONE_NUMBER--->" + intent.getStringExtra(Constants.PHONE_NUMBER))
        }

        if (intent.hasExtra(Constants.FROM) && !Utilities.isNullOrEmpty(
                intent.getStringExtra(
                    Constants.FROM
                )
            )
        ) {
            when (intent.getStringExtra(Constants.FROM)) {
                Constants.PROFILE -> bundle.putString(Constants.FROM, Constants.PROFILE)
            }
        }

        navController.setGraph(R.navigation.nav_graph_sud_life_policy, bundle)

        /* navController.addOnDestinationChangedListener { controller, destination, _ ->
             binding.toolBarView.toolbarTitle.text = when (destination.id) {
                 R.id.sudLifePolicyDashboardFragment -> resources.getString(R.string.TITLE_POLICY_DASHBOARD)
                 R.id.sudLifePolicyDetailsFragment -> resources.getString(R.string.TITLE_POLICY_DETAILS)
                 R.id.viewReceiptFragment -> resources.getString(R.string.TITLE_PREMIUM_RECEIPT)
                 else -> ""
             }

             binding.toolBarView.imgHelp.visibility = when (destination.id) {
                 R.id.sudLifePolicyDashboardFragment -> View.VISIBLE
                 else -> View.GONE
             }

             *//*            if (destination.id == controller.graph.startDestination) {
                            supportActionBar!!.setDisplayShowTitleEnabled(false)
                            supportActionBar?.setDisplayHomeAsUpEnabled(true)
                            supportActionBar?.setHomeButtonEnabled(true)
                            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
                        } else {
                            supportActionBar!!.setDisplayShowTitleEnabled(false)
                            supportActionBar?.setDisplayHomeAsUpEnabled(true)
                            supportActionBar?.setHomeButtonEnabled(true)
                            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
                        }*//*

            supportActionBar!!.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

            binding.toolBarView.toolbarCommon.navigationIcon?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    appColorHelper.textColor, BlendModeCompat.SRC_ATOP
                )

            binding.toolBarView.imgHelp.setOnClickListener {
                if (onHelpClickListener != null) {
                    onHelpClickListener!!.onHelpClick()
                }
            }
        }*/
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // API 5+ solution
                onBackPressedDispatcher.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    interface OnHelpClickListener {
        fun onHelpClick()
    }

    fun setOnHelpClickListener(listener: OnHelpClickListener) {
        this.onHelpClickListener = listener
    }

}