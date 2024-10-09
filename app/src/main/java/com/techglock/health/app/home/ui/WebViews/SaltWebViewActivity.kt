package com.techglock.health.app.home.ui.WebViews

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ActivitySaltWebViewBinding
import com.techglock.health.app.home.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class SaltWebViewActivity : BaseActivity() {

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private lateinit var binding: ActivitySaltWebViewBinding
    private val appColorHelper = AppColorHelper.instance!!

    override fun getViewModel(): BaseViewModel = viewModel
    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivitySaltWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
        try {
            initialiseWebView()
            setUpToolbar()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initialiseWebView() {
        viewModel.showProgress()

        binding.webViewSalt.settings.javaScriptEnabled = true
        binding.webViewSalt.addJavascriptInterface(JSBridge(this), "JSBridge")
        binding.webViewSalt.webViewClient = CustomWebViewClient()

        val mptResultPageUrl = Utilities.getUserPreference(PreferenceConstants.MPT_RESULT_PAGE_URL)
        if (!Utilities.isNullOrEmpty(mptResultPageUrl)) {
            Utilities.printLogError("UrlToLoad--->$mptResultPageUrl")
            binding.webViewSalt.loadUrl(mptResultPageUrl)
        } else {
            binding.webViewSalt.clearCache(true)
            binding.webViewSalt.clearFormData()
            binding.webViewSalt.clearHistory()
            binding.webViewSalt.clearSslPreferences()
            val mptUrl = "${Constants.SALT_MPT_API}?phone=${viewModel.phone}"
            Utilities.printLogError("UrlToLoad--->$mptUrl")
            binding.webViewSalt.loadUrl(mptUrl)
        }
    }

    inner class CustomWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            viewModel.hideProgressBar()
        }

    }

    /**
     * Receive message from webView and pass on to native.
     */
    class JSBridge(val context: Context) {
        @JavascriptInterface
        fun showMessageInNative(resultData:String) {
            try {
                val data = JSONObject(resultData)
                Utilities.storeUserPreference(PreferenceConstants.MPT_RESULT_ID,data.getString("resultId"))
                Utilities.storeUserPreference(PreferenceConstants.MPT_RESULT_PAGE_URL,data.getString("resultPageUrl"))
                Utilities.storeUserPreference(PreferenceConstants.MPT_COHORT_TITLE,data.getString("cohortTitle"))
                Utilities.storeUserPreference(PreferenceConstants.MPT_COHORT_ICON_URL,data.getString("cohortIconUrl"))
                val cleverTapData = HashMap<String, Any>()
                cleverTapData[CleverTapConstants.FROM] = CleverTapConstants.VIEW_RESULT
                CleverTapHelper.pushEventWithProperties(context,CleverTapConstants.SALT_MPT,cleverTapData)

                val saltData = HashMap<String, Any>()
                saltData[CleverTapConstants.VALUE] = data.getString("cohortTitle")
                CleverTapHelper.pushEventWithProperties(context,CleverTapConstants.SALT_MPT_INFO,saltData)
            } catch ( e:Exception ) {
                e.printStackTrace()
            }
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        binding.toolBarView.toolbarTitle.text = resources.getString(R.string.MONEY_PERSONALITY_TEST)
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

}