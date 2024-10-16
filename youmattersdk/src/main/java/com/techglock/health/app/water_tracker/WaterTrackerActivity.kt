package com.techglock.health.app.water_tracker

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
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
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.showDialog
import com.techglock.health.app.databinding.ActivityWaterTrackerBinding
import com.techglock.health.app.water_tracker.viewmodel.WaterTrackerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WaterTrackerActivity : BaseActivity(), DefaultNotificationDialog.OnDialogValueListener {

    private lateinit var navController: NavController
    private val appColorHelper = AppColorHelper.instance!!
    private val viewModel: WaterTrackerViewModel by lazy {
        ViewModelProvider(this)[WaterTrackerViewModel::class.java]
    }
    private lateinit var binding: ActivityWaterTrackerBinding

    private var screen = ""
    private var notificationTitle = ""
    private var notificationMsg = ""

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityWaterTrackerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBarView.toolbarWaterTracker)
        // Setting up a back button
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_water_tracker) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        val bundle = Bundle()
        if (intent.hasExtra(Constants.SCREEN)) {
            screen = intent.getStringExtra(Constants.SCREEN)!!
            if (intent.hasExtra(Constants.NOTIFICATION_TITLE)) {
                notificationTitle = intent.getStringExtra(Constants.NOTIFICATION_TITLE)!!
            }
            if (intent.hasExtra(Constants.NOTIFICATION_MESSAGE)) {
                notificationMsg = intent.getStringExtra(Constants.NOTIFICATION_MESSAGE)!!
            }
            checkNotificationData()
        }

        navController.setGraph(R.navigation.nav_graph_water_tracker_feature, bundle)
        navController.addOnDestinationChangedListener { controller, destination, _ ->
            binding.toolBarView.toolbarTitle.text = when (destination.id) {
                R.id.waterTrackerDashboardFragment -> resources.getString(R.string.TITLE_WATER_TRACKER)
                else -> resources.getString(R.string.TITLE_WATER_TRACKER)
            }

/*            when (destination.id) {
                R.id.waterTrackerDashboardFragment -> {
                    binding.layoutWaterTracker.background =
                        ContextCompat.getDrawable(this, R.drawable.img_bg_water_tracker)
                }

                else -> {
                    binding.layoutWaterTracker.background =
                        ContextCompat.getDrawable(this, R.drawable.bg_transparant)
                }
            }*/

            if (destination.id == controller.graph.getStartDestination()) {
                supportActionBar!!.setDisplayShowTitleEnabled(false)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeButtonEnabled(true)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
            } else {
                supportActionBar!!.setDisplayShowTitleEnabled(false)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeButtonEnabled(true)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
            }

            binding.toolBarView.toolbarWaterTracker.navigationIcon?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    appColorHelper.textColor,
                    BlendModeCompat.SRC_ATOP
                )
        }

    }

    private fun checkNotificationData() {
        Utilities.printLogError("screen--->$screen")
        when (screen) {
            "WATER_REMINDER" -> {
                Handler(Looper.getMainLooper()).postDelayed({
                    showNotificationDialog()
                }, 1500)
            }

            "WATER_REMINDER_21_POSITIVE" -> {
                showBlast()
            }
        }
    }

    private fun showBlast() {
        binding.imgCustomAnim.setAnimation(R.raw.blast_2)
        Handler(Looper.getMainLooper()).postDelayed({
            showNotificationDialog()
        }, 2000)
    }

    private fun showNotificationDialog() {
        showDialog(
            listener = this,
            title = notificationTitle,
            message = notificationMsg,
            showDismiss = false,
            showLeftBtn = false,
            rightText = this.resources.getString(R.string.OK)
        )
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

    /*    override fun onBackPressed() {
            routeToHomeScreen()
        }*/

    fun routeToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {}

}