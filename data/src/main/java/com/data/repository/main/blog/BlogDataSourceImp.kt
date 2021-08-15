package com.data.repository.main.blog

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.data.models.AuthToken
import com.data.models.BlogPostEntity
import com.data.models.BlogSearchResponse
import com.data.models.mappers.BlogPostMapper
import com.data.network.main.OpenApiMainService
import com.data.persistance.BlogPostDao
import com.data.persistance.returnOrderedBlogQuery
import com.data.repository.JobManager
import com.data.repository.NetworkBoundResource
import com.data.session.SessionManager
import com.data.utils.Const.PAGINATION_PAGE_SIZE
import com.data.utils.GenericApiResponse
import com.domain.utils.DataState
import com.domain.viewState.BlogViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@InternalCoroutinesApi
class BlogDataSourceImp @Inject constructor(
    private val openApiMainService: OpenApiMainService,
    private val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
) : BlogDataSource, JobManager("BlogDataSourceImp") {

    override fun searchBlogPosts(
        query: String,
        page: Int,
        filterAndOrder: String
    ): LiveData<DataState<BlogViewState>> {
        val authToken: AuthToken? = sessionManager.cashedToken.value

        return object :
            NetworkBoundResource<BlogSearchResponse, List<BlogPostEntity>, BlogViewState>(
                isNetworkAvailable = sessionManager.isConnectedToInternet(),
                isNetworkRequest = true,
                shouldCancelIfNoInternet = false,
                shouldLoadFromCache = true

            ) {
            override suspend fun createCashRequestAndReturn() {
                withContext(Main) {
                    result.addSource(loadFromCache()) { viewState ->
                        viewState.blogFields.isQueryInProgress = false
                        if (page * PAGINATION_PAGE_SIZE > viewState.blogFields.blogList.size) {
                            viewState.blogFields.isQueryExhausted = true
                        }
                        onCompleteJob(DataState.data(viewState, null))
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(
                response: GenericApiResponse.ApiSuccessResponse<BlogSearchResponse>
            ) {
                updateLocalDb(response.body.results)
                createCashRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogSearchResponse>> {
                return openApiMainService.searchListBlogPost(
                    "Token ${authToken!!.token}", query, filterAndOrder, page
                )
            }

            override fun loadFromCache(): LiveData<BlogViewState> {
                val blogPosts = blogPostDao.returnOrderedBlogQuery(query, filterAndOrder, page)
                return Transformations.switchMap(blogPosts) {
                    object : LiveData<BlogViewState>() {
                        override fun onActive() {
                            super.onActive()
                            value = BlogViewState(
                                BlogViewState.BlogFields(
                                    blogList = it.map { blogPost ->
                                        BlogPostMapper.toBlogPostDomain(blogPost)

                                    }, isQueryInProgress = true
                                )
                            )
                        }
                    }
                }
            }

            override suspend fun updateLocalDb(cacheObject: List<BlogPostEntity>?) {
                if (cacheObject != null) {
                    withContext(IO) {
                        for (blogPost in cacheObject) {
                            try {
                                //launch each insert as a separate job tp executed in parallel
                                launch {
//                                    Log.d(
//                                        "AppDebug",
//                                        "updateLocalDb: inserting blog: $blogPost"
//                                    )
                                    blogPostDao.insert(blogPost)
                                }

                            } catch (e: Exception) {
                                Log.e(
                                    "AppDebug",
                                    "updateLocalDb: error updating cache on " +
                                            "blog post with slug ${blogPost.slug}"
                                )
                            }
                        }
                    }
                }
            }

            override fun setJob(job: Job) {
                addJob("searchBlogPosts", job)
            }

        }.asLiveData()
    }
}
