package com.blogapp.ui.main.blogs.viewModel

import com.blogapp.models.BlogPost
import com.blogapp.models.mappers.BlogPostMapper

fun BlogViewModel.setQuery(query: String) {
    val update = getCurrentViewStateOrNew()
    if (query == update.blogFields.searchQuery) return
    update.blogFields.searchQuery = query
    setViewState(update)
}

fun BlogViewModel.setBlogList(blogList: List<BlogPost>) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.blogList = blogList.map {
        BlogPostMapper.toBlogPostDomain(it)
    }
    setViewState(update)
}

fun BlogViewModel.setBlogPost(blogPost: BlogPost) {
    val update = getCurrentViewStateOrNew()
    update.viewBlogFields.blogPost = BlogPostMapper.toBlogPostDomain(blogPost)
    setViewState(update)
}

fun BlogViewModel.setAuthorOfBlogPost(isAuthorOfBlogPost: Boolean) {
    val update = getCurrentViewStateOrNew()
    update.viewBlogFields.isAuthorOfBlogPost = isAuthorOfBlogPost
    setViewState(update)
}

fun BlogViewModel.setQueryExhausted(isQueryExhausted: Boolean) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.isQueryExhausted = isQueryExhausted
    setViewState(update)
}

fun BlogViewModel.setQueryInProgress(isQueryInProgress: Boolean) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.isQueryInProgress = isQueryInProgress
    setViewState(update)
}