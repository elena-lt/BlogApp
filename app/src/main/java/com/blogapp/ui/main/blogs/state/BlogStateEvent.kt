package com.blogapp.ui.main.blogs.state

sealed class BlogStateEvent {

    object BlogSearchEvent : BlogStateEvent()
    object CheckAuthorOfTheBlogPost : BlogStateEvent()
    object None : BlogStateEvent()
}