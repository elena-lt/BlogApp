package com.data.repository.main.createBlogPost

import android.net.Uri
import com.domain.repository.CreateBlogRepository

import javax.inject.Inject

class CreateBlogRepositoryImp @Inject constructor(
    private val dataSource: CreateBlogDataSource
) : CreateBlogRepository {

    override fun createNewBlog(title: String, body: String, imageUri: Uri) =
        dataSource.createNewBlog(title, body, imageUri)


}