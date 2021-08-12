package com.domain.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.domain.models.BlogPostDomain
import com.domain.utils.DataState
import com.domain.viewState.BlogViewState
import kotlinx.coroutines.flow.Flow


interface BlogRepository {

    suspend fun searchBlogPosts(query: String): Flow<PagingData<BlogPostDomain>>
}