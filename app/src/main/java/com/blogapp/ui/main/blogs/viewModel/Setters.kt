package com.blogapp.ui.main.blogs.viewModel

import android.util.Log
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

fun BlogViewModel.setFilter(filter: String?) {
    filter?.let {
        val update = getCurrentViewStateOrNew()
        update.blogFields.filter = filter
        setViewState(update)
    }
}

fun BlogViewModel.setOrder(order: String?) {
    order?.let {
        val update = getCurrentViewStateOrNew()
        update.blogFields.order = order
        setViewState(update)
    }
}

fun BlogViewModel.setIsAuthorOfBlogPost(isAuthor: Boolean) {
    val update = getCurrentViewStateOrNew()
    update.viewBlogFields.isAuthorOfBlogPost = isAuthor
    setViewState(update)
}

fun BlogViewModel.removeDeletedBlogPost() {
    val update = getCurrentViewStateOrNew()
    val list = update.blogFields.blogList.toMutableList()
    for (i in 0 until list.size) {
        if (list[i] == getBlogPost()) {
            list.remove(getBlogPost())
            break
        }
    }
    setBlogList(list.map {
        BlogPostMapper.toBlogPost(it)
    })
}