package com.data.repository.main.blog

import android.net.Uri
import androidx.lifecycle.LiveData
import com.domain.models.BlogPostDomain
import com.domain.repository.BlogRepository
import com.domain.dataState.DataState
import com.domain.viewState.BlogViewState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BlogRepositoryImp @Inject constructor(
    private val blogDataSource: BlogDataSource
) : BlogRepository {

    override fun searchBlogPosts(
        query: String,
        filterAndOrder: String,
        page: Int
    ): Flow<DataState<BlogViewState>> =
        blogDataSource.searchBlogPosts(query, page, filterAndOrder)

    override fun checkAuthorOfBlogPost(slug: String): Flow<DataState<BlogViewState>> =
        blogDataSource.checkAuthorOfBlogPost(slug)

    override fun updateBlogPost(slug: String, blogTitle: String,  blogBody: String, imageUri: Uri): Flow<DataState<BlogViewState>> =
        blogDataSource.updateBlogPost(slug, blogTitle, blogBody, imageUri)

    override fun deleteBlog(blogPost: BlogPostDomain): Flow<DataState<BlogViewState>> =
        blogDataSource.deleteBlogPost(blogPost)
}