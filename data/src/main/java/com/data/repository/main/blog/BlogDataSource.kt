package com.data.repository.main.blog


import androidx.paging.PagingData
import com.domain.models.BlogPostDomain
import kotlinx.coroutines.flow.Flow

interface BlogDataSource {

    suspend fun searchBlogPosts(query: String): Flow<PagingData<BlogPostDomain>>
}