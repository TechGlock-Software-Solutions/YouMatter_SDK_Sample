package com.techglock.health.app.remote

import com.techglock.health.app.di.DIModule
import com.techglock.health.app.model.blogs.BlogRecommendationListModel
import com.techglock.health.app.model.blogs.BlogsCategoryModel
import com.techglock.health.app.model.blogs.BlogsListAllModel
import com.techglock.health.app.model.blogs.BlogsListByCategoryModel
import com.techglock.health.app.model.blogs.BlogsListBySearchModel
import javax.inject.Inject
import javax.inject.Named

class BlogsDatasource @Inject constructor(@Named(DIModule.DEFAULT_NEW) private val defaultService: ApiService) {

    //suspend fun getBlogsResponse(data: String) = blogsService.downloadBlogs(data)

    suspend fun getBlogsResponse(data: BlogsListAllModel) = defaultService.downloadBlogs(data)

    suspend fun getSearchBlogsResponse(data: BlogsListBySearchModel) =
        defaultService.searchBlogs(data)

    suspend fun getBlogsCategoryResponse(data: BlogsCategoryModel) =
        defaultService.blogsCategoryList(data)

    suspend fun getBlogsSuggestionResponse(data: BlogRecommendationListModel) =
        defaultService.blogsListSuggestion(data)

    suspend fun getBlogsRelatedTo(data: BlogRecommendationListModel) =
        defaultService.blogsRelatedToList(data)

    suspend fun getBlogsListByCategory(data: BlogsListByCategoryModel) =
        defaultService.blogsListByCategory(data)

}