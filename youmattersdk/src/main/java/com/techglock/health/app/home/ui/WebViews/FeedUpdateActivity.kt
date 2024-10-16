package com.techglock.health.app.home.ui.WebViews

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ActivityFeedUpdateBinding
import com.techglock.health.app.home.ui.HomeMainActivity
import com.techglock.health.app.home.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedUpdateActivity : BaseActivity() {

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private lateinit var binding: ActivityFeedUpdateBinding
    private val appColorHelper = AppColorHelper.instance!!

    private var urlToLoad = ""
    private var title = ""
    private var from = ""

    override fun getViewModel(): BaseViewModel = viewModel

    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.webViewFeed.canGoBack()) {
                binding.webViewFeed.goBack()
            } else {
                //finish()
                when(from) {
                    "POLICY" -> finish()
                    else-> routeToHomeScreen()
                }
            }
        }
    }

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityFeedUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
        try {
            if (intent.hasExtra(Constants.FROM)) {
                from = intent.getStringExtra(Constants.FROM)!!
            }
            if (intent.hasExtra(Constants.TITLE)) {
                title = intent.getStringExtra(Constants.TITLE)!!
            }
            if (intent.hasExtra(Constants.WEB_URL)) {
                urlToLoad = intent.getStringExtra(Constants.WEB_URL)!!
            }
            Utilities.printLogError("From--->$from")
            Utilities.printLogError("Title--->$title")
            Utilities.printLogError("UrlToLoad--->$urlToLoad")
            setUpToolbar(title)
            initialiseWebView()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initialiseWebView() {
        viewModel.showProgress()
        setWebViewSettings()
        binding.webViewFeed.loadUrl(urlToLoad)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebViewSettings() {
        val settings = binding.webViewFeed.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        binding.webViewFeed.webViewClient = CustomWebViewClient()
        binding.webViewFeed.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
    }

    inner class CustomWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            Utilities.printLogError("Url--->$url")
            //Utilities.redirectToChrome(url,this@FeedUpdateActivity)
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            viewModel.hideProgressBar()
        }

    }

    private fun setUpToolbar(title: String) {
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        binding.toolBarView.toolbarTitle.text = title
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding.toolBarView.toolbarCommon.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(appColorHelper.textColor, BlendModeCompat.SRC_ATOP)

        binding.toolBarView.toolbarCommon.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroy() {
        binding.webViewFeed.destroy()
        super.onDestroy()
    }

    private fun routeToHomeScreen() {
        //openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
        val intent = Intent(this,HomeMainActivity::class.java)
        startActivity(intent)
    }

}