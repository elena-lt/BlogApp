package com.domain.usecases.main.blog

import android.net.Uri
import com.domain.repository.BlogRepository
import javax.inject.Inject

class UpdateBlogPostUseCase @Inject constructor(
    private val blogRepository: BlogRepository
) {

    fun invoke(slug: String, title: String, body: String, imageUri: Uri) =
        blogRepository.updateBlogPost(slug, title, body, imageUri)
}