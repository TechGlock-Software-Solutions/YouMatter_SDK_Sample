package com.techglock.health.app.home.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ActivityBlogDetailDashbaordBinding
import com.techglock.health.app.home.adapter.BlogSuggestionAdapter
import com.techglock.health.app.home.viewmodel.DashboardViewModel
import com.techglock.health.app.home.views.HomeBinding.setHtmlTxt
import com.techglock.health.app.model.blogs.BlogItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BlogDetailActivity : BaseActivity() {

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private lateinit var binding: ActivityBlogDetailDashbaordBinding

    private var body = ""
    private var blogId = "0"
    private var categoryId = 0

    private var title = ""
    private var desc = ""
    private var link = ""

    private val appColorHelper = AppColorHelper.instance!!
    private var blogAdapter: BlogSuggestionAdapter? = null


    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_blog_detail_dashbaord)
        binding = ActivityBlogDetailDashbaordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            title = intent.getStringExtra(Constants.TITLE)!!
            desc = intent.getStringExtra(Constants.DESCRIPTION)!!
            link = intent.getStringExtra(Constants.LINK)!!
            body = intent.getStringExtra(Constants.BODY)!!
            blogId = intent.getStringExtra(Constants.BLOG_ID)!!
            categoryId = intent.getIntExtra(Constants.CATEGORY_ID, 0)
            Utilities.printLog("categoryId----->$categoryId")
            setupToolbar()
            initialise()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initialise() {
        binding.webViewBlogs.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
        setWebViewSettings(binding.webViewBlogs)

        if (!Utilities.isNullOrEmpty(body)) {
            val html1 = ""
            val html2 = ""
            //val html3 = "%s%s"
            val html4 = "<style>\n" +
                    " img {\n" +
                    "         width:100 %!important;\n" +
                    "         padding:1%;\n" +
                    " }\n" +
                    "</style>"
            val str = String.format(html1, "IRANSansMobile_UltraLight.ttf", 40)
            val content = body.replace("text-align:", "")
                .replace("font-family:", "")
                .replace("line-height:", "")
                .replace("dir=", "")
                //.replace(Regex("height=\"[0-9]+\""), "")
                //.replace(Regex("<img(.*?)src=\"([^\"]+)\"(.*?)>"), "<img src=\"$2\">")
                .replace(
                    Regex("<img(.*?)src=\"([^\"]+)\"(.*?)>"),
                    "<img src=\"$2\" style=\"width:100%\">"
                )
                .replace("Loading Custom Ratings...", "")
            //.replace("width=", "width=\"100%;\"")
            val myHtmlString = str + content + html2
            binding.webViewBlogs.loadDataWithBaseURL(
                null,
                html4 + myHtmlString,
                "text/html",
                "UTF-8",
                null
            )
            //binding.webViewBlogs.loadDataWithBaseURL(null, body, "text/html", "UTF-8", null)
            //binding.webViewBlogs.loadUrl(requireArguments().getString("LINK")!!)
            Utilities.printLog("BLOG_DETAIL-----------\n${(html4 + myHtmlString)}")
        }
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvBlogs.layoutManager = layoutManager
        blogAdapter = BlogSuggestionAdapter(viewModel)
        binding.rvBlogs.adapter = blogAdapter
        viewModel.blogsRelated.observe(this) {}
        viewModel.getRelatedBlogApi(1, blogId, categoryId)


        viewModel.healthBlogSuggestionList.observe(this) {
            it?.let {
                if (blogAdapter != null) {
                    blogAdapter!!.updateData(it)
                }
            }

        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebViewSettings(webView: WebView) {
        val settings = webView.settings

        settings.javaScriptEnabled = true
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        /*settings.setSupportMultipleWindows(true)
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.loadsImagesAutomatically = true
        //settings.lightTouchEnabled = true
        settings.domStorageEnabled = true
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        //settings.defaultFontSize = R.dimen._7sdp
        settings.textSize = WebSettings.TextSize.LARGEST
        //settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        settings.userAgentString = "Android"*/

        webView.webViewClient = WebViewClient()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolBarBlogDetail.toolbarCommon)
        if (intent.hasExtra(Constants.TITLE)) {
            binding.toolBarBlogDetail.toolbarTitle.setHtmlTxt(intent.getStringExtra(Constants.TITLE)!!)
        }
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        binding.toolBarBlogDetail.imgShareBlog.setOnClickListener {
            shareBlog()
        }

        binding.toolBarBlogDetail.toolbarCommon.navigationIcon?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.textColor, BlendModeCompat.SRC_ATOP
            )

        binding.toolBarBlogDetail.toolbarCommon.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun shareBlog() {
        viewModel.shareBlog(
            BlogItem(
                title = title,
                description = desc,
                link = link
            )
        )
    }

    override fun onBackPressed() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
        //finish()
    }
}
