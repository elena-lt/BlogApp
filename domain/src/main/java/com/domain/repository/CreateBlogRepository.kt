package com.domain.repository

import android.net.Uri

interface CreateBlogRepository {

    fun createNewBlog(title: String, body: String, imageUri: Uri)
}