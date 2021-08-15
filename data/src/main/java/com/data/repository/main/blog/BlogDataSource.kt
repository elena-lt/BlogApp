package com.data.repository.main.blog

import androidx.lifecycle.LiveData
import com.domain.utils.DataState
import com.domain.viewState.BlogViewState

interface BlogDataSource {

    fun searchBlogPosts(query: String, page: Int, filterAndOrder: String): LiveData<DataState<BlogViewState>>
}