package com.data.repository.main.createBlogPost

import android.net.Uri
import com.data.models.AuthToken
import com.data.models.BlogCreateUpdateResponse
import com.data.models.BlogPostEntity
import com.data.network.main.OpenApiMainService
import com.data.persistance.BlogPostDao
import com.data.repository.JobManager
import com.data.repository.NetworkBoundResource
import com.data.session.SessionManager
import com.data.utils.SuccessHandling.Companion.SUCCESS_BLOG_CREATED
import com.domain.dataState.DataState
import com.domain.dataState.MessageType
import com.domain.dataState.StateMessage
import com.domain.dataState.UIComponentType
import com.domain.viewState.CreateNewBlogViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@ExperimentalCoroutinesApi
class CreateBlogDataSourceImp @Inject constructor(
    private val openApiMainService: OpenApiMainService,
    private val sessionManager: SessionManager,
    private val blogPostDao: BlogPostDao
) : CreateBlogDataSource, JobManager("CreateBlogDataSourceImp ") {

    private var authToken: AuthToken = sessionManager.cashedToken.value!!

    override fun createNewBlog(
        title: String,
        body: String,
        imageUri: Uri
    ): Flow<DataState<CreateNewBlogViewState>> {

        val title = RequestBody.create(MediaType.parse("text/plain"), title)
        val body = RequestBody.create(MediaType.parse("text/plain"), body)

        val imageFile = imageUri.path?.let { File(it) }
        val imageRequestBody = RequestBody.create(MediaType.parse("image/*"), imageFile!!)
        val image =
            MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)

        return object :
            NetworkBoundResource<BlogCreateUpdateResponse, BlogPostEntity, CreateNewBlogViewState>(
                IO,
                apiCall = {
                    openApiMainService.createBlogPost(
                        "Token ${authToken.token}", title, body, image
                    )
                }
            ) {

            override suspend fun handleNetworkSuccess(response: BlogCreateUpdateResponse): DataState<CreateNewBlogViewState>? {
                return if (response.response == SUCCESS_BLOG_CREATED) {
                    DataState.ERROR(
                        stateMessage = StateMessage(
                            message = SUCCESS_BLOG_CREATED,
                            uiComponentType = UIComponentType.DIALOG,
                            messageType = MessageType.SUCCESS
                        )
                    )
                } else null
            }

            override suspend fun updateCache(networkObject: BlogCreateUpdateResponse) {
                if (networkObject.response == SUCCESS_BLOG_CREATED) {
                    val blogPost = BlogPostEntity(
                        networkObject.pk,
                        networkObject.title,
                        networkObject.slug,
                        networkObject.body,
                        networkObject.image,
                        networkObject.date_updated,
                        networkObject.username
                    )

                    blogPostDao.insert(blogPost)
                }
            }
        }.result
    }

}