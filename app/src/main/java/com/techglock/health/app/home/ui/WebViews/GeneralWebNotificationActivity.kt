package com.techglock.health.app.home.ui.WebViews

import android.annotation.SuppressLint
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
import com.techglock.health.app.databinding.ActivityGeneralWebNotificationBinding
import com.techglock.health.app.home.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GeneralWebNotificationActivity : BaseActivity() {

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private lateinit var binding: ActivityGeneralWebNotificationBinding
    private val appColorHelper = AppColorHelper.instance!!

    override fun getViewModel(): BaseViewModel = viewModel
    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.webViewGeneralWebNotification.canGoBack()) {
                binding.webViewGeneralWebNotification.goBack()
            } else {
                finish()
            }
        }
    }

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityGeneralWebNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
        try {
            if (intent.hasExtra(Constants.NOTIFICATION_TITLE)) {
                setUpToolbar(intent.getStringExtra(Constants.NOTIFICATION_TITLE)!!)
            } else {
                setUpToolbar("")
            }
            initialiseWebView()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initialiseWebView() {
        viewModel.showProgress()
        setWebViewSettings()
        val urlToLoad = intent.getStringExtra(Constants.WEB_URL)!!
        Utilities.printLogError("UrlToLoad--->$urlToLoad")
        binding.webViewGeneralWebNotification.loadUrl(urlToLoad)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebViewSettings() {
        val settings = binding.webViewGeneralWebNotification.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        binding.webViewGeneralWebNotification.webViewClient = CustomWebViewClient()
        binding.webViewGeneralWebNotification.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.transparent
            )
        )
    }

    inner class CustomWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            Utilities.printLogError("Url--->$url")
            //Utilities.redirectToChrome(url,this@GeneralWebNotificationActivity)
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
        binding.toolBarView.toolbarCommon.navigationIcon?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.textColor, BlendModeCompat.SRC_ATOP
            )

        binding.toolBarView.toolbarCommon.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroy() {
        binding.webViewGeneralWebNotification.destroy()
        super.onDestroy()
    }

}