package com.data.repository.main.createBlogPost

import android.net.Uri
import androidx.lifecycle.LiveData
import com.domain.utils.DataState
import com.domain.viewState.CreateNewBlogViewState

interface CreateBlogDataSource {

    fun createNewBlog(title: String, body: String, imageUri: Uri): LiveData<DataState<CreateNewBlogViewState>>
}