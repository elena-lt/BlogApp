package com.data.repository.main.createBlogPost

import android.net.Uri
import com.data.network.main.OpenApiMainService
import com.data.persistance.BlogPostDao
import com.data.repository.JobManager
import com.data.session.SessionManager
import javax.inject.Inject

class CreateBlogDataSourceImp @Inject constructor(
    private val openApiMainService: OpenApiMainService,
    private val sessionManager: SessionManager,
    private val blogPostDao: BlogPostDao
) : CreateBlogDataSource, JobManager("CreateBlogDataSourceImp ") {

    override fun createNewBlog(title: String, body: String, imageUri: Uri) {
        TODO("Not yet implemented")
    }

}