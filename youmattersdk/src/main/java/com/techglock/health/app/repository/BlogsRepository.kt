package com.techglock.health.app.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.techglock.health.app.model.blogs.BlogRecommendationListModel
import com.techglock.health.app.model.blogs.BlogsCategoryModel
import com.techglock.health.app.model.blogs.BlogsListAllModel
import com.techglock.health.app.model.blogs.BlogsListByCategoryModel
import com.techglock.health.app.model.blogs.BlogsListBySearchModel
import com.techglock.health.app.remote.BlogsDatasource
import com.techglock.health.app.repository.utils.NetworkDataBoundResource
import com.techglock.health.app.repository.utils.Resource
import javax.inject.Inject

interface BlogsRepository {

    //suspend fun downloadBlogs(forceRefresh: Boolean = false, data: String): LiveData<Resource<List<BlogModel.Blog>>>
    suspend fun downloadBlogs(
        forceRefresh: Boolean = false,
        data: BlogsListAllModel
    ): LiveData<Resource<BlogsListAllModel.BlogsListAllResponse>>

    suspend fun searchBlogs(
        forceRefresh: Boolean = false,
        data: BlogsListBySearchModel
    ): LiveData<Resource<BlogsListBySearchModel.BlogsListBySearchResponse>>

    suspend fun blogsCategory(
        forceRefresh: Boolean = false,
        data: BlogsCategoryModel
    ): LiveData<Resource<BlogsCategoryModel.BlogsCategoryResponse>>

    //suspend fun blogsListByCategory(forceRefresh: Boolean = false, data: BlogsListByCategoryModel): LiveData<Resource<BlogsListByCategoryModel.BlogsCategoryResponse>>
    suspend fun blogsListByCategory(
        forceRefresh: Boolean = false,
        data: BlogsListByCategoryModel
    ): LiveData<Resource<BlogsListByCategoryModel.BlogsCategoryResponse>>

    suspend fun blogsListSuggestion(
        forceRefresh: Boolean = false,
        data: BlogRecommendationListModel
    ): LiveData<Resource<BlogRecommendationListModel.BlogsResponse>>

    suspend fun blogsRelatedTo(
        forceRefresh: Boolean = false,
        data: BlogRecommendationListModel
    ): LiveData<Resource<BlogRecommendationListModel.BlogsResponse>>
}


class BlogsRepositoryImpl @Inject constructor(
    private val datasource: BlogsDatasource,
    val context: Context
) :
    BlogsRepository {

    override suspend fun downloadBlogs(
        forceRefresh: Boolean,
        data: BlogsListAllModel
    ): LiveData<Resource<BlogsListAllModel.BlogsListAllResponse>> {

        return object :
            NetworkDataBoundResource<BlogsListAllModel.BlogsListAllResponse, BlogsListAllModel.BlogsListAllResponse>(
                context
            ) {

            override fun processResponse(response: BlogsListAllModel.BlogsListAllResponse): BlogsListAllModel.BlogsListAllResponse {
                return response
            }

            override suspend fun createCallAsync(): BlogsListAllModel.BlogsListAllResponse {
                return datasource.getBlogsResponse(data)
            }

        }.build().asLiveData()

    }

    override suspend fun searchBlogs(
        forceRefresh: Boolean,
        data: BlogsListBySearchModel
    ): LiveData<Resource<BlogsListBySearchModel.BlogsListBySearchResponse>> {

        return object :
            NetworkDataBoundResource<BlogsListBySearchModel.BlogsListBySearchResponse, BlogsListBySearchModel.BlogsListBySearchResponse>(
                context
            ) {

            override fun processResponse(response: BlogsListBySearchModel.BlogsListBySearchResponse): BlogsListBySearchModel.BlogsListBySearchResponse {
                return response
            }

            override suspend fun createCallAsync(): BlogsListBySearchModel.BlogsListBySearchResponse {
                return datasource.getSearchBlogsResponse(data)
            }

        }.build().asLiveData()

    }

    override suspend fun blogsListByCategory(
        forceRefresh: Boolean,
        data: BlogsListByCategoryModel
    ): LiveData<Resource<BlogsListByCategoryModel.BlogsCategoryResponse>> {

        return object :
            NetworkDataBoundResource<BlogsListByCategoryModel.BlogsCategoryResponse, BlogsListByCategoryModel.BlogsCategoryResponse>(
                context
            ) {
            override fun processResponse(response: BlogsListByCategoryModel.BlogsCategoryResponse): BlogsListByCategoryModel.BlogsCategoryResponse {
                return response
            }

            override suspend fun createCallAsync(): BlogsListByCategoryModel.BlogsCategoryResponse {
                return datasource.getBlogsListByCategory(data)
            }

        }.build().asLiveData()

    }

    override suspend fun blogsListSuggestion(
        forceRefresh: Boolean,
        data: BlogRecommendationListModel
    ): LiveData<Resource<BlogRecommendationListModel.BlogsResponse>> {

        return object :
            NetworkDataBoundResource<BlogRecommendationListModel.BlogsResponse, BlogRecommendationListModel.BlogsResponse>(
                context
            ) {
            override fun processResponse(response: BlogRecommendationListModel.BlogsResponse): BlogRecommendationListModel.BlogsResponse {
                return response
            }

            override suspend fun createCallAsync(): BlogRecommendationListModel.BlogsResponse {
                return datasource.getBlogsSuggestionResponse(data)
            }

        }.build().asLiveData()

    }

    override suspend fun blogsCategory(
        forceRefresh: Boolean,
        data: BlogsCategoryModel
    ): LiveData<Resource<BlogsCategoryModel.BlogsCategoryResponse>> {

        return object :
            NetworkDataBoundResource<BlogsCategoryModel.BlogsCategoryResponse, BlogsCategoryModel.BlogsCategoryResponse>(
                context
            ) {

            override fun processResponse(response: BlogsCategoryModel.BlogsCategoryResponse): BlogsCategoryModel.BlogsCategoryResponse {
                return response
            }

            override suspend fun createCallAsync(): BlogsCategoryModel.BlogsCategoryResponse {
                return datasource.getBlogsCategoryResponse(data)
            }

        }.build().asLiveData()

    }

    /*    override suspend fun blogsCategory(forceRefresh: Boolean, data: BlogsCategoryModel): LiveData<Resource<BlogsCategoryModel.BlogsCategoryResponse>> {

            return object : NetworkDataBoundResource<BlogsCategoryModel.BlogsCategoryResponse, BlogsCategoryModel.BlogsCategoryResponse>(context) {
                override fun processResponse(response: BlogsCategoryModel.BlogsCategoryResponse): BlogsCategoryModel.BlogsCategoryResponse {
                    return response
                }

                override suspend fun createCallAsync(): BlogsCategoryModel.BlogsCategoryResponse {
                    return datasource.getBlogsCategoryResponse(data)
                }

            }.build().asLiveData()

        }*/

    /*    override suspend fun blogsCategory(forceRefresh: Boolean, data: BlogsCategoryModel): LiveData<Resource<BlogsCategoryModel.BlogsCategoryResponse>> {

        return object : NetworkBoundResource<BlogsCategoryModel.BlogsCategoryResponse, BaseResponse<BlogsCategoryModel.BlogsCategoryResponse>>(context) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): BlogsCategoryModel.BlogsCategoryResponse {
                return BlogsCategoryModel.BlogsCategoryResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<BlogsCategoryModel.BlogsCategoryResponse> {
                return datasource.getBlogsCategoryResponse(data)
            }

            override fun processResponse(response: BaseResponse<BlogsCategoryModel.BlogsCategoryResponse>): BlogsCategoryModel.BlogsCategoryResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: BlogsCategoryModel.BlogsCategoryResponse) {
            }

            override fun shouldFetch(data: BlogsCategoryModel.BlogsCategoryResponse?): Boolean {
                return true
            }

        }.build().asLiveData()

    }*/

    override suspend fun blogsRelatedTo(
        forceRefresh: Boolean,
        data: BlogRecommendationListModel
    ): LiveData<Resource<BlogRecommendationListModel.BlogsResponse>> {

        return object :
            NetworkDataBoundResource<BlogRecommendationListModel.BlogsResponse, BlogRecommendationListModel.BlogsResponse>(
                context
            ) {
            override fun processResponse(response: BlogRecommendationListModel.BlogsResponse): BlogRecommendationListModel.BlogsResponse {
                return response
            }

            override suspend fun createCallAsync(): BlogRecommendationListModel.BlogsResponse {
                return datasource.getBlogsRelatedTo(data)
            }

        }.build().asLiveData()

    }

    /*    override suspend fun blogsListByCategory(forceRefresh: Boolean, data: BlogsListByCategoryModel): LiveData<Resource<BlogsListByCategoryModel.BlogsCategoryResponse>> {
            return object : NetworkBoundResource<BlogsListByCategoryModel.BlogsCategoryResponse, BaseResponse<BlogsListByCategoryModel.BlogsCategoryResponse>>(context) {

                override fun shouldStoreInDb(): Boolean = false

                override suspend fun loadFromDb(): BlogsListByCategoryModel.BlogsCategoryResponse {
                    return BlogsListByCategoryModel.BlogsCategoryResponse()
                }

                override suspend fun createCallAsync(): BaseResponse<BlogsListByCategoryModel.BlogsCategoryResponse> {
                    return datasource.getBlogsListByCategory(data)
                }

                override fun processResponse(response: BaseResponse<BlogsListByCategoryModel.BlogsCategoryResponse>): BlogsListByCategoryModel.BlogsCategoryResponse {
                    return response.jSONData
                }

                override suspend fun saveCallResults(items: BlogsListByCategoryModel.BlogsCategoryResponse) {
                }

                override fun shouldFetch(data: BlogsListByCategoryModel.BlogsCategoryResponse?): Boolean {
                    return true
                }

            }.build().asLiveData()
        }*/

}