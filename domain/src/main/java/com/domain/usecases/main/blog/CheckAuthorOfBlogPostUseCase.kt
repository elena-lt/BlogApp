package com.domain.usecases.main.blog

import com.domain.repository.BlogRepository
import javax.inject.Inject

class CheckAuthorOfBlogPostUseCase @Inject constructor(
    private val blogRepository: BlogRepository
) {

    fun invoke(slug: String) = blogRepository.checkAuthorOfBlogPost(slug)
}