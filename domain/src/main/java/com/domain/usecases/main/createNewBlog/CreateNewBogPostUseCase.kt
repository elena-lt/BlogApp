package com.domain.usecases.main.createNewBlog

import android.net.Uri
import com.domain.repository.CreateBlogRepository
import javax.inject.Inject

class CreateNewBogPostUseCase @Inject constructor(
    private val createNewBlogRepository: CreateBlogRepository
) {

    fun invoke(title: String, body: String, imageUri: Uri) =
        createNewBlogRepository.createNewBlog(title, body, imageUri)
}