package com.data.repository.main.blog

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.domain.models.BlogPostDomain
import com.domain.utils.DataState
import com.domain.viewState.BlogViewState
import kotlinx.coroutines.flow.Flow

interface BlogDataSource {

    suspend fun searchBlogPosts(query: String): Flow<PagingData<BlogPostDomain>>
}