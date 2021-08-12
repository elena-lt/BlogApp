package com.blogapp.ui.main.blogs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
) : ViewModel() {

    private val _event: MutableSharedFlow<BlogStateEvent> = MutableSharedFlow()
    val event = _event.asSharedFlow()

    private val _state: MutableStateFlow<BlogViewState.BlogFields> =
        MutableStateFlow(BlogViewState.BlogFields())
    val state = _state.asStateFlow()

    val currentState: BlogViewState.BlogFields
        get() = state.value

    init {
        subscribeEvents()
    }

    private fun subscribeEvents() {
        viewModelScope.launch {
            event.collect {
                handleIntent(it)
            }
        }
    }

    private fun handleIntent(intent: BlogStateEvent) {
        when (intent) {
            is BlogStateEvent.BlogSearchEvent -> {
                fetchBlogs()
            }
            is BlogStateEvent.CheckAuthorOfTheBlogPost -> {

            }
        }
    }

    fun setEvent(event: BlogStateEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    fun setState(reduce: BlogViewState.BlogFields.() -> BlogViewState.BlogFields) {
        val newState = currentState.reduce()
        _state.value = newState
    }

    private fun fetchBlogs() {
        viewModelScope.launch {
            searchBlogPost.invoke(state.value.searchQuery).collect { data ->
                setState {
                    BlogViewState.BlogFields(blogPosts = data)
                }

            }
        }
    }
}
