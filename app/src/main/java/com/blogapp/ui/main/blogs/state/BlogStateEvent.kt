package com.blogapp.ui.main.blogs.state

import android.net.Uri

sealed class BlogStateEvent {

    object BlogSearchEvent : BlogStateEvent()
    object CheckAuthorOfTheBlogPost : BlogStateEvent()
    object DeleteBlogStateEvent : BlogStateEvent()
    data class UpdateBlogStateEvent(
        val title: String,
        val body: String,
        var image: Uri
    ): BlogStateEvent()

    object None : BlogStateEvent()
}