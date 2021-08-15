package com.data.di.main

import androidx.room.Room
import com.data.network.main.OpenApiMainService
import com.data.persistance.AccountPropertiesDao
import com.data.persistance.AppDatabase
import com.data.persistance.BlogPostDao
import com.data.repository.main.account.AccountRepositoryImp
import com.data.repository.main.blog.BlogDataSource
import com.data.repository.main.blog.BlogDataSourceImp
import com.data.repository.main.blog.BlogRepositoryImp
import com.data.repository.main.createBlogPost.CreateBlogDataSource
import com.data.repository.main.createBlogPost.CreateBlogDataSourceImp
import com.data.repository.main.createBlogPost.CreateBlogRepositoryImp
import com.data.session.SessionManager
import com.domain.repository.AccountRepository
import com.domain.repository.BlogRepository
import com.domain.repository.CreateBlogRepository
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.InternalCoroutinesApi
import retrofit2.Retrofit

@Module
class MainModule {

    @MainScope
    @Provides
    fun provideOpenApiMainService(retrofit: Retrofit) =
        retrofit.create(OpenApiMainService::class.java)

    @InternalCoroutinesApi
    @MainScope
    @Provides
    fun provideAccountRepository(
        openApiMainService: OpenApiMainService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
    ): AccountRepository {

        return AccountRepositoryImp(openApiMainService, accountPropertiesDao, sessionManager)
    }

    @MainScope
    @Provides
    fun provideBlogPostDao(database: AppDatabase): BlogPostDao = database.getBlogPostDao()

    @InternalCoroutinesApi
    @MainScope
    @Provides
    fun provideBlogDataSource(
        openApiMainService: OpenApiMainService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): BlogDataSource = BlogDataSourceImp(openApiMainService, blogPostDao, sessionManager)

    @MainScope
    @Provides
    fun provideBlogRepository(blogDataSource: BlogDataSource): BlogRepository =
        BlogRepositoryImp(blogDataSource)

    @MainScope
    @Provides
    fun provideCreateBlogDataSource(
        openApiMainService: OpenApiMainService,
        sessionManager: SessionManager,
        blogPostDao: BlogPostDao
    ): CreateBlogDataSource =
        CreateBlogDataSourceImp(openApiMainService, sessionManager, blogPostDao)

    @MainScope
    @Provides
    fun provideCreateBlogRepository(dataSource: CreateBlogDataSource): CreateBlogRepository =
        CreateBlogRepositoryImp(dataSource)
}