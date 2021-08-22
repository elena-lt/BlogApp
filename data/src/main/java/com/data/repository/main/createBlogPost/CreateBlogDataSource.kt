package com.data.repository.main.createBlogPost

import android.net.Uri
import com.domain.dataState.DataState
import com.domain.viewState.CreateNewBlogViewState
import kotlinx.coroutines.flow.Flow

interface CreateBlogDataSource {

    fun createNewBlog(title: String, body: String, imageUri: Uri): Flow<DataState<CreateNewBlogViewState>>
}