package com.blogapp.ui.main.blogs.state

import androidx.paging.PagingData
import com.domain.models.BlogPostDomain

data class BlogViewState(
    //blogFragment vars
    val blogFields: BlogFields = BlogFields(),

    //viewBlog fragment
    val viewBlogFields: ViewBlogFields = ViewBlogFields()

    //updateBlogFragment vars
) {
    data class BlogFields(
        var blogList: List<BlogPostDomain> = ArrayList<BlogPostDomain>(),
        var blogPosts: PagingData<BlogPostDomain>? = null,
        var searchQuery: String = ""
    )

    data class ViewBlogFields(
        var blogPost: BlogPostDomain? = null,
        var isAuthorOfBlogPost: Boolean = false,
    )
}