package com.techglock.health.app.hra

import android.os.Bundle
import android.view.MenuItem
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
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
import com.techglock.health.app.databinding.ActivityHraHomeBinding
import com.techglock.health.app.hra.viewmodel.HraViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HraHomeActivity : BaseActivity() {

    private val viewModel: HraViewModel by lazy {
        ViewModelProvider(this)[HraViewModel::class.java]
    }
    private lateinit var navController: NavController
    private val appColorHelper = AppColorHelper.instance!!
    private lateinit var binding: ActivityHraHomeBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        try {
            LocaleHelper.onAttach(this, LocaleHelper.getLanguage(this))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding = ActivityHraHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBarView.toolbarHra)
        // Setting up a back button
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_hra) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        val bundle = Bundle()
        Utilities.printLogError("intent--->$intent")
        if (intent.hasExtra(Constants.SCREEN)) {
            bundle.putString(Constants.SCREEN, intent.getStringExtra(Constants.SCREEN))
            if (intent.hasExtra(Constants.NOTIFICATION_MESSAGE)) {
                bundle.putString(
                    Constants.NOTIFICATION_MESSAGE,
                    intent.getStringExtra(Constants.NOTIFICATION_MESSAGE)
                )
            }
        }
        navController.setGraph(R.navigation.nav_graph_hra_feature, bundle)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
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

            //binding.toolBarView.toolbarHra.setBackgroundColor(ContextCompat.getColor(this,R.color.white))

            binding.toolBarView.toolbarHra.navigationIcon?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    appColorHelper.textColor, BlendModeCompat.SRC_ATOP
                )
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.nav_host_fragment_hra).navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
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

    /*    override fun onBackPressed() {
           routeToHomeScreen()
        }*/

}
