package com.blogapp.ui.main.blogs

import androidx.lifecycle.viewModelScope
import com.blogapp.ui.base.BaseViewModelFlow
import com.blogapp.ui.main.blogs.state.BlogStateEvent
import com.blogapp.ui.main.blogs.state.BlogViewState
import com.domain.usecases.main.blog.SearchBlogPostsUseCase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@InternalCoroutinesApi
class BlogViewModel @Inject constructor(
    private val searchBlogPost: SearchBlogPostsUseCase
) : BaseViewModelFlow<BlogStateEvent, BlogViewState>() {

    override fun createInitialState(): BlogViewState {
        return BlogViewState()
    }

    override fun handleEvent(event: BlogStateEvent) {
        when (event) {
            is BlogStateEvent.BlogSearchEvent -> {
                fetchBlogs()
            }
            is BlogStateEvent.CheckAuthorOfTheBlogPost -> {

            }
        }
    }

    private fun fetchBlogs() {
        viewModelScope.launch {
            searchBlogPost.invoke(state.value.blogFields.searchQuery).collect { data ->
                setState {
                    BlogViewState(blogFields = BlogViewState.BlogFields(blogPosts = data))
                }

            }
        }
    }
}
