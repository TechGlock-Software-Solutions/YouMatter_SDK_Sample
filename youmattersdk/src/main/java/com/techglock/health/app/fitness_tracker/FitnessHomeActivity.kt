package com.techglock.health.app.fitness_tracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.fitness.FitRequestCode
import com.techglock.health.app.common.fitness.FitnessDataManager
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ActivityFitnessHomeBinding
import com.techglock.health.app.fitness_tracker.util.StepCountHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FitnessHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFitnessHomeBinding
    private lateinit var navController: NavController

    //private lateinit var appBarConfiguration: AppBarConfiguration
    private val appColorHelper = AppColorHelper.instance!!

    @Inject
    lateinit var stepCountHelper: StepCountHelper
    private var googleAccountListener: OnGoogleAccountSelectListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            LocaleHelper.onAttach(this, LocaleHelper.getLanguage(this))

        } catch (e: Exception) {
            e.printStackTrace()
        }
        //setContentView(R.layout.activity_fitness_home)
        binding = ActivityFitnessHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        FirebaseHelper.logScreenEvent(FirebaseConstants.ACTIVITY_TRACKER_SCREEN)
        setSupportActionBar(binding.toolbarFitLayout.toolBarFitness)
        // Setting up a back button
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        val bundle = Bundle()
        if (intent.hasExtra(Constants.FROM) && intent.getStringExtra(Constants.FROM)
                .equals(Constants.TRACK_PARAMETER, ignoreCase = true)
        ) {
            bundle.putString(Constants.FROM, Constants.TRACK_PARAMETER)
        } else if (intent.hasExtra(Constants.SCREEN)) {
            bundle.putString(Constants.SCREEN, intent.getStringExtra(Constants.SCREEN))
            if (intent.hasExtra(Constants.NOTIFICATION_MESSAGE)) {
                bundle.putString(
                    Constants.NOTIFICATION_MESSAGE,
                    intent.getStringExtra(Constants.NOTIFICATION_MESSAGE)
                )
            }
        }
        navController.setGraph(R.navigation.fitness_navigation, bundle)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            binding.toolbarFitLayout.toolbarTitle.text = when (destination.id) {
                //R.id.fitnessDashboardFragment -> resources.getString(R.string.TITLE_ACTIVITY_TRACKER)
                else -> resources.getString(R.string.TITLE_ACTIVITY_TRACKER)
            }

            binding.toolbarFitLayout.toolBarFitness.setBackgroundColor(
                ContextCompat.getColor(
                    this@FitnessHomeActivity,
                    R.color.background_color
                )
            )

            if (destination.id == controller.graph.startDestinationId) {
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

            binding.toolbarFitLayout.toolBarFitness.navigationIcon?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    appColorHelper.textColor, BlendModeCompat.SRC_ATOP
                )
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // API 5+ solution
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            Utilities.printLogError("requestCode-> $requestCode")
            Utilities.printLogError("resultCode-> $resultCode")
            Utilities.printLogError("data-> $data")

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FitRequestCode.READ_DATA.value) {
                    //refreshAdapter()
                    Utilities.printLogError("onActivityResult READ_DATA")
                    //viewModel.showProgressBar(resources.getString(R.string.SYNCHRONIZING))
                    if (googleAccountListener != null) {
                        googleAccountListener!!.onGoogleAccountSelection(Constants.SUCCESS)
                    }
                    //stepCountHelper.synchronize(this)
                }
            } else {
                FitnessDataManager(this).oAuthErrorMsg(requestCode, resultCode)
                if (googleAccountListener != null) {
                    googleAccountListener!!.onGoogleAccountSelection(Constants.FAILURE)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface OnGoogleAccountSelectListener {
        fun onGoogleAccountSelection(from: String)
    }

    fun setDataReceivedListener(listener: OnGoogleAccountSelectListener) {
        this.googleAccountListener = listener
    }

    fun routeToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

}
