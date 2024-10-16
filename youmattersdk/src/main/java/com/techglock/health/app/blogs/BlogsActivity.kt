package com.techglock.health.app.blogs

import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.techglock.health.app.R
import com.techglock.health.app.blogs.viewmodel.BlogViewModel
import com.techglock.health.app.blogs.views.BlogsBinding.setHtmlTxt
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.databinding.ActivityBlogsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BlogsActivity : BaseActivity() {

    private lateinit var navController: NavController
    private val appColorHelper = AppColorHelper.instance!!
    private val viewModel: BlogViewModel by lazy {
        ViewModelProvider(this)[BlogViewModel::class.java]
    }

    private lateinit var binding: ActivityBlogsBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityBlogsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBarView.toolbarBlogs)
        // Setting up a back button
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_blogs) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        val bundle = Bundle()
        if (intent.hasExtra(Constants.SCREEN)) {
            bundle.putString(Constants.SCREEN, intent.getStringExtra(Constants.SCREEN))
        }
        navController.setGraph(R.navigation.nav_graph_blogs_feature, bundle)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            binding.toolBarView.toolbarTitle.text = when (destination.id) {
                R.id.blogsDashboardFragment -> resources.getString(R.string.HEALTH_LIBRARY)
                //R.id.blogDetailFragment -> resources.getString(R.string.BLOG_DETAILS)
                else -> ""
            }

            when (destination.id) {
                R.id.blogDetailFragment -> {
                    binding.toolBarView.toolbarTitle.gravity = Gravity.START
                }

                else -> {
                    binding.toolBarView.toolbarTitle.gravity = Gravity.CENTER
                }
            }

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
            //toolbar_blogs.setBackgroundColor(ContextCompat.getColor(this,R.color.background_color))
            binding.toolBarView.toolbarBlogs.navigationIcon?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    appColorHelper.textColor, BlendModeCompat.SRC_ATOP
                )
        }

        binding.toolBarView.imgVivantLogo.setOnClickListener {
            openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
        }
//        FirebaseHelper.logScreenEvent(FirebaseConstants.BLOGS_SCREEN)
    }

    fun setTitle(title: String) {
        binding.toolBarView.toolbarTitle.setHtmlTxt(title)
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

}
