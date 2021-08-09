package com.data.repository.main.blog

import com.data.network.main.OpenApiMainService
import com.data.persistance.BlogPostDao
import com.data.session.SessionManager
import javax.inject.Inject

class BlogDataSourceImp @Inject constructor(
    private val openApiMainService: OpenApiMainService,
    private val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
): BlogDataSource {


}