package com.data.repository.main.blog

import android.net.Uri
import androidx.lifecycle.LiveData
import com.domain.models.BlogPostDomain
import com.domain.utils.DataState
import com.domain.viewState.BlogViewState

interface BlogDataSource {

    fun searchBlogPosts(query: String, page: Int, filterAndOrder: String): LiveData<DataState<BlogViewState>>

    fun checkAuthorOfBlogPost(slug: String): LiveData<DataState<BlogViewState>>

    fun updateBlogPost(slug: String, blogTitle: String,  blogBody: String, imageUri: Uri): LiveData<DataState<BlogViewState>>

    fun deleteBlogPost(blogPost: BlogPostDomain): LiveData<DataState<BlogViewState>>
}