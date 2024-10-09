package com.techglock.health.app.home.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentDisclaimerBinding
import com.techglock.health.app.home.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisclaimerFragment : BaseFragment() {

    private lateinit var binding: FragmentDisclaimerBinding

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }

    override fun getViewModel(): BaseViewModel = viewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utilities.printLogError("Inside=> onCreate")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeMainActivity).setToolbarInfo(
            0,
            showAppLogo = false,
            title = getString(R.string.DISCLAIMER),
            showBg = false,
            showBottomNavigation = false
        )
        Utilities.printLog("Inside=> onViewCreated")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDisclaimerBinding.inflate(inflater, container, false)
        try {
            initialise()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Utilities.printLogError("Inside=> onCreateView")
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initialise() {

        setWebViewSettings(binding.disclaimerWV)
        binding.disclaimerWV.setBackgroundColor(
            ContextCompat.getColor(
                this.requireContext(),
                R.color.transparent
            )
        )
        binding.disclaimerWV.loadUrl(Constants.CENTURION_DISCLAIMER_URL)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebViewSettings(webView: WebView) {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        binding.disclaimerWV.webViewClient = CustomWebViewClient()
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


}