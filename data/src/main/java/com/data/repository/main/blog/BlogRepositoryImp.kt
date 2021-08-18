package com.data.repository.main.blog

import android.net.Uri
import androidx.lifecycle.LiveData
import com.domain.models.BlogPostDomain
import com.domain.repository.BlogRepository
import com.domain.utils.DataState
import com.domain.viewState.BlogViewState
import javax.inject.Inject

class BlogRepositoryImp @Inject constructor(
    private val blogDataSource: BlogDataSource
) : BlogRepository {

    override fun searchBlogPosts(
        query: String,
        filterAndOrder: String,
        page: Int
    ): LiveData<DataState<BlogViewState>> =
        blogDataSource.searchBlogPosts(query, page, filterAndOrder)

    override fun checkAuthorOfBlogPost(slug: String): LiveData<DataState<BlogViewState>> =
        blogDataSource.checkAuthorOfBlogPost(slug)

    override fun updateBlogPost(slug: String, blogTitle: String,  blogBody: String, imageUri: Uri): LiveData<DataState<BlogViewState>> =
        blogDataSource.updateBlogPost(slug, blogTitle, blogBody, imageUri)

    override fun deleteBlog(blogPost: BlogPostDomain): LiveData<DataState<BlogViewState>> =
        blogDataSource.deleteBlogPost(blogPost)
}