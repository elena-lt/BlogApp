package com.blogapp.models

data class BlogPost(
    var primaryKey: Int,
    var title: String,
    var slug: String,
    var body: String,
    var image: String,
    var date_updated: String,
    var username: String
)
