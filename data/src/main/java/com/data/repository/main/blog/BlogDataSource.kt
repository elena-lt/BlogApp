package com.data.repository.main.blog

import android.net.Uri
import com.domain.models.BlogPostDomain
import com.domain.dataState.DataState
import com.domain.viewState.BlogViewState
import kotlinx.coroutines.flow.Flow

interface BlogDataSource {

    fun searchBlogPosts(query: String, page: Int, filterAndOrder: String): Flow<DataState<BlogViewState>>

    fun checkAuthorOfBlogPost(slug: String): Flow<DataState<BlogViewState>>

    fun updateBlogPost(slug: String, blogTitle: String,  blogBody: String, imageUri: Uri): Flow<DataState<BlogViewState>>

    fun deleteBlogPost(blogPost: BlogPostDomain): Flow<DataState<BlogViewState>>
}