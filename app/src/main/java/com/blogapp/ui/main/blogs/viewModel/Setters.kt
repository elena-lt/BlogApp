package com.blogapp.ui.main.blogs.viewModel

import android.net.Uri
import android.util.Log
import com.blogapp.models.BlogPost
import com.blogapp.models.mappers.BlogPostMapper
import com.domain.models.BlogPostDomain
import com.domain.viewState.BlogViewState

fun BlogViewModel.setQuery(query: String) {
    val update = currentState
    if (query == update.blogFields.searchQuery) return
    update.blogFields.searchQuery = query
    setViewState(update)
}

fun BlogViewModel.setBlogList(blogList: List<BlogPost>) {
    val update = currentState
    update.blogFields.blogList = blogList.map {
        BlogPostMapper.toBlogPostDomain(it)
    }
    setViewState(update)
}

fun BlogViewModel.setBlogPost(blogPost: BlogPostDomain) {
    val update = currentState
    update.viewBlogFields.blogPost = blogPost
    setViewState(update)
}

fun BlogViewModel.setAuthorOfBlogPost(isAuthorOfBlogPost: Boolean) {
    val update = currentState
    update.viewBlogFields.isAuthorOfBlogPost = isAuthorOfBlogPost
    setViewState(update)
}

fun BlogViewModel.setQueryExhausted(isQueryExhausted: Boolean) {
    val update = currentState
    update.blogFields.isQueryExhausted = isQueryExhausted
    setViewState(update)
}

fun BlogViewModel.setQueryInProgress(isQueryInProgress: Boolean) {
    val update = currentState
    update.blogFields.isQueryInProgress = isQueryInProgress
    setViewState(update)
}

fun BlogViewModel.setFilter(filter: String?) {
    filter?.let {
        val update = currentState
        update.blogFields.filter = filter
        setViewState(update)
    }
}

fun BlogViewModel.setOrder(order: String?) {
    order?.let {
        val update = currentState
        update.blogFields.order = order
        setViewState(update)
    }
}

fun BlogViewModel.setIsAuthorOfBlogPost(isAuthor: Boolean) {
    val update = currentState.copy(
        viewBlogFields = BlogViewState.ViewBlogFields(
            getBlogPost(),
            isAuthorOfBlogPost = isAuthor
        )
    )
    setViewState(update)
}

fun BlogViewModel.removeDeletedBlogPost() {
    val update = currentState
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

fun BlogViewModel.setUpdatedBlogFields(
    title: String?,
    body: String?,
    imageUri: Uri?
) {
    val update = currentState
    val updatedBlogFields = update.updateBlogFields
    title?.let { updatedBlogFields.blogTitle = it }
    body?.let { updatedBlogFields.blogBody = it }
    imageUri?.let { updatedBlogFields.imageUri = it }
    update.updateBlogFields = updatedBlogFields
    setViewState(update)
}

fun BlogViewModel.updateListItem(newBlogPost: BlogPostDomain) {
    val update = currentState
    val list = update.blogFields.blogList.toMutableList()
    for (i in 0 until list.size) {
        if (list[i].primaryKey == getBlogPost().primaryKey) {
            list[i] = newBlogPost
            break
        }
    }
    update.blogFields.blogList = list
    setViewState(update)
}

fun BlogViewModel.onBlogPostUpdateSuccess(blogPost: BlogPostDomain) {
    setUpdatedBlogFields(
        blogPost.title,
        blogPost.body,
        null
    )
    setBlogPost(blogPost) //update ViewBlogFragment
    updateListItem(blogPost) //update list in BlogFragment
}