package com.techglock.health.app.track_parameter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.fitness.FitRequestCode
import com.techglock.health.app.common.fitness.FitnessDataManager
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ActivityParameterHomeBinding
import com.techglock.health.app.track_parameter.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ParameterHomeActivity : BaseActivity() {

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val appColorHelper = AppColorHelper.instance!!
    private lateinit var binding: ActivityParameterHomeBinding

    private var googleAccountListener: OnGoogleAccountSelectListener? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        try {
            LocaleHelper.onAttach(this, LocaleHelper.getLanguage(this))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //setContentView(R.layout.activity_parameter_home)
        binding = ActivityParameterHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarLayout.toolBarParameter
        setSupportActionBar(binding.toolbarLayout.toolBarParameter)
        // Setting up a back button
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_paremeter) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        val bundle = Bundle()
        if (intent.hasExtra(Constants.FROM)) {
            when (intent.getStringExtra(Constants.FROM)) {
                "DashboardBP" -> {
                    bundle.putString(Constants.FROM, "DashboardBP")
                }

                "DashboardBMI" -> {
                    bundle.putString(Constants.FROM, "DashboardBMI")
                }
            }
        } else if (intent.hasExtra(Constants.SCREEN)) {
            bundle.putString(Constants.SCREEN, intent.getStringExtra(Constants.SCREEN))
        }
        navController.setGraph(R.navigation.track_paramter_nav_graph, bundle)



        navController.addOnDestinationChangedListener { controller, destination, _ ->

            binding.toolbarLayout.toolbarTitle.text = when (destination.id) {
                R.id.selectProfileFragment -> resources.getString(R.string.TITLE_SELECT_PARAMETERS)
                R.id.updateParameterFragment -> resources.getString(R.string.TITLE_UPDATE_PARAMETERS)
                R.id.trackDashboardFragment -> resources.getString(R.string.TITLE_TRACK_HEALTH_PARAMETERS)
                R.id.currentHistoryFragment -> resources.getString(R.string.TITLE_PARAMETER_HISTORY)
//                R.id.updateParameterFrag -> resources.getString(R.string.TITLE_UPDATE_PARAMETERS)
//                R.id.historyFragment -> resources.getString(R.string.TITLE_PARAMETER_HISTORY)
//                R.id.detailHistoryFragment -> resources.getString(R.string.TITLE_COMPLETE_HISTORY)
                else -> resources.getString(R.string.TITLE_TRACK_HEALTH_PARAMETERS)
            }

            /*            if (destination.id == R.id.updateParameterFrag) {
                            toolBarParameter.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
                        } else {
                            toolBarParameter.setBackgroundColor(ContextCompat.getColor(this,R.color.background_color))
                        }*/

            /*            if (destination.id == R.id.homeFragment) {
                            toolbarLayout.visibility = View.GONE
                        } else {
                            toolbarLayout.visibility = View.VISIBLE
                        }*/

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
            binding.toolbarLayout.toolBarParameter.navigationIcon?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    appColorHelper.textColor, BlendModeCompat.SRC_ATOP
                )
        }

        binding.toolbarLayout.imgVivantLogo.setOnClickListener {
//            openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
            finish()
        }
//        FirebaseHelper.logScreenEvent(FirebaseConstants.HEALTH_PARAMETERS_TRACKER_SCREEN)
    }

    fun setTitle(title: String) {
        binding.toolbarLayout.toolbarTitle.text = title
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

    /*fun routeToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }*/

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                // API 5+ solution
//                onBackPressed()
                onSupportNavigateUp()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }*/

    fun setDataReceivedListener(listener: OnGoogleAccountSelectListener) {
        this.googleAccountListener = listener
    }

    interface OnGoogleAccountSelectListener {
        fun onGoogleAccountSelection(from: String)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            Utilities.printLogError("requestCode-> $requestCode")
            Utilities.printLogError("resultCode-> $resultCode")
            Utilities.printLogError("data-> $data")

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FitRequestCode.READ_DATA.value) {
                    Utilities.printLogError("onActivityResult READ_DATA")
                    if (googleAccountListener != null) {
                        googleAccountListener!!.onGoogleAccountSelection(Constants.SUCCESS)
                    }
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

}
