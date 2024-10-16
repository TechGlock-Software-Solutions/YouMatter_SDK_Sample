package com.techglock.health.app.blogs.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.blogs.adapter.BlogAdapter
import com.techglock.health.app.blogs.adapter.BlogAdapter.OnBottomReachedListener
import com.techglock.health.app.blogs.viewmodel.BlogViewModel
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentBlogsDashboardBinding
import com.techglock.health.app.model.blogs.BlogItem
import com.techglock.health.app.model.blogs.BlogsCategoryModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BlogDashboardFragment : BaseFragment() {

    private val viewModel: BlogViewModel by lazy {
        ViewModelProvider(this)[BlogViewModel::class.java]
    }

    private lateinit var binding: FragmentBlogsDashboardBinding

    private var tab = 0
    private var screen = ""
    private var categoryId = 0
    private var blogAdapter: BlogAdapter? = null
    private var currentPage = 0
    private var page = 1
    private var searchPage = 1
    private var isBackRespOver = true
    private var categoryList: MutableList<BlogsCategoryModel.Category> = mutableListOf()
    private var animation: LayoutAnimationController? = null

    private var mLayoutManager: LinearLayoutManager? = null
    //private var loading = true
    //private var pastVisibleItems = 0
    //private var visibleItemCount:Int = 0
    //private var totalItemCount:Int = 0

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            arguments?.let {
                screen = it.getString(Constants.SCREEN, "")!!
                Utilities.printLogError("screen--->$screen")
                tab = requireArguments().getInt(Constants.TAB, 0)
                Utilities.printLogError("tab--->$tab")
                if (tab != 0 && screen.equals(Constants.BACK, ignoreCase = true)) {
                    isBackRespOver = false
                }
            }

            // Callback to Handle back button event
            val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    //(activity as BlogsActivity).routeToHomeScreen()
                    requireActivity().finish()
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
        binding = FragmentBlogsDashboardBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel
        //binding.lifecycleOwner = viewLifecycleOwner
        try {
            initialise()
            setClickable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {
        binding.layoutSearchBlog.visibility = View.GONE
        startShimmer()
        animation =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_from_bottom)
        viewModel.callListBlogsCategory()

        blogAdapter = BlogAdapter(this, viewModel)
        mLayoutManager = LinearLayoutManager(requireContext())
        binding.rvBlogs.layoutManager = mLayoutManager
        binding.rvBlogs.adapter = blogAdapter

        viewModel.listBlogsCategory.observe(viewLifecycleOwner) {
            if (it != null) {
                Utilities.printLogError("CategoryList--->${it.categoryList.size}")
                categoryList.clear()
                categoryList.add(
                    BlogsCategoryModel.Category(
                        id = 0,
                        name = resources.getString(R.string.FOR_YOU)
                    )
                )
                categoryList.add(
                    BlogsCategoryModel.Category(
                        id = 1,
                        name = resources.getString(R.string.ALL)
                    )
                )
                categoryList.addAll(it.categoryList)
                categoryList =
                    categoryList.filter { cat -> !cat.name.equals("Health A-Z", ignoreCase = true) }
                        .toMutableList()
                for (category in categoryList) {
                    category.name = category.name.replace("&amp;", "&")
                    //Utilities.printLogError("category--->$category")
                    binding.tabLayout.addTab(binding.tabLayout.newTab().setText(category.name))
                }
                tab = requireArguments().getInt(Constants.TAB, 0)
                Utilities.printLogError("tabAfterResp--->$tab")
                isBackRespOver = true

                //Handler().postDelayed({  binding.tabLayout.getTabAt(tab)!!.select() },100)
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.tabLayout.getTabAt(tab)!!.select()
                }, 100)
                /*                if (tab != 0) {
                                    binding.tabLayout.getTabAt(tab)!!.select()
                                }*/
            }
        }

        viewModel.blogList.observe(viewLifecycleOwner) {}
        viewModel.searchBlogs.observe(viewLifecycleOwner) {}
        viewModel.blogsListByCategory.observe(viewLifecycleOwner) {}
        viewModel.blogsSuggestionList.observe(viewLifecycleOwner) {
            if (it != null) {
                Utilities.printLog("BlogsSize=> " + it.blogs.size)
                if (it.blogs.isEmpty()) {
                    binding.rvBlogs.visibility = View.GONE
                    binding.noBlogsSuggestion.visibility = View.VISIBLE
                } else {
                    binding.rvBlogs.visibility = View.VISIBLE
                    binding.noBlogsSuggestion.visibility = View.GONE
                }
            }
        }

    }

    private fun setClickable() {

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tb: TabLayout.Tab) {
                tab = tb.position
                if (tb.position != 0) {
                    binding.rvBlogs.visibility = View.VISIBLE
                    binding.noBlogsSuggestion.visibility = View.GONE
                }
                val category = categoryList[tb.position]
                Utilities.printData("", category, true)
                categoryId = category.id
                currentPage = 0
                page = 1
                searchPage = 1
                Utilities.hideKeyboard(requireActivity())
                startShimmer()
                blogAdapter!!.clearAdapterList()
                Utilities.printLogError("isBackRespOver--->$isBackRespOver")
                CleverTapHelper.pushEvent(requireContext(), getEventByBlogCategory(categoryId))
                if (categoryId == 1) {
                    binding.layoutSearchBlog.visibility = View.VISIBLE
                    if (isBackRespOver) {
                        viewModel.callGetBlogsFromServerApi(currentPage, this@BlogDashboardFragment)
                    }
                } else if (categoryId == 0) {
                    binding.layoutSearchBlog.visibility = View.GONE
                    Utilities.printLogError("Tab=> " + tb.text)
                    if (isBackRespOver) {
                        viewModel.callBlogsListSuggestion(page, this@BlogDashboardFragment)
                    }
                } else {
                    binding.layoutSearchBlog.visibility = View.GONE
                    Utilities.printLogError("Tab=> " + tb.text)
                    if (isBackRespOver) {
                        viewModel.callBlogsListByCategory(
                            categoryId,
                            page,
                            this@BlogDashboardFragment
                        )
//                    viewModel.callBlogsListSuggestion(categoryId,page,this@BlogDashboardFragment)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        blogAdapter!!.setOnBottomReachedListener(object : OnBottomReachedListener {
            override fun onBottomReached(position: Int) {
                Utilities.printLogError("categoryId--->$categoryId")
                currentPage += 10
                page += 1

                if (categoryId == 1) {
                    if (Utilities.isNullOrEmpty(binding.edtSearchBlog.text.toString())) {
                        viewModel.callGetBlogsFromServerApi(currentPage, this@BlogDashboardFragment)
                    } else {
                        searchPage += 1
                        viewModel.callSearchHealthBlogApi(
                            binding.edtSearchBlog.text.toString(),
                            searchPage,
                            this@BlogDashboardFragment
                        )
                    }
                } else if (categoryId == 0) {
                    viewModel.callBlogsListSuggestion(page, this@BlogDashboardFragment)
                } else {
                    viewModel.callBlogsListByCategory(categoryId, page, this@BlogDashboardFragment)
                    /*                    if (Utilities.isNullOrEmpty(binding.edtSearchBlog.text.toString())) {
                                            viewModel.callBlogsListByCategory(categoryId, page, this@BlogDashboardFragment)
                                        }*/
                }
            }
        })

        binding.edtSearchBlog.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                try {
                    if (!binding.edtSearchBlog.isPerformingCompletion) {
                        if (editable.toString().length >= 3) {
                            searchPage = 1
                            blogAdapter!!.clearAdapterList()
                            viewModel.callSearchHealthBlogApi(
                                editable.toString(),
                                searchPage,
                                this@BlogDashboardFragment
                            )
                            /*                            Handler(Looper.getMainLooper()).postDelayed({
                                                            viewModel.callSearchHealthBlogApi(editable.toString(),this@BlogDashboardFragment)
                                                        }, 1000)*/
                        } else {
                            viewModel.callGetBlogsFromServerApi(0, this@BlogDashboardFragment)
                        }
                    }
                    if (editable.toString() == "") {
                        Utilities.printLogError("categoryId--->$categoryId")
                        blogAdapter!!.clearAdapterList()
                        if (categoryId == 0) {
                            viewModel.callGetBlogsFromServerApi(0, this@BlogDashboardFragment)
                        } else {
                            //viewModel.callBlogsListByCategory(categoryId,page,this@BlogDashboardFragment)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        /*        binding.rvBlogs.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        if (dy > 0) { //check for scroll down
                            visibleItemCount = mLayoutManager!!.childCount;
                            totalItemCount = mLayoutManager!!.itemCount;
                            pastVisibleItems = mLayoutManager!!.findFirstVisibleItemPosition()

                            if (loading) {
                                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                    loading = false;
                                    //Log.v("...", "Last Item Wow !");
                                    // Do pagination.. i.e. fetch new data
                                    currentPage += 10
                                    page += 1

                                    if ( categoryId == 0 ) {
                                        if (Utilities.isNullOrEmpty(binding.edtSearchBlog.text.toString())) {
                                            viewModel.callGetBlogsFromServerApi(currentPage, this@BlogDashboardFragment)
                                        }
                                    } else {
                                        viewModel.callBlogsListByCategory(categoryId, page, this@BlogDashboardFragment)
                                        if (Utilities.isNullOrEmpty(binding.edtSearchBlog.text.toString())) {
                                            viewModel.callBlogsListByCategory(categoryId, page, this@BlogDashboardFragment)
                                        }
                                    }
                                    loading = true;
                                }
                            }
                        }

                    }

                })*/

    }

    fun updateBlogsList(blogList: ArrayList<BlogItem>, from: String) {
        Utilities.printLogError("from--->$from")
        Utilities.printLogError("Blogs($from)--->${blogList.size}")
        if (blogList.isNotEmpty()) {
            when (from) {
                Constants.SEARCH -> {
                    //blogAdapter!!.clearAdapterList()
                    binding.rvBlogs.layoutAnimation = animation
                    //binding.rvBlogs.scheduleLayoutAnimation()
                }
            }

            blogAdapter!!.updateData(blogList)
            binding.rvBlogs.visibility = View.VISIBLE
            binding.layoutNoHealthBlogs.visibility = View.GONE
            binding.noBlogsSuggestion.visibility = View.GONE
        } else {
            binding.rvBlogs.visibility = View.GONE
            binding.layoutNoHealthBlogs.visibility = View.GONE
            binding.noBlogsSuggestion.visibility = View.VISIBLE
        }
    }

    fun viewBlog(view: View, blog: BlogItem) {
        val data = HashMap<String, Any>()
        data[CleverTapConstants.FROM] = CleverTapConstants.BLOGS
        data[CleverTapConstants.CATEGORY_ID] = blog.categoryId!!
        data[CleverTapConstants.BLOG_ID] = blog.id!!
        CleverTapHelper.pushEventWithProperties(
            requireContext(),
            CleverTapConstants.BLOG_DETAILS_SCREEN,
            data
        )
        val bundle = Bundle()
        bundle.putString(Constants.TITLE, blog.title)
        bundle.putString(Constants.DESCRIPTION, blog.description)
        bundle.putString(Constants.BODY, blog.body)
        bundle.putString(Constants.LINK, blog.link)
        bundle.putInt(Constants.CATEGORY_ID, blog.categoryId!!)
        bundle.putInt(Constants.TAB, tab)
        view.findNavController()
            .navigate(R.id.action_blogsDashboardFragment_to_blogDetailFragment, bundle)
    }

    private fun startShimmer() {
        binding.layoutShimmer.startShimmer()
        binding.layoutShimmer.visibility = View.VISIBLE
    }

    fun stopShimmer() {
        binding.layoutShimmer.stopShimmer()
        binding.layoutShimmer.visibility = View.GONE
    }

    fun getEventByBlogCategory(categoryId: Int): String {
        var event = ""
        when (categoryId) {
            0 -> event = CleverTapConstants.BLOG_FOR_YOU_SCREEN
            1 -> event = CleverTapConstants.BLOG_ALL_BLOGS_SCREEN
            495 -> event = CleverTapConstants.BLOG_CANCER_CARE_SCREEN
            496 -> event = CleverTapConstants.BLOG_COVID_19_SCREEN
            497 -> event = CleverTapConstants.BLOG_DIABETES_SCREEN
            498 -> event = CleverTapConstants.BLOG_ELDER_CARE_SCREEN
            499 -> event = CleverTapConstants.BLOG_EMOTIONAL_HEALTH_SCREEN
            500 -> event = CleverTapConstants.BLOG_FITNESS_SCREEN
            501 -> event = CleverTapConstants.BLOG_FOOD_AND_HEALTH_SCREEN
            508 -> event = CleverTapConstants.BLOG_GENERAL_HEALTH_SCREEN
            503 -> event = CleverTapConstants.BLOG_HAIR_AND_SKIN_SCREEN
            504 -> event = CleverTapConstants.BLOG_HEART_CARE_SCREEN
            505 -> event = CleverTapConstants.BLOG_LIFESTYLE_SCREEN
            506 -> event = CleverTapConstants.BLOG_OCCUPATIONAL_HEALTH_SCREEN
            507 -> event = CleverTapConstants.BLOG_PARENTING_SCREEN
            515 -> event = CleverTapConstants.BLOG_PREGNANCY_CARE_SCREEN
            494 -> event = CleverTapConstants.BLOG_WOMEN_HEALTH_SCREEN
        }
        return event
    }

}
