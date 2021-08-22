package com.blogapp.ui.main.blogs.state

import android.net.Uri
import com.blogapp.ui.base.BaseStateEvent

sealed class BlogStateEvent : BaseStateEvent {

    object BlogSearchEvent : BlogStateEvent() {
        override fun errorInfo(): String {
            return "Error searching for blog posts."
        }
    }

    object CheckAuthorOfTheBlogPost : BlogStateEvent() {
        override fun errorInfo(): String {
            return "Error checking if you are the author of this blog post."
        }
    }

    object DeleteBlogStateEvent : BlogStateEvent() {
        override fun errorInfo(): String {
            return "Error deleting that blog post."
        }
    }

    data class UpdateBlogStateEvent(
        val title: String,
        val body: String,
        var image: Uri
    ): BlogStateEvent() {
        override fun errorInfo(): String {
            return "Error updating that blog post."
        }
    }
}