package com.techglock.health.app.model.blogs

import com.techglock.health.app.model.BaseRequest
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BlogsCategoryModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: JsonObject, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("page")
        val page: Int = 0,
        @SerializedName("per_page")
        val perPage: Int = 10
    )

    data class BlogsCategoryResponse(
        @SerializedName("CategoryList")
        val categoryList: List<Category> = listOf()
    )

    data class Category(
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("name")
        var name: String = ""
    )

}

/*
class BlogsCategoryModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("RequestType")
        val requestType: String = "POST"
    )

    data class BlogsCategoryResponse(
        @SerializedName("Result")
        val result: Result = Result()
    )

    data class Result(
        @SerializedName("CategoryList")
        val categoryList: List<Category> = listOf()
    )

    data class Category(
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("name")
        var name: String = ""
    )

}*/
