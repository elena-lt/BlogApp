package com.domain.repository

import androidx.paging.PagingData
import com.domain.models.BlogPostDomain
import kotlinx.coroutines.flow.Flow

interface BlogRepository {

    suspend fun searchBlogPosts(query: String): Flow<PagingData<BlogPostDomain>>
}