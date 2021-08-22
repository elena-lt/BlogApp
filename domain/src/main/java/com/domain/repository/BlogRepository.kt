package com.domain.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import com.domain.models.BlogPostDomain
import com.domain.dataState.DataState
import com.domain.viewState.BlogViewState
import kotlinx.coroutines.flow.Flow

interface BlogRepository {

    fun searchBlogPosts(query: String, filterAndOrder: String, page: Int): Flow<DataState<BlogViewState>>

    fun checkAuthorOfBlogPost(slug: String):Flow<DataState<BlogViewState>>

    fun updateBlogPost(slug: String, blogTitle: String,  blogBody: String, imageUri: Uri): Flow<DataState<BlogViewState>>

    fun deleteBlog(blogPost: BlogPostDomain): Flow<DataState<BlogViewState>>
}