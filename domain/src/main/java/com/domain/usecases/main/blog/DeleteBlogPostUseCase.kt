package com.domain.usecases.main.blog

import com.domain.models.BlogPostDomain
import com.domain.repository.BlogRepository
import javax.inject.Inject

class DeleteBlogPostUseCase @Inject constructor(
    private val blogRepository: BlogRepository
) {

    fun invoke (blogPost: BlogPostDomain) = blogRepository.deleteBlog(blogPost)
}