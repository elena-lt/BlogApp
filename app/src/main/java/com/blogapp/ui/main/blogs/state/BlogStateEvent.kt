package com.blogapp.ui.main.blogs.state

sealed class BlogStateEvent {

    object BlogSearchEvent: BlogStateEvent()

    object None: BlogStateEvent()
}