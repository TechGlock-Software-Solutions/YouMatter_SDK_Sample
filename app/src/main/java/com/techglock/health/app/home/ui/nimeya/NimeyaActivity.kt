package com.techglock.health.app.home.ui.nimeya

import android.os.Bundle
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
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ActivityNimeyaBinding
import com.techglock.health.app.home.viewmodel.NimeyaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NimeyaActivity : BaseActivity() {

    private lateinit var navController: NavController
    private val appColorHelper = AppColorHelper.instance!!
    private val viewModel: NimeyaViewModel by lazy {
        ViewModelProvider(this)[NimeyaViewModel::class.java]
    }
    private lateinit var binding: ActivityNimeyaBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityNimeyaBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_nimeya) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        val bundle = Bundle()
        if (intent.hasExtra(Constants.TO)) {
            val to = intent.getStringExtra(Constants.TO)!!
            Utilities.printLogError("To_1--->" + to)
            bundle.putString(Constants.TO, to)
        }

        navController.setGraph(R.navigation.nav_graph_nimeya, bundle)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            binding.toolBarView.toolbarTitle.text = when (destination.id) {
                R.id.riskoMeterInputFragment -> resources.getString(R.string.TITLE_RISKO_METER)
                R.id.riskoMeterResultFragment -> resources.getString(R.string.TITLE_RISKO_METER)
                R.id.protectoMeterInputFragment -> resources.getString(R.string.TITLE_PROTECTO_METER)
                R.id.protectoMeterResultFragment -> resources.getString(R.string.TITLE_PROTECTO_METER)
                R.id.retiroMeterInputFragment -> resources.getString(R.string.TITLE_RETIRO_METER)
                R.id.retiroMeterResultFragment -> resources.getString(R.string.TITLE_RETIRO_METER)
                else -> ""
            }

            /*            if (destination.id == controller.graph.startDestination) {
                            supportActionBar!!.setDisplayShowTitleEnabled(false)
                            supportActionBar?.setDisplayHomeAsUpEnabled(true)
                            supportActionBar?.setHomeButtonEnabled(true)
                            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
                        } else {
                            supportActionBar!!.setDisplayShowTitleEnabled(false)
                            supportActionBar?.setDisplayHomeAsUpEnabled(true)
                            supportActionBar?.setHomeButtonEnabled(true)
                            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
                        }*/

            supportActionBar!!.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

            binding.toolBarView.toolbarCommon.navigationIcon?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    appColorHelper.textColor, BlendModeCompat.SRC_ATOP
                )
        }

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