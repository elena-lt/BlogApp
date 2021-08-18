package com.domain.viewState

import android.net.Uri
import com.domain.models.BlogPostDomain
import com.domain.utils.Const.BLOG_ORDER_ASC
import com.domain.utils.Const.BLOG_ORDER_DESC
import com.domain.utils.Const.ORDER_BY_ASC_DATE_UPDATED

data class BlogViewState(
    //blogFragment vars
    val blogFields: BlogFields = BlogFields(),

    //viewBlog fragment
    val viewBlogFields: ViewBlogFields = ViewBlogFields(),

        //updateBlogFragment vars
    var updateBlogFields: UpdateBlogFields = UpdateBlogFields()
) {
    data class BlogFields(
        var blogList: List<BlogPostDomain> = ArrayList<BlogPostDomain>(),
        var searchQuery: String = "",
        var page: Int = 1,
        var isQueryInProgress: Boolean = false,
        var isQueryExhausted: Boolean = false,
        var filter: String = ORDER_BY_ASC_DATE_UPDATED,
        var order: String = BLOG_ORDER_DESC
    )

    data class ViewBlogFields(
        var blogPost: BlogPostDomain? = null,
        var isAuthorOfBlogPost: Boolean = false,
    )

    data class UpdateBlogFields(
        var blogTitle: String? = null,
        var blogBody: String? = null,
        var imageUri: Uri? = null
    )
}
