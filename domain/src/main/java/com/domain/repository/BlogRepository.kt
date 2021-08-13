package com.domain.repository

import androidx.lifecycle.LiveData
import com.domain.utils.DataState
import com.domain.viewState.BlogViewState

interface BlogRepository {

    fun searchBlogPosts(query: String, page: Int): LiveData<DataState<BlogViewState>>
}