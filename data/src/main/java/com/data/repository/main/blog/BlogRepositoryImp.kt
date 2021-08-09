package com.data.repository.main.blog

import com.domain.repository.BlogRepository
import javax.inject.Inject

class BlogRepositoryImp @Inject constructor(
    private val blogDataSource: BlogDataSource
): BlogRepository {
}