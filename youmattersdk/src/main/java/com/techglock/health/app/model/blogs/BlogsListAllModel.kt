package com.techglock.health.app.model.blogs

import com.techglock.health.app.model.BaseRequest
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BlogsListAllModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: JsonObject, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("per_page")
        val perPage: Int = 10,
        @SerializedName("offset")
        val offset: Int = 0
    )

    data class BlogsListAllResponse(
        @SerializedName("ListAll")
        val listAll: List<BlogModel.Blog> = listOf()
    )

}