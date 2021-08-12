package com.domain.usecases.main.blog

import com.domain.repository.BlogRepository
import javax.inject.Inject

class SearchBlogPostsUseCase @Inject constructor(
    private val blogRepository: BlogRepository
) {

    suspend fun invoke(query: String) = blogRepository.searchBlogPosts(query)
}