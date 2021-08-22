package com.data.repository.main.blog

import android.net.Uri
import com.data.models.*
import com.data.models.mappers.BlogPostMapper
import com.data.network.main.OpenApiMainService
import com.data.persistance.BlogPostDao
import com.data.repository.JobManager
import com.data.repository.NetworkBoundResource
import com.data.session.SessionManager
import com.data.utils.Const.PAGINATION_PAGE_SIZE
import com.data.utils.SuccessHandling.Companion.RESPONSE_HAS_PERMISSION_TO_EDIT
import com.data.utils.SuccessHandling.Companion.SUCCESS_BLOG_DELETED
import com.domain.dataState.DataState
import com.domain.dataState.MessageType
import com.domain.dataState.StateMessage
import com.domain.dataState.UIComponentType
import com.domain.models.BlogPostDomain
import com.domain.viewState.BlogViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@ExperimentalCoroutinesApi
class BlogDataSourceImp @Inject constructor(
    private val openApiMainService: OpenApiMainService,
    private val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
) : BlogDataSource, JobManager("BlogDataSourceImp") {

    override fun searchBlogPosts(
        query: String,
        page: Int,
        filterAndOrder: String
    ): Flow<DataState<BlogViewState>> {
        val authToken: AuthToken? = sessionManager.cashedToken.value

        return object :
            NetworkBoundResource<BlogSearchResponse, List<BlogPostEntity>, BlogViewState>(
                IO,
                apiCall = {
                    openApiMainService.searchListBlogPost(
                        authorization = "Token ${authToken?.token!!}",
                        query = query,
                        ordering = filterAndOrder,
                        page = page
                    )
                },
                cacheCall = {
                    blogPostDao.searchBlogPostsOrderByDateDESC(
                        query = query,
                        page = page,
                        pageSize = PAGINATION_PAGE_SIZE
                    )
                }
            ) {
            override suspend fun updateCache(networkObject: BlogSearchResponse) {
                for (blogPost in networkObject.results) {
                    blogPostDao.insert(blogPost)
                }
            }

            override suspend fun handleCacheSuccess(response: List<BlogPostEntity>): DataState<BlogViewState> {
                return DataState.SUCCESS(
                    BlogViewState(blogFields = BlogViewState.BlogFields(response.map {
                        BlogPostMapper.toBlogPostDomain(it)
                    }))
                )
            }

            override suspend fun handleNetworkSuccess(response: BlogSearchResponse): DataState<BlogViewState>? {
                val cacheResponse = cacheCall?.invoke()
                cacheResponse?.let {
                    return handleCacheSuccess(it)
                } ?: return null
            }
        }.result

    }


    override fun checkAuthorOfBlogPost(slug: String): Flow<DataState<BlogViewState>> {
        val authToken: AuthToken? = sessionManager.cashedToken.value

        return object : NetworkBoundResource<GenericResponse, Void, BlogViewState>(
            IO,
            apiCall = {
                openApiMainService.isAuthor(
                    authorization = "Token ${authToken?.token!!}",
                    slug = slug
                )
            }
        ) {
            override suspend fun handleNetworkSuccess(response: GenericResponse): DataState<BlogViewState>? {
                val isAuthor = response.response == RESPONSE_HAS_PERMISSION_TO_EDIT
                return DataState.SUCCESS(
                    BlogViewState(viewBlogFields = BlogViewState.ViewBlogFields(isAuthorOfBlogPost = isAuthor))
                )
            }

        }.result
    }

    override fun updateBlogPost(
        slug: String,
        blogTitle: String,
        blogBody: String,
        imageUri: Uri
    ): Flow<DataState<BlogViewState>> {
        val authToken: AuthToken? = sessionManager.cashedToken.value

        val title = RequestBody.create(MediaType.parse("text/plain"), blogTitle)
        val body = RequestBody.create(MediaType.parse("text/plain"), blogBody)

        val imageFile = imageUri.path?.let { File(it) }
        val imageRequestBody = imageFile?.let { RequestBody.create(MediaType.parse("image/*"), it) }
        val image = imageRequestBody?.let {
            MultipartBody.Part.createFormData("image", imageFile.name, it)
        }

        return object :
            NetworkBoundResource<BlogCreateUpdateResponse, BlogPostEntity, BlogViewState>(
                IO,
                apiCall = {
                    openApiMainService.updateBlogPost(
                        "Token ${authToken?.token!!}",
                        slug,
                        title,
                        body,
                        image
                    )
                }
            ) {
            override suspend fun updateCache(networkObject: BlogCreateUpdateResponse) {
                blogPostDao.updateBlogPost(
                    networkObject.pk,
                    networkObject.title,
                    networkObject.body,
                    networkObject.image
                )
            }

            override suspend fun handleNetworkSuccess(response: BlogCreateUpdateResponse): DataState<BlogViewState>? {
                val updatedPost = BlogPostDomain(
                    response.pk,
                    response.title,
                    response.slug,
                    response.body,
                    response.image,
                    response.date_updated,
                    response.username
                )
                return DataState.SUCCESS(
                    data = BlogViewState(viewBlogFields = BlogViewState.ViewBlogFields(blogPost = updatedPost))
                )
            }
        }.result
    }

    override fun deleteBlogPost(blogPost: BlogPostDomain): Flow<DataState<BlogViewState>> {
        val authToken: AuthToken? = sessionManager.cashedToken.value

        return object : NetworkBoundResource<GenericResponse, BlogPostEntity, BlogViewState>(
            IO,
            apiCall = {
                openApiMainService.deleteBlogPost(
                    authorization = "Token ${authToken?.token!!}",
                    slug = blogPost.slug
                )
            }
        ) {
            override suspend fun updateCache(networkObject: GenericResponse) {
                blogPostDao.deleteBlogPost(BlogPostMapper.toBlogPostData(blogPost))
            }

            override suspend fun handleNetworkSuccess(response: GenericResponse): DataState<BlogViewState>? {
                val isDeleted = response.response == SUCCESS_BLOG_DELETED
                return if (isDeleted) {
                    DataState.ERROR(
                        StateMessage(
                            message = SUCCESS_BLOG_DELETED,
                            uiComponentType = UIComponentType.TOAST,
                            messageType = MessageType.SUCCESS
                        )
                    )
                } else {
                    DataState.ERROR(
                        StateMessage(
                            message = "ERROR",
                            uiComponentType = UIComponentType.TOAST,
                            messageType = MessageType.ERROR
                        )
                    )
                }
            }
        }.result
    }
}
