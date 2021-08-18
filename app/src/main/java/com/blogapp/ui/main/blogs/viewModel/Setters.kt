package com.blogapp.ui.main.blogs.viewModel

import android.net.Uri
import com.blogapp.models.BlogPost
import com.blogapp.models.mappers.BlogPostMapper
import com.domain.models.BlogPostDomain

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

fun BlogViewModel.setBlogPost(blogPost: BlogPostDomain) {
    val update = getCurrentViewStateOrNew()
    update.viewBlogFields.blogPost = blogPost
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

fun BlogViewModel.setUpdatedBlogFields(
    title: String?,
    body: String?,
    imageUri: Uri?
) {
    val update = getCurrentViewStateOrNew()
    val updatedBlogFields = update.updateBlogFields
    title?.let { updatedBlogFields.blogTitle = it }
    body?.let { updatedBlogFields.blogBody = it }
    imageUri?.let { updatedBlogFields.imageUri = it }
    update.updateBlogFields = updatedBlogFields
    setViewState(update)
}

fun BlogViewModel.updateListItem(newBlogPost: BlogPostDomain) {
    val update = getCurrentViewStateOrNew()
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