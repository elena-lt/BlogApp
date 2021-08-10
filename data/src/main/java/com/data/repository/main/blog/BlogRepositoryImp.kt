package com.data.repository.main.blog

import androidx.lifecycle.LiveData
import com.domain.repository.BlogRepository
import com.domain.utils.DataState
import com.domain.viewState.BlogViewState
import javax.inject.Inject

class BlogRepositoryImp @Inject constructor(
    private val blogDataSource: BlogDataSource
) : BlogRepository {

    override fun searchBlogPosts(query: String): LiveData<DataState<BlogViewState>> =
        blogDataSource.searchBlogPosts(query)
}