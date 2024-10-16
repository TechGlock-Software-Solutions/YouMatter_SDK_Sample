package com.techglock.health.app.records_tracker

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
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ActivityHealthRecordsBinding
import com.techglock.health.app.records_tracker.viewmodel.HealthRecordsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HealthRecordsActivity : BaseActivity() {

    private val viewModel: HealthRecordsViewModel by lazy {
        ViewModelProvider(this)[HealthRecordsViewModel::class.java]
    }
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val appColorHelper = AppColorHelper.instance!!
    private lateinit var binding: ActivityHealthRecordsBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        try {
            LocaleHelper.onAttach(this, LocaleHelper.getLanguage(this))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //setContentView(R.layout.activity_health_records)
        binding = ActivityHealthRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBarView.toolbarShr)
        // Setting up a back button
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_shr) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        val bundle = Bundle()
        if (intent.hasExtra(Constants.FROM) && !Utilities.isNullOrEmpty(
                intent.getStringExtra(
                    Constants.FROM
                )
            )
        ) {
            when (intent.getStringExtra(Constants.FROM)) {
                Constants.MEDICATION -> bundle.putString(Constants.FROM, Constants.MEDICATION)
                Constants.TRACK_PARAMETER -> bundle.putString(
                    Constants.FROM,
                    Constants.TRACK_PARAMETER
                )
            }
        }

        navController.setGraph(R.navigation.nav_graph_shr_feature, bundle)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            binding.toolBarView.toolbarTitle.text = when (destination.id) {
                //R.id.digitizedRecordsListFragment -> resources.getString(R.string.TITLE_DIGITIZED_RECORDS)
                R.id.viewRecordsFragment -> resources.getString(R.string.TITLE_MY_HEALTH_RECORDS)
                //R.id.fragmentDigitize -> resources.getString(R.string.TITLE_DIGITIZED_RECORD)
                else -> resources.getString(R.string.TITLE_ADD_RECORDS)
            }

            /*            if (destination.id == R.id.viewRecordsFragment) {
                            toolbar_shr.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
                        } else {
                            toolbar_shr.setBackgroundColor(ContextCompat.getColor(this,R.color.background_color))
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

            binding.toolBarView.toolbarShr.navigationIcon?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    appColorHelper.textColor, BlendModeCompat.SRC_ATOP
                )
        }

        binding.toolBarView.imgVivantLogo.setOnClickListener {
            openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
        }
//        FirebaseHelper.logScreenEvent(FirebaseConstants.HEALTH_RECORDS_SCREEN)
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

    fun routeToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

}
