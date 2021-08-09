package com.domain.models

data class BlogPostDomain(

    var primaryKey: Int,
    var title: String,
    var slug: String,
    var body: String,
    var image: String,
    var date_updated: Long,
    var username: String
)
