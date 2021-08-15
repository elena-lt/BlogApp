package com.blogapp.ui.main.createPost.state

import okhttp3.MultipartBody

sealed class CreateBlogStateEvent {

    data class CreateNewBlogEvent (
        var title: String,
        val body: String,
        val image: MultipartBody.Part
            ): CreateBlogStateEvent()

    object None: CreateBlogStateEvent()
}