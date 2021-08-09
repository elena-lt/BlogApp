package com.domain.viewState

import com.domain.models.BlogPostDomain

data class BlogViewState(
    //blogFragment vars
    val blogFields: BlogFields = BlogFields()

    //viewBlog fragment

    //updateBlogFragment vars
) {
    data class BlogFields(
        var blogList: List<BlogPostDomain> = ArrayList<BlogPostDomain>(),
        var searchQuery: String = ""
    )
}
