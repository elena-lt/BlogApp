package com.data.repository.main.blog

import android.util.Log
import androidx.paging.*
import com.data.network.main.OpenApiMainService
import com.data.persistance.BlogPostDao
import com.data.repository.JobManager
import com.data.repository.main.blog.pagingSource.BlogPostsPagingSource
import com.data.session.SessionManager
import com.domain.models.BlogPostDomain
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@InternalCoroutinesApi
class BlogDataSourceImp @Inject constructor(
    private val openApiMainService: OpenApiMainService,
    private val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
) : BlogDataSource, JobManager("BlogDataSourceImp") {

    override suspend fun searchBlogPosts(
        query: String
    ): Flow<PagingData<BlogPostDomain>> {
        Log.d("BLOG_REPOSITORY", "searchBlogPosts")

        return Pager(
            config = PagingConfig(
                pageSize = 10,
                maxSize = 100,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                BlogPostsPagingSource(openApiMainService, sessionManager) }
        ).flow

   }
//
//    override fun searchBlogPosts(
//        query: String
//    ): LiveData<DataState<BlogViewState>> {
//        val authToken: AuthToken? = sessionManager.cashedToken.value
//
//        val result = Pager(
//            config = PagingConfig(
//                pageSize = 10,
//                maxSize = 100,
//                enablePlaceholders = true
//            ),
//            pagingSourceFactory = { BlogPostsPagingSource(openApiMainService) }
//        )
//        val returnData =
//            DataState.data(data = BlogViewState(blogFields = BlogViewState.BlogFields(blogList = result)))
//
//        return returnData
//    }
}
