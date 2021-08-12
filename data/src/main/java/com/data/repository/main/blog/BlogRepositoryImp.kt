package com.data.repository.main.blog

import androidx.paging.PagingData
import com.domain.models.BlogPostDomain
import com.domain.repository.BlogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BlogRepositoryImp @Inject constructor(
    private val blogDataSource: BlogDataSource
) : BlogRepository {

    override suspend fun searchBlogPosts(query: String): Flow<PagingData<BlogPostDomain>> =
        blogDataSource.searchBlogPosts(query)
}