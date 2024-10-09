package com.techglock.health.app.model.blogs

data class BlogItem(
    var id: String? = "",
    var title: String? = "",
    var description: String? = "",
    var date: String? = "",
    var image: String? = "",
    var link: String? = "",
    var body: String? = "",
    var categoryId: Int? = 508
)
