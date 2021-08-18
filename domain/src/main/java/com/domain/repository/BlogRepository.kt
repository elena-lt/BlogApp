package com.domain.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import com.domain.models.BlogPostDomain
import com.domain.utils.DataState
import com.domain.viewState.BlogViewState

interface BlogRepository {

    fun searchBlogPosts(query: String, filterAndOrder: String, page: Int): LiveData<DataState<BlogViewState>>

    fun checkAuthorOfBlogPost(slug: String):LiveData<DataState<BlogViewState>>

    fun updateBlogPost(slug: String, blogTitle: String,  blogBody: String, imageUri: Uri): LiveData<DataState<BlogViewState>>

    fun deleteBlog(blogPost: BlogPostDomain): LiveData<DataState<BlogViewState>>
}