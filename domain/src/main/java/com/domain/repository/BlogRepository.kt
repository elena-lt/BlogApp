package com.domain.repository

import androidx.lifecycle.LiveData
import com.domain.utils.DataState
import com.domain.viewState.BlogViewState

interface BlogRepository {

    fun searchBlogPosts(query: String, filterAndOrder: String, page: Int): LiveData<DataState<BlogViewState>>

    fun checkAuthorOfBlogPost(slug: String):LiveData<DataState<BlogViewState>>
}