package com.techglock.health.app.blogs.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.techglock.health.app.R
import com.techglock.health.app.blogs.domain.BlogsManagementUseCase
import com.techglock.health.app.blogs.ui.BlogDashboardFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Event
import com.techglock.health.app.common.utils.PreferenceUtils
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.model.blogs.*
import com.techglock.health.app.repository.utils.Resource
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.*
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class BlogViewModel @Inject constructor(
    application: Application,
    private val blogManagementUseCase: BlogsManagementUseCase,
    private val preferenceUtils: PreferenceUtils,
    private val context: Context?
) : BaseViewModel(application) {

    val authToken = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")

    private val visibleThreshold = 10
    var tabNumber = 0

    //For blogs carousal
    var healthBlogSuggestionList = MutableLiveData<List<BlogItem>>()

    private var listBlogsCategorySource: LiveData<Resource<BlogsCategoryModel.BlogsCategoryResponse>> =
        MutableLiveData()
    private val _listBlogsCategory = MediatorLiveData<BlogsCategoryModel.BlogsCategoryResponse?>()
    val listBlogsCategory: LiveData<BlogsCategoryModel.BlogsCategoryResponse?> get() = _listBlogsCategory

    private var blogsSuggestionListSource: LiveData<Resource<BlogRecommendationListModel.BlogsResponse>> =
        MutableLiveData()
    private val _blogsSuggestionList =
        MediatorLiveData<BlogRecommendationListModel.BlogsResponse?>()
    val blogsSuggestionList: LiveData<BlogRecommendationListModel.BlogsResponse?> get() = _blogsSuggestionList

    private var blogsListByCategorySource: LiveData<Resource<BlogsListByCategoryModel.BlogsCategoryResponse>> =
        MutableLiveData()
    private val _blogsListByCategory =
        MediatorLiveData<BlogsListByCategoryModel.BlogsCategoryResponse?>()
    val blogsListByCategory: LiveData<BlogsListByCategoryModel.BlogsCategoryResponse?> get() = _blogsListByCategory

    private var blogsRelatedSource: LiveData<Resource<BlogRecommendationListModel.BlogsResponse>> =
        MutableLiveData()
    private val _blogsRelated = MediatorLiveData<BlogRecommendationListModel.BlogsResponse?>()
    val blogsRelated: LiveData<BlogRecommendationListModel.BlogsResponse?> get() = _blogsRelated

    private var blogListSource: LiveData<Resource<BlogsListAllModel.BlogsListAllResponse>> =
        MutableLiveData()
    private val _blogList = MediatorLiveData<BlogsListAllModel.BlogsListAllResponse?>()
    val blogList: LiveData<BlogsListAllModel.BlogsListAllResponse?> get() = _blogList

    private var searchblogsSource: LiveData<Resource<BlogsListBySearchModel.BlogsListBySearchResponse>> =
        MutableLiveData()
    private val _searchBlogs = MediatorLiveData<BlogsListBySearchModel.BlogsListBySearchResponse?>()
    val searchBlogs: LiveData<BlogsListBySearchModel.BlogsListBySearchResponse?> get() = _searchBlogs

    /*    private var blogListSource: LiveData<Resource<List<BlogModel.Blog>>> = MutableLiveData()
        private val _blogList = MediatorLiveData<List<BlogModel.Blog>>()
        val blogList: LiveData<List<BlogModel.Blog>> get() = _blogList

        private var searchblogsSource: LiveData<Resource<List<BlogModel.Blog>>> = MutableLiveData()
        private val _searchBlogs = MediatorLiveData<List<BlogModel.Blog>>()
        val searchBlogs: LiveData<List<BlogModel.Blog>> get() = _searchBlogs*/

    /*    fun callListBlogsCategory() = viewModelScope.launch(Dispatchers.Main) {

                val requestData = BlogsCategoryModel(
                    Gson().toJson(BlogsCategoryModel.JSONDataRequest(
                        requestType = ""), BlogsCategoryModel.JSONDataRequest::class.java), authToken)

                //_progressBar.value = Event("")
            _listBlogsCategory.removeSource(listBlogsCategorySource)
                withContext(Dispatchers.IO) {
                    listBlogsCategorySource = blogManagementUseCase.invokeBlogsCategory(true, requestData )
                }
            _listBlogsCategory.addSource(listBlogsCategorySource) {
                _listBlogsCategory.value = it.data

                    if (it.status == Resource.Status.SUCCESS) {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.data != null) {
                            Utilities.printLogError("CategoryList--->${it.data!!.result.categoryList.size}")
                        }
                    }
                    if (it.status == Resource.Status.ERROR) {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.errorNumber.equals("1100014", true)) {
                            _sessionError.value = Event(true)
                        } else {
                            toastMessage(it.errorMessage)
                        }
                    }
                }
            }*/

    /*    val obj = JsonObject()
        obj.addProperty("page",1)
        obj.addProperty("per_page","100")
        val requestData = BlogsCategoryModel(obj,authToken)*/


    fun callListBlogsCategory() = viewModelScope.launch(Dispatchers.Main) {

        val obj = JsonObject()
        obj.addProperty("page", 1)
        obj.addProperty("per_page", 100)
        val requestData = BlogsCategoryModel(obj, authToken)

        //_progressBar.value = Event("")
        _listBlogsCategory.removeSource(listBlogsCategorySource)
        withContext(Dispatchers.IO) {
            listBlogsCategorySource = blogManagementUseCase.invokeBlogsCategory(true, requestData)
        }
        _listBlogsCategory.addSource(listBlogsCategorySource) {
            try {
                _listBlogsCategory.value = it.data
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.data != null) {
                            Utilities.printLogError("CategoryList--->${it.data.categoryList.size}")
                        }
                    }

                    Resource.Status.ERROR -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.errorNumber.equals("1100014", true)) {
                            _sessionError.value = Event(true)
                        } else {
                            toastMessage(it.errorMessage)
                        }
                    }

                    else -> {}
                }
            } catch (e: Exception) {
                Utilities.printException(e)
            }

        }

    }

    fun callBlogsListSuggestion(page: Int, fragment: BlogDashboardFragment) =
        viewModelScope.launch(Dispatchers.Main) {

            val obj = JsonObject()
            obj.addProperty(
                "personid", preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
            )
            obj.addProperty("page", page)
            obj.addProperty("per_page", visibleThreshold)
            obj.addProperty("sorting", "jumbled")
            val requestData = BlogRecommendationListModel(obj, authToken)

            //_progressBar.value = Event("")
            _blogsSuggestionList.removeSource(blogsSuggestionListSource)
            withContext(Dispatchers.IO) {
                blogsSuggestionListSource =
                    blogManagementUseCase.invokeBlogsListSuggestion(true, requestData)
            }
            _blogsSuggestionList.addSource(blogsSuggestionListSource) {
                try {
                    _blogsSuggestionList.value = it.data
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.data != null) {
                                //Utilities.printData("BlogsByCategoryResp",it.data!!,true)
                                extractBlogContent(it.data.blogs, fragment, Constants.CATEGORY)
                                //extractBlogContent(it.data!!.result.blogs,fragment,Constants.CATEGORY)
                            }
                        }

                        Resource.Status.ERROR -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            fragment.stopShimmer()
                            if (it.errorNumber.equals("1100014", true)) {
                                _sessionError.value = Event(true)
                            } else {
                                toastMessage(it.errorMessage)
                            }
                        }

                        else -> {}
                    }
                } catch (e: Exception) {
                    Utilities.printException(e)
                }
            }

        }

    fun callBlogsListByCategory(categoryId: Int, page: Int, fragment: BlogDashboardFragment) =
        viewModelScope.launch(Dispatchers.Main) {

            val obj = JsonObject()
            obj.addProperty("categories", categoryId)
            obj.addProperty("page", page)
            obj.addProperty("per_page", visibleThreshold)
            val requestData = BlogsListByCategoryModel(obj, authToken)

            //_progressBar.value = Event("")
            _blogsListByCategory.removeSource(blogsListByCategorySource)
            withContext(Dispatchers.IO) {
                blogsListByCategorySource =
                    blogManagementUseCase.invokeBlogsListByCategory(true, requestData)
            }
            _blogsListByCategory.addSource(blogsListByCategorySource) {

                try {
                    _blogsListByCategory.value = it.data
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.data != null) {
                                //Utilities.printData("BlogsByCategoryResp",it.data!!,true)
                                extractBlogContent(it.data.blogs, fragment, Constants.CATEGORY)
                                //extractBlogContent(it.data!!.result.blogs,fragment,Constants.CATEGORY)
                            }
                        }

                        Resource.Status.ERROR -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            fragment.stopShimmer()
                            if (it.errorNumber.equals("1100014", true)) {
                                _sessionError.value = Event(true)
                            } else {
                                toastMessage(it.errorMessage)
                            }
                        }

                        else -> {}
                    }
                } catch (e: Exception) {
                    Utilities.printException(e)
                }
            }

        }

    fun callGetBlogsFromServerApi(CurrentPage: Int, fragment: BlogDashboardFragment) =
        viewModelScope.launch(Dispatchers.Main) {

            val obj = JsonObject()
            obj.addProperty("per_page", visibleThreshold)
            obj.addProperty("offset", CurrentPage)
            val requestData = BlogsListAllModel(obj, authToken)

            //_progressBar.value = Event("Loading Health Blog...")
            _blogList.removeSource(blogListSource)
            withContext(Dispatchers.IO) {
                blogListSource = blogManagementUseCase.invokeDownloadBlog(
                    isForceRefresh = true, data = requestData
                )
            }
            _blogList.addSource(blogListSource) {
                try {
                    _blogList.value = it.data
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.data != null) {
                                //Utilities.printLog("Blog_Response--->" + it.data!!)
                                extractBlogContent(it.data.listAll, fragment, Constants.ALL)
                            }
                        }

                        Resource.Status.ERROR -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            fragment.stopShimmer()
                            toastMessage(it.errorMessage)
                        }

                        else -> {}
                    }
                } catch (e: Exception) {
                    Utilities.printException(e)
                }
            }

        }

    fun callSearchHealthBlogApi(keyWord: String, page: Int, fragment: BlogDashboardFragment) =
        viewModelScope.launch(Dispatchers.Main) {

            val obj = JsonObject()
            obj.addProperty("search", keyWord)
            obj.addProperty("page", page)
            obj.addProperty("per_page", visibleThreshold)
            val requestData = BlogsListBySearchModel(obj, authToken)

            //_progressBar.value = Event("Loading Health Blog...")
            _searchBlogs.removeSource(searchblogsSource)
            withContext(Dispatchers.IO) {
                searchblogsSource = blogManagementUseCase.invokeSearchBlog(
                    isForceRefresh = true, data = requestData
                )
            }
            _searchBlogs.addSource(searchblogsSource) {
                try {
                    _searchBlogs.value = it.data
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.data != null) {
                                //Utilities.printLog("Blog_Response--->" + it.data!!)
                                extractBlogContent(
                                    it.data.listBySearchTerm, fragment, Constants.SEARCH
                                )
                            }
                        }

                        Resource.Status.ERROR -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            fragment.stopShimmer()
                            //toastMessage(it.errorMessage)
                        }

                        else -> {}
                    }
                } catch (e: Exception) {
                    Utilities.printException(e)
                }
            }

        }

    fun callBlogsListByCategory(blogId: String, categoryId: Int, page: Int) =
        viewModelScope.launch(Dispatchers.Main) {

            val obj = JsonObject()
            obj.addProperty("categories", categoryId)
            obj.addProperty("page", page)
            obj.addProperty("per_page", 6)
            val requestData = BlogsListByCategoryModel(obj, authToken)

            //_progressBar.value = Event("")
            _blogsListByCategory.removeSource(blogsListByCategorySource)
            withContext(Dispatchers.IO) {
                blogsListByCategorySource =
                    blogManagementUseCase.invokeBlogsListByCategory(true, requestData)
            }
            _blogsListByCategory.addSource(blogsListByCategorySource) {
                try {
                    _blogsListByCategory.value = it.data
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.data != null) {
                                //Utilities.printData("BlogsByCategoryResp",it.data!!,true)
                                //                    extractBlogContent(it.data!!.blogs,fragment,Constants.CATEGORY)
                                extractBlogContent(it.data.blogs, blogId)
                                //extractBlogContent(it.data!!.result.blogs,fragment,Constants.CATEGORY)
                            }
                        }

                        Resource.Status.ERROR -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            //                fragment.stopShimmer()
                            if (it.errorNumber.equals("1100014", true)) {
                                _sessionError.value = Event(true)
                            } else {
                                toastMessage(it.errorMessage)
                            }
                        }

                        else -> {}
                    }
                } catch (e: Exception) {
                    Utilities.printException(e)
                }
            }

        }

    /*    fun callGetBlogsFromServerApi( CurrentPage: Int, fragment: BlogDashboardFragment) = viewModelScope.launch(Dispatchers.Main) {

            val blogProxyURL = Constants.strBlogsProxyURL
            val blogURL = "$blogProxyURL$visibleThreshold&offset=$CurrentPage"

            //_progressBar.value = Event("Loading Health Blog...")
            _blogList.removeSource(blogListSource)
            withContext(Dispatchers.IO) {
                blogListSource = blogManagementUseCase.invokeDownloadBlog(isForceRefresh = true, data = blogURL)
            }
            _blogList.addSource(blogListSource) {
                _blogList.value = it.data

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        //Utilities.printLog("Blog_Response--->" + it.data!!)
                        extractBlogContent(it.data!!,fragment,Constants.ALL)
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    fragment.stopShimmer()
                    toastMessage(it.errorMessage)
                }
            }

        }

        fun callSearchHealthBlogApi( keyWord:String, page : Int ,fragment: BlogDashboardFragment) = viewModelScope.launch(Dispatchers.Main) {

            val blogProxyURL = Constants.strSearchBlogsProxyURL
            val blogURL = "$blogProxyURL$keyWord&per_page=$visibleThreshold&page=$page"
            //https://blogs.vivant.me/wp-json/wp/v2/posts?search?search=the&per_page=20&page=1

            //_progressBar.value = Event("Loading Health Blog...")
            _searchBlogs.removeSource(searchblogsSource)
            withContext(Dispatchers.IO) {
                searchblogsSource = blogManagementUseCase.invokeDownloadBlog(isForceRefresh = true, data = blogURL)
            }
            _searchBlogs.addSource(searchblogsSource) {
                _searchBlogs.value = it.data

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        //Utilities.printLog("Blog_Response--->" + it.data!!)
                        extractBlogContent(it.data!!,fragment,Constants.SEARCH)
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    fragment.stopShimmer()
                    //toastMessage(it.errorMessage)
                }
            }

        }*/

    /*
        fun callSearchHealthBlogApi( keyWord:String, fragment: BlogDashboardFragment) = viewModelScope.launch(Dispatchers.Main) {

            val blogProxyURL = Constants.strSearchBlogsProxyURL
            val blogURL = "$blogProxyURL$keyWord"

            //_progressBar.value = Event("Loading Health Blog...")
            _searchBlogs.removeSource(searchblogsSource)
            withContext(Dispatchers.IO) {
                searchblogsSource = blogManagementUseCase.invokeDownloadBlog(isForceRefresh = true, data = blogURL)
            }
            _searchBlogs.addSource(searchblogsSource) {
                _searchBlogs.value = it.data

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        //Utilities.printLog("Blog_Response--->" + it.data!!)
                        extractBlogContent(it.data!!,fragment,Constants.SEARCH)
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    fragment.stopShimmer()
                    toastMessage(it.errorMessage)
                }
            }

        }*/

    private fun extractBlogContent(
        list: List<BlogModel.Blog>, fragment: BlogDashboardFragment, from: String
    ) {
        Utilities.printLogError("Blogs($from)--->${list.size}")
        val blogList: ArrayList<BlogItem> = ArrayList()
        for (blog in list) {
            val link = blog.link
            val id = blog.id.toString()
            val date = DateHelper.getDateTimeAs_ddMMMyyyyNew(blog.date)
            val renderedTitle = blog.title.rendered
            val renderedContent = blog.content.rendered
            val renderedExcerpt = blog.excerpt.rendered
            Utilities.printLogError("Title--->$renderedTitle")
            //Utilities.printLogError("BlogLink--->$link")
            val documentExcerpt: Document = Jsoup.parse(renderedExcerpt)
            val pExcerpt: Elements = documentExcerpt.getElementsByTag("p")
            var strDescription = ""
            for (x: Element in pExcerpt) {
                strDescription += x.text()
            }
            //Utilities.printLogError("Description--->$strDescription")
            val documentContent: Document = Jsoup.parse(renderedContent)
            val pContent: Elements = documentContent.getElementsByTag("p")
            var strBody = ""
            for (x in pContent) {
                strBody += x.text()
            }
            val elementsByClassImage: Elements = documentContent.getElementsByTag("img")
            val strImgSrc = elementsByClassImage.attr("src")
            //Utilities.printLogError("ImgUrl--->$strImgSrc")
            var categoryId = 0
            if (!blog.categories.isNullOrEmpty()) {
                categoryId = blog.categories.get(0)
            }
            val blogItem = BlogItem(
                id = id,
                title = renderedTitle,
                description = strDescription,
                date = date,
                image = strImgSrc,
                link = link,
                body = renderedContent,
                categoryId = categoryId
            )
            if (!blogList.contains(blogItem)) {
                blogList.add(blogItem)
            }
        }
        fragment.updateBlogsList(blogList, from)
        fragment.stopShimmer()
    }

    fun shareBlog(blog: BlogItem) {
        val title = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(blog.title, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            Html.fromHtml(blog.title).toString()
        }
        val desc = blog.description!!.substring(0, 70) + ".....Continue reading at,"
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        sendIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + desc + "\n" + blog.link)/*        sendIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + desc + "\n"
                        + context.resources.getString(R.string.blog_link))*/
        sendIntent.type = "text/plain"
        sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context!!.startActivity(sendIntent)
    }

    /*    fun callListBlogsCategory() = viewModelScope.launch(Dispatchers.Main) {

        val obj = JsonObject()
        //obj.addProperty("page",page)
        //obj.addProperty("per_page",visibleThreshold)
        val requestData = BlogsCategoryModel(obj,authToken)

        //_progressBar.value = Event("")
        _listBlogsCategory.removeSource(listBlogsCategorySource)
        withContext(Dispatchers.IO) {
            listBlogsCategorySource = blogManagementUseCase.invokeBlogsCategory(true, requestData )
        }
        _listBlogsCategory.addSource(listBlogsCategorySource) {
            _listBlogsCategory.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    Utilities.printLogError("CategoryList--->${it.data!!.categoryList.size}")
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }*/

    fun viewBlog(view: View, blog: BlogItem) {
        val bundle = Bundle()
        bundle.putString(Constants.TITLE, blog.title)
        bundle.putString(Constants.DESCRIPTION, blog.description)
        bundle.putString(Constants.BODY, blog.body)
        bundle.putString(Constants.BLOG_ID, blog.id)
        bundle.putString(Constants.LINK, blog.link)
        bundle.putInt(Constants.CATEGORY_ID, blog.categoryId!!)
        bundle.putInt(Constants.TAB, tabNumber)
        view.findNavController()
            .navigate(R.id.action_blogsDetailsFragment_to_blogDetailFragment, bundle)
    }

    fun getRelatedBlogApi(
        page: Int, blogID: String, categroryId: Int
    ) = viewModelScope.launch(Dispatchers.Main) {

        val obj = JsonObject()
        obj.addProperty("categories", categroryId)
        obj.addProperty("page", page)
        obj.addProperty("per_page", 5)
        obj.addProperty("sorting", "JUMBLED")
        val requestData = BlogRecommendationListModel(obj, authToken)

        //_progressBar.value = Event("")
        _blogsRelated.removeSource(blogsRelatedSource)
        withContext(Dispatchers.IO) {
            blogsRelatedSource = blogManagementUseCase.invokeBlogsListRelatedTo(true, requestData)
        }
        _blogsRelated.addSource(blogsRelatedSource) {
            try {
                _blogsRelated.value = it.data
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.data != null) {
                            //Utilities.printData("BlogsByCategoryResp",it.data!!,true)
                            extractBlogContent(it.data.blogs, blogID)
                            //extractBlogContent(it.data!!.result.blogs,fragment,Constants.CATEGORY)
                        }
                    }

                    Resource.Status.ERROR -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.errorNumber.equals("1100014", true)) {
                            _sessionError.value = Event(true)
                        } else {
                            toastMessage(it.errorMessage)
                        }
                    }

                    else -> {}
                }
            } catch (e: Exception) {
                Utilities.printException(e)
            }
        }
    }

    private fun extractBlogContent(list: List<BlogModel.Blog>, blogId: String) {
        val blogList: ArrayList<BlogItem> = ArrayList()
        for (blog in list) {
            val link = blog.link
            val id = blog.id.toString()
            val date = DateHelper.getDateTimeAs_ddMMMyyyyNew(blog.date)
            val renderedTitle = blog.title.rendered
            val renderedContent = blog.content.rendered
            val renderedExcerpt = blog.excerpt.rendered
            Utilities.printLog("Title--->$renderedTitle")
            Utilities.printLog("BlogLink--->$link")
            val documentExcerpt: Document = Jsoup.parse(renderedExcerpt)
            val pExcerpt: Elements = documentExcerpt.getElementsByTag("p")
            var strDescription = ""
            for (x: Element in pExcerpt) {
                strDescription += x.text()
            }
            Utilities.printLog("Description--->$strDescription")
            val documentContent: Document = Jsoup.parse(renderedContent)
            val pContent: Elements = documentContent.getElementsByTag("p")
            var strBody = ""
            for (x in pContent) {
                strBody += x.text()
            }
            val elementsByClassImage: Elements = documentContent.getElementsByTag("img")
            val strImgSrc = elementsByClassImage.attr("src")
            Utilities.printLog("ImgUrl--->$strImgSrc")
            var categoryId = 0
            if (!blog.categories.isNullOrEmpty()) {
                categoryId = blog.categories.get(0)
            }
            val blogItem = BlogItem(
                id = id,
                title = renderedTitle,
                description = strDescription,
                date = date,
                image = strImgSrc,
                link = link,
                body = renderedContent,
                categoryId = categoryId
            )
            if (!blogList.contains(blogItem)) {
                if (blogId.isNullOrEmpty()) {
                    blogList.add(blogItem)
                } else {
                    if (!blogId.equals(id, true)) blogList.add(blogItem)
                }
            }
        }
        healthBlogSuggestionList.postValue(blogList)
    }

}