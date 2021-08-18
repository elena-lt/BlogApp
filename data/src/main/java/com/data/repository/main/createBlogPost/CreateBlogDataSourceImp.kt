package com.data.repository.main.createBlogPost

import android.net.Uri
import androidx.lifecycle.LiveData
import com.data.models.BlogCreateUpdateResponse
import com.data.models.BlogPostEntity
import com.data.network.main.OpenApiMainService
import com.data.persistance.BlogPostDao
import com.data.repository.JobManager
import com.data.repository.NetworkBoundResource
import com.data.session.SessionManager
import com.data.utils.GenericApiResponse
import com.data.utils.SuccessHandling.Companion.RESPONSE_MUST_BECOME_MEMBER
import com.domain.utils.AbsentLiveData
import com.domain.utils.DataState
import com.domain.utils.Response
import com.domain.utils.ResponseType
import com.domain.viewState.CreateNewBlogViewState
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@InternalCoroutinesApi
class CreateBlogDataSourceImp @Inject constructor(
    private val openApiMainService: OpenApiMainService,
    private val sessionManager: SessionManager,
    private val blogPostDao: BlogPostDao
) : CreateBlogDataSource, JobManager("CreateBlogDataSourceImp ") {

    override fun createNewBlog(
        title: String,
        body: String,
        imageUri: Uri
    ): LiveData<DataState<CreateNewBlogViewState>> {
        val authToken = sessionManager.cashedToken.value

        return object :
            NetworkBoundResource<BlogCreateUpdateResponse, BlogPostEntity, CreateNewBlogViewState>(
                isNetworkAvailable = sessionManager.isConnectedToInternet(),
                shouldLoadFromCache = false,
                shouldCancelIfNoInternet = true,
                isNetworkRequest = true
            ) {
            override suspend fun createCashRequestAndReturn() {
                /*NO-OPS*/
            }

            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<BlogCreateUpdateResponse>) {
                if (response.body.response != RESPONSE_MUST_BECOME_MEMBER) {
                    val updatedPost = BlogPostEntity(
                        response.body.pk,
                        response.body.title,
                        response.body.slug,
                        response.body.body,
                        response.body.image,
                        response.body.date_updated,
                        response.body.username,
                    )
                    updateLocalDb(updatedPost)
                }

                withContext(Main) {
                    onCompleteJob(
                        DataState.data(
                            null,
                            Response(response.body.response, ResponseType.Dialog())
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogCreateUpdateResponse>> {
                val titleRb = RequestBody.create(MediaType.parse("text/plain"), title)
                val bodyRb = RequestBody.create(MediaType.parse("text/plain"), body)

                val imageFile = imageUri.path?.let { File(it) }
                val imageRequestBody = RequestBody.create(MediaType.parse("image/*"), imageFile)
                val imageMultipartBody =
                    MultipartBody.Part.createFormData("image", imageFile?.name, imageRequestBody)
                return openApiMainService.createBlogPost(
                    "Token ${authToken?.token!!}",
                    titleRb,
                    bodyRb,
                    imageMultipartBody
                )
            }

            override fun loadFromCache(): LiveData<CreateNewBlogViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: BlogPostEntity?) {
                cacheObject?.let {
                    blogPostDao.insert(it)
                }
            }

            override fun setJob(job: Job) {
                addJob("createNewBlog", job)
            }

        }.asLiveData()
    }

}