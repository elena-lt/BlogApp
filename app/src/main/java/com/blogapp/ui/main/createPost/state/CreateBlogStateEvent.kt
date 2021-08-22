package com.blogapp.ui.main.createPost.state

import android.net.Uri
import com.blogapp.ui.base.BaseStateEvent
import okhttp3.MultipartBody

sealed class CreateBlogStateEvent : BaseStateEvent {

    data class CreateNewBlogEvent (
        var title: String,
        val body: String,
        val image: Uri
            ): CreateBlogStateEvent() {
        override fun errorInfo(): String {
            return "Unable to create a new blog post."
        }
    }
}