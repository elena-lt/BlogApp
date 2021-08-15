package com.data.repository.main.createBlogPost

import android.net.Uri

interface CreateBlogDataSource {

    fun createNewBlog(title: String, body: String, imageUri: Uri)
}