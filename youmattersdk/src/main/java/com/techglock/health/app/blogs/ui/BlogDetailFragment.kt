package com.techglock.health.app.blogs.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.blogs.BlogsActivity
import com.techglock.health.app.blogs.adapter.BlogSuggestionAdapter
import com.techglock.health.app.blogs.viewmodel.BlogViewModel
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentBlogDetailBinding
import com.techglock.health.app.model.blogs.BlogItem
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BlogDetailFragment : BaseFragment() {

    private val viewModel: BlogViewModel by lazy {
        ViewModelProvider(this)[BlogViewModel::class.java]
    }

    private lateinit var binding: FragmentBlogDetailBinding

    private var tab = 0
    private var body = ""
    private var title = ""
    private var desc = ""
    private var link = ""
    private var categoryId = 508

    //private var currentPage = 0
    private var blogId = "0"
    private var page = 1
    private var blogAdapter: BlogSuggestionAdapter? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

        try {
            // Callback to Handle back button event
            val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val bundle = Bundle()
                    bundle.putString(Constants.SCREEN, Constants.BACK)
                    bundle.putInt(Constants.TAB, tab)
                    findNavController().navigate(
                        R.id.action_blogDetailFragment_to_blogsDashboardFragment,
                        bundle
                    )
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlogDetailBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel
        //binding.lifecycleOwner = viewLifecycleOwner

        body = requireArguments().getString(Constants.BODY, "")!!
        blogId = requireArguments().getString(Constants.BLOG_ID, "0")!!
        title = requireArguments().getString(Constants.TITLE, "")!!
        desc = requireArguments().getString(Constants.DESCRIPTION, "")!!
        link = requireArguments().getString(Constants.LINK, "")!!
        categoryId = requireArguments().getInt(Constants.CATEGORY_ID, 0)

        tab = requireArguments().getInt(Constants.TAB, 0)
        viewModel.tabNumber = tab
        Utilities.printLogError("tab--->$tab")

        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        (activity as BlogsActivity).setTitle(title)
        binding.btnShare.visibility = View.GONE
        binding.txtSuggestedForYou.visibility = View.GONE
        setWebViewSettings(binding.webViewBlogs)
        binding.webViewBlogs.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.transparent
            )
        )

        //Utilities.printLog("Body----->"+body )
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
        }

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvBlogs.layoutManager = layoutManager
        blogAdapter = BlogSuggestionAdapter(this, viewModel)
        binding.rvBlogs.adapter = blogAdapter
        viewModel.blogsRelated.observe(viewLifecycleOwner) {}
        viewModel.healthBlogSuggestionList.observe(viewLifecycleOwner) {
            if (it != null) {
                blogAdapter!!.updateData(it)
            }
        }
        viewModel.getRelatedBlogApi(page, blogId, categoryId)
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

        //webView.webViewClient = WebViewClient()
        webView.webViewClient = object : WebViewClient() {

            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
//                binding.btnShare.visibility = View.VISIBLE
                binding.txtSuggestedForYou.visibility = View.VISIBLE
            }

        }
    }

    private fun setClickable() {

        binding.btnShare.setOnClickListener {
            shareBlog()
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

    /*    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
            inflater.inflate(R.menu.options_menu_blog, menu)
            var drawable: Drawable = menu.findItem(R.id.action_share_blog).icon
            drawable = DrawableCompat.wrap(drawable)
            DrawableCompat.setTint(drawable, appColorHelper.textColor)
            super.onCreateOptionsMenu(menu, inflater)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == R.id.action_share_blog) {
                shareBlog()
            }
            return super.onOptionsItemSelected(item)
        }*/

}
