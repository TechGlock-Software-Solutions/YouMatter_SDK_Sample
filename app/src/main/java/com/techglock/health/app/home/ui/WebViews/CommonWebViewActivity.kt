package com.techglock.health.app.home.ui.WebViews

import android.app.DownloadManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ActivityCommonWebViewBinding
import com.techglock.health.app.home.viewmodel.WebViewViewModel
import dagger.hilt.android.AndroidEntryPoint
import im.delight.android.webview.AdvancedWebView
import java.io.File

lateinit var advancedWebView: AdvancedWebView

@AndroidEntryPoint
class CommonWebViewActivity : BaseActivity(), AdvancedWebView.Listener {

    private lateinit var binding: ActivityCommonWebViewBinding
    private val viewModel: WebViewViewModel by lazy {
        ViewModelProvider(this)[WebViewViewModel::class.java]
    }

    private var strWebUrl = ""
    private var hasCookies: Boolean = false
    private var encodedString = ""
    private var toolbarTitle = ""
    private val appColorHelper = AppColorHelper.instance!!
    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_common_web_view)
        binding = ActivityCommonWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
        initialise()
        setupToolbar()
    }

    override fun getViewModel(): BaseViewModel = viewModel

    private fun initialise() {
        toolbarTitle = intent.getStringExtra(Constants.Toobar_Title)!!
        strWebUrl = intent.getStringExtra(Constants.WEB_URL)!!
        hasCookies = intent.getBooleanExtra(Constants.HAS_COOKIES, false)
        Utilities.printLog("WebUrl , HasCookies -----> $strWebUrl , $hasCookies , $toolbarTitle")

        advancedWebView = findViewById(R.id.webView)

//        val animation = AnimationUtils.loadAnimation(this, R.anim.rotate_forward)
//        binding.imgLoader.startAnimation(animation)
//        binding.imgLoader.visibility = View.VISIBLE

        advancedWebView.setBackgroundColor(ContextCompat.getColor(this, R.color.background_color))
        advancedWebView.setListener(this, this)
        advancedWebView.loadUrl(strWebUrl)
        advancedWebView.setGeolocationEnabled(false)
        advancedWebView.setMixedContentAllowed(false)
        advancedWebView.setCookiesEnabled(true)
        advancedWebView.setThirdPartyCookiesEnabled(true)
        advancedWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                //Toast.makeText(this@MainActivity, "Finished loading", Toast.LENGTH_SHORT).show()
            }
        }
        advancedWebView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)
                //Toast.makeText(this@MainActivity, title, Toast.LENGTH_SHORT).show()
            }
        }

        advancedWebView.settings.javaScriptEnabled = true
        setRequestHandler()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolBarViewWellness.toolbarCommon)
        binding.toolBarViewWellness.toolbarTitle.text = toolbarTitle
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        binding.toolBarViewWellness.toolbarCommon.navigationIcon?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.textColor,
                BlendModeCompat.SRC_ATOP
            )

        binding.toolBarViewWellness.toolbarCommon.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        binding.webView.onActivityResult(requestCode, resultCode, intent)
    }

    override fun onPageStarted(url: String?, favicon: Bitmap?) {
    }

    override fun onPageFinished(url: String?) {
//        binding.imgLoader.clearAnimation()
//        binding.imgLoader.visibility = View.GONE
        binding.animationView.repeatCount = 1
    }

    override fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {
    }


    override fun onDownloadRequested(
        url: String?,
        suggestedFilename: String?,
        mimeType: String?,
        contentLength: Long,
        contentDisposition: String?,
        userAgent: String?
    ) {
        /*Utilities.printLog(
            "MAIN_ACTIVITY",
            "onDownloadRequested(url = " + url + ",  suggestedFilename = " + suggestedFilename + ",  mimeType = " + mimeType + ",  contentLength = " + contentLength + ",  contentDisposition = " + contentDisposition + ",  userAgent = " + userAgent + ")"
        )*/

        val request = DownloadManager.Request(Uri.parse(url))
        val mimeType = "application/pdf"
        request.setMimeType(mimeType)
        request.setDestinationUri(Uri.fromFile(File(Environment.DIRECTORY_DOWNLOADS)))
        val cookies: String = CookieManager.getInstance().getCookie(url)

        request.addRequestHeader("cookie", cookies)
        request.addRequestHeader("User-Agent", userAgent)
        request.setDescription("Downloading file...")
        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType))
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            URLUtil.guessFileName(url, contentDisposition, mimeType)
        )
        val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
        Toast.makeText(applicationContext, "Downloading File...", Toast.LENGTH_LONG).show()
    }

    override fun onExternalPageRequest(url: String?) {
    }

    private fun setRequestHandler() {
        Handler().postDelayed({
            class MyJavaScriptInterface(context: Context?) {
                val mContext = context;

                @JavascriptInterface
                fun makeToast(content: String) {
                    try {
                        Utilities.printLog("WEB_VIEW--->Reached here--->$content")
                        sendDebugNotification(mContext, content)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            advancedWebView.addJavascriptInterface(MyJavaScriptInterface(this), "app")
        }, 500)
    }

    fun sendDebugNotification(context: Context?, msg: String?) {
        try {
            if (context != null) {
                val alarmNotificationManager =
                    context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                //Added new : In Android "O" or higher version, it's Mandatory to use a channel with your Notification Builder
                val channelId = "channel_debug" // The id of the channel.
                val name: CharSequence = "Debug" // The user-visible name of the channel.
                val importance = NotificationManager.IMPORTANCE_HIGH
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val mChannel = NotificationChannel(channelId, name, importance)
                    alarmNotificationManager.createNotificationChannel(mChannel)
                }
                if (alarmNotificationManager != null) {
                    val intent = Intent()
                    intent.component =
                        ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)

                    //get pending intent
                    val pendingIntent = PendingIntent.getActivity(
                        context,
                        111111,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                    val alarmSound =
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                    //Create notification
                    val alarmNotificationBuilder: NotificationCompat.Builder =
                        NotificationCompat.Builder(context, channelId)
                            .setContentTitle("Debug Notification")
                            .setSmallIcon(R.drawable.notification_logo)
                            //.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                            //.setLargeIcon(BitmapFactory.decodeResource(mContext.resources, R.mipmap.ic_launcher))
                            .setStyle(NotificationCompat.BigTextStyle().bigText(msg))
                            .setContentText(msg)
                            .setSound(alarmSound)
                            .setAutoCancel(true)
                            .setChannelId(channelId)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setPriority(Notification.PRIORITY_HIGH)
                    alarmNotificationBuilder.setContentIntent(pendingIntent)
                    alarmNotificationManager.notify(111111, alarmNotificationBuilder.build())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
