package com.techglock.health.app.home.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ActivityTermsConditionBinding
import com.techglock.health.app.home.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TermsConditionActivity : BaseActivity() {

    private val viewModel: SettingsViewModel by lazy {
        ViewModelProvider(this)[SettingsViewModel::class.java]
    }

    //    private val backGroudCallViewModel: BackgroundCallViewModel by viewModel()
    private lateinit var binding: ActivityTermsConditionBinding

    private val appColorHelper = AppColorHelper.instance!!
    private var from = ""
    private var url = ""
    private var title = ""

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_terms_condition)
        binding = ActivityTermsConditionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (intent.hasExtra(Constants.FROM)) {
            from = intent.getStringExtra(Constants.FROM)!!
        }
        val data = HashMap<String, Any>()
        data[CleverTapConstants.FROM] = CleverTapConstants.DASHBOARD
        when (from) {
            Constants.TERMS_CONDITIONS -> {
                url = Constants.TERMS_CONDITIONS_API
                title = viewModel.localResource.getString(R.string.TERMS_AND_CONDITIONS)
                CleverTapHelper.pushEventWithProperties(
                    this,
                    CleverTapConstants.TERMS_CONDITIONS_SCREEN,
                    data,
                    false
                )
            }

            Constants.PRIVACY_POLICY -> {
                url = Constants.PRIVACY_POLICY_API
                title = viewModel.localResource.getString(R.string.PRIVACY_POLICY)
                CleverTapHelper.pushEventWithProperties(
                    this,
                    CleverTapConstants.PRIVACY_POLICY_SCREEN,
                    data,
                    false
                )
            }
        }
        Utilities.printLogError("From--->$from")
        Utilities.printLogError("Url--->$url")
        setUpToolbar(title)
        initialiseWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initialiseWebView() {
        //val animation = AnimationUtils.loadAnimation(this, R.anim.rotate_forward)
        //binding.imgLoading.startAnimation(animation)
        binding.imgLoading.visibility = View.VISIBLE

        /*        binding.webViewTerms.webViewClient = CustomWebViewClient()
                val webSettings = binding.webViewTerms.settings
                webSettings.javaScriptEnabled = true*/

        setWebViewSettings(binding.webViewTerms)
        //binding.webViewTerms.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
        binding.webViewTerms.loadUrl(url)

        /*        viewModel.getTermsAndConditionsData(true)
                viewModel.termsConditions.observe(this) {
                    if (it != null) {
                        if ( it.termsConditions?.description != null ) {
                            binding.webViewTerms.loadDataWithBaseURL(null, it.termsConditions?.description!!, "text/html", "UTF-8", null)
                        }
                    }
                }*/
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebViewSettings(webView: WebView) {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        //settings.setSupportZoom(true)
        //settings.builtInZoomControls = true
        binding.webViewTerms.webViewClient = CustomWebViewClient()
    }

    inner class CustomWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            when {
                url.startsWith("tel:") -> {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    startActivity(intent)
                    return true
                }

                url.startsWith("mailto:") -> {
                    var mUrl = url
                    mUrl = mUrl.substring(7)
                    val mail = Intent(Intent.ACTION_SEND)
                    mail.type = "application/octet-stream"
                    mail.putExtra(Intent.EXTRA_EMAIL, arrayOf(mUrl))
                    startActivity(mail)
                    return true
                }

                else -> {
                    view.loadUrl(url)
                }
            }
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            binding.imgLoading.clearAnimation()
            binding.imgLoading.visibility = View.GONE
        }

    }

    private fun setUpToolbar(title: String) {
        setSupportActionBar(binding.toolBarViewTerms.toolbarCommon)
        binding.toolBarViewTerms.toolbarTitle.text = title
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding.toolBarViewTerms.toolbarCommon.navigationIcon?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.textColor, BlendModeCompat.SRC_ATOP
            )

        binding.toolBarViewTerms.toolbarCommon.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        val code = LocaleHelper.getLanguage(this)
        Utilities.printLogError("LanguageCode--->$code")
        LocaleHelper.setLocale(this, code)
        finish()
    }

}