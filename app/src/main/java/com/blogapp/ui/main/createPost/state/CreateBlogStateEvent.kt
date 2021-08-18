package com.blogapp.ui.main.createPost.state

import android.net.Uri
import okhttp3.MultipartBody

sealed class CreateBlogStateEvent {

    data class CreateNewBlogEvent (
        var title: String,
        val body: String,
        val image: Uri
            ): CreateBlogStateEvent()

    object None: CreateBlogStateEvent()
}