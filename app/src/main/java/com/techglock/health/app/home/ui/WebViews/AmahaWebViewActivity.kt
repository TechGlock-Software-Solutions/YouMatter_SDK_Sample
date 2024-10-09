package com.techglock.health.app.home.ui.WebViews

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ActivityAmahaWebViewBinding
import com.techglock.health.app.home.viewmodel.DashboardViewModel
import com.techglock.health.app.repository.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AmahaWebViewActivity : BaseActivity() {

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private lateinit var binding: ActivityAmahaWebViewBinding
    private val appColorHelper = AppColorHelper.instance!!
//    private val firebaseHelper = FirebaseHelper

    private var moduleCode = ""
    private var title = ""
    private val reqCode = 121
    private lateinit var customWebChromeClient: CustomWebChromeClient
    private var webFilePath: ValueCallback<Array<Uri>>? = null

    //upload multiple files in webview
    private var allowMultiFile = true

    override fun getViewModel(): BaseViewModel = viewModel
    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_amaha_web_view)
        binding = ActivityAmahaWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
        try {
            moduleCode = intent.getStringExtra(Constants.MODULE_CODE)!!
            title = intent.getStringExtra(Constants.TITLE)!!
            Utilities.printLogError("ModuleCode--->$moduleCode")
            Utilities.printLogError("Title--->$title")
//            callFirebaseEvents(moduleCode)
            setupToolbar()
            initialiseWebView()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //FirebaseHelper.logScreenEvent(FirebaseConstants.CONTACT_US_SCREEN)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initialiseWebView() {

        //binding.imgLoading.visibility = View.VISIBLE
        setWebViewSettings()
        binding.webViewAmaha.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
        viewModel.callGetSSOUrlApi(moduleCode)
        viewModel.getSSOUrl.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                if (!Utilities.isNullOrEmpty(it.data!!.sso.ssoUrl)) {
                    binding.webViewAmaha.loadUrl(it.data.sso.ssoUrl)
                    viewModel.callAddFeatureAccessLogApi(
                        moduleCode,
                        Utilities.getDescriptionByModuleCode(moduleCode),
                        "InnerHour/Amaha",
                        it.data.sso.ssoUrl.split("?")[0]
                    )
                } else {
                    viewModel.hideProgressBar()
                }
            }
        }

        viewModel.addFeatureAccessLog.observe(this) {}
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun setWebViewSettings() {
        //This line should be used only in debug mode
        WebView.setWebContentsDebuggingEnabled(true)

        val settings = binding.webViewAmaha.settings
        settings.javaScriptEnabled = true
        settings.userAgentString = "YouMatter"
        //settings.setAppCacheMaxSize(1024*1024*8)
        //settings.setSupportZoom(true)
        //settings.builtInZoomControls = true

        val appCachePath: String = cacheDir.absolutePath
        //settings.setAppCachePath(appCachePath)
        settings.domStorageEnabled = true // Open DOM storage function
        //settings.setAppCacheEnabled(true) //Turn on the H5(APPCache) caching function
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.loadsImagesAutomatically = true
        settings.setGeolocationEnabled(false)
        settings.setNeedInitialFocus(false)
        settings.saveFormData = false
        settings.allowFileAccess = true // Readable file cache
        settings.allowFileAccessFromFileURLs = true
        settings.mediaPlaybackRequiresUserGesture = false

        binding.webViewAmaha.addJavascriptInterface(WebviewJavascriptInterface(this), "YouMatter")

        binding.webViewAmaha.webViewClient = CustomWebViewClient()
        customWebChromeClient = CustomWebChromeClient()
        binding.webViewAmaha.webChromeClient = customWebChromeClient

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

                url.startsWith("intent:") -> {
                    try {
                        //Utilities.printLogError("url--->$url")
                        val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                else -> {
                    view.loadUrl(url)
                }
            }
            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            //Utilities.printLogError("url--->$url")
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            viewModel.hideProgressBar()
            //binding.imgLoading.clearAnimation()
            //binding.imgLoading.visibility = View.GONE
        }

    }

    inner class CustomWebChromeClient : WebChromeClient() {

        override fun onPermissionRequest(request: PermissionRequest) {
            request.grant(request.resources)
        }

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            webFilePath = filePathCallback
            openFileChooser()
            return true
        }

    }

    private fun openFileChooser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Build.MANUFACTURER == "samsung") {
                try {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "*/*"
                    startActivityForResult(Intent.createChooser(intent, "Select File"), reqCode)
                } catch (e: Exception) {
                    val intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Downloads.EXTERNAL_CONTENT_URI)
                    intent.type = "*/*"
                    val mimeTypes = arrayOf(
                        "application/pdf",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "application/msword",
                        "*/*"
                    )
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                    startActivityForResult(Intent.createChooser(intent, "Select File"), reqCode)
                }
            } else {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Downloads.EXTERNAL_CONTENT_URI)
                intent.type = "*/*"
                val mimeTypes = arrayOf(
                    "application/pdf",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/msword",
                    "*/*"
                )
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                startActivityForResult(Intent.createChooser(intent, "Select File"), reqCode)
            }
        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            val mimetypes = arrayOf(
                "application/pdf",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/msword",
                "*/*"
            )
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
            startActivityForResult(intent, reqCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultIntent)
        if (requestCode == reqCode) {
            if (resultCode == RESULT_OK) {
                val array = arrayOf(resultIntent?.data!!)
                webFilePath?.onReceiveValue(array)
            } else {
                webFilePath?.onReceiveValue(arrayOf())
            }
            webFilePath = null
        }
    }

    /*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            try {
                var results: Array<Uri?>? = null
                if (resultCode == RESULT_OK) {
                    if ( requestCode == reqCode ) {
                        if ( null == webFilePath ) {
                            return
                        }
                        if ( intent != null ) {
                            val dataString = intent.dataString
                            if (dataString != null) {
                                results = arrayOf(Uri.parse(dataString))
                            } else {
                                if ( allowMultiFile ) {
                                    if (intent.clipData != null) {
                                        val numSelectedFiles = intent.clipData!!.itemCount
                                        results = arrayOfNulls(numSelectedFiles)
                                        for (i in 0 until numSelectedFiles) {
                                            results[i] = intent.clipData!!.getItemAt(i).uri
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                webFilePath!!.onReceiveValue(results as Array<Uri>)
                //asw_file_path!!.onReceiveValue(results!!)
                webFilePath = null
            } catch ( e : Exception ) {
                e.printStackTrace()
            }
        }*/

    inner class WebviewJavascriptInterface(val mContext: Context) {

        @JavascriptInterface
        fun copyCoupon(coupon: String) {
            (mContext.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.setPrimaryClip(
                ClipData.newPlainText("coupon_code_copy", coupon)
            )
        }

        @JavascriptInterface
        fun downloadFile(downloadUrl: String, downloadFileName: String) {
            downloadUrl.let { url ->
                val request = DownloadManager.Request(Uri.parse(url))
                request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS, downloadFileName
                )
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                (mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(
                    request
                )
            }
        }

        @JavascriptInterface
        fun addToCalendar(bookingDetails: String) {
            val currentBookingName = "booking_"
            getExternalFilesDir(null)?.absolutePath?.let {
                val storageDir = File(it)
                if (!storageDir.exists()) {
                    storageDir.mkdirs()
                }
                val file = File.createTempFile(currentBookingName, ".ics", storageDir)
                file.writeText(bookingDetails)
                val bookingUri = FileProvider.getUriForFile(
                    mContext, applicationContext.packageName.toString() + ".provider", file
                )
                val extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString())
                val mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(bookingUri, mimetype)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }
        }

        @JavascriptInterface
        fun shareProvider(title: String, body: String, url: String) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TITLE, title)
            shareIntent.putExtra(Intent.EXTRA_TEXT, "$body: $url")
            startActivity(Intent.createChooser(shareIntent, "Share Using"))
        }

        @JavascriptInterface
        fun openMap(lat: String, long: String, label: String) {
            try {
                val gmmIntentUri = Uri.parse("geo:<$lat>,<$long>?z=15&q=<$lat>,<$long>($label)")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            } catch (e: Exception) {
                val intent = Intent(
                    Intent.ACTION_VIEW, Uri.parse("geo:<$lat>,<$long>?q=<$lat>,<$long>($label)")
                )
                startActivity(intent)
            }
        }

        @JavascriptInterface
        fun closeWebView() {
            finish()
        }

    }

    /*    @JavascriptInterface
        fun downloadFile(url: String, name: String) {
            downloadUrl = url
            downloadFileName = name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                || ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
                downloadFile()
            } else {
                requestAccessForWriteExternalStorage()
            }
        }*/

    private fun setupToolbar() {
        setSupportActionBar(binding.toolBarView.toolbarAmaha)
        binding.toolBarView.toolbarTitle.text = title
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        binding.toolBarView.toolbarAmaha.navigationIcon?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.textColor, BlendModeCompat.SRC_ATOP
            )

        binding.toolBarView.toolbarAmaha.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            //routeToHomeScreen()
        }
    }


    private fun callFirebaseEvents(code: String) {
        /*when (code) {
            "DASHBOARD_URL" -> firebaseHelper.logScreenEvent(FirebaseConstants.AMAHA_DASHBOARD_SCREEN)
            "BOT_URL" -> firebaseHelper.logScreenEvent(FirebaseConstants.AMAHA_CHATBOT_SCREEN)
            "ASMT_HOME_URL" -> firebaseHelper.logScreenEvent(FirebaseConstants.AMAHA_ASSESSMENT_SCREEN)
            "AUDIO_URL" -> firebaseHelper.logScreenEvent(FirebaseConstants.AMAHA_AUDIOS_SCREEN)
            "VIDEO_URL" -> firebaseHelper.logScreenEvent(FirebaseConstants.AMAHA_VIDEOS_SCREEN)
            "BLOGS_URL" -> firebaseHelper.logScreenEvent(FirebaseConstants.AMAHA_BLOGS_SCREEN)
        }*/
    }

}