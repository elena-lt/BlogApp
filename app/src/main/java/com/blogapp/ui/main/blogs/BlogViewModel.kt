package com.blogapp.ui.main.blogs

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.blogapp.models.BlogPost
import com.blogapp.models.mappers.BlogPostMapper
import com.blogapp.ui.base.BaseViewModel
import com.blogapp.ui.main.blogs.state.BlogStateEvent
import com.domain.models.BlogPostDomain
import com.domain.usecases.main.blog.SearchBlogPostsUseCase
import com.domain.utils.AbsentLiveData
import com.domain.utils.DataState
import com.domain.viewState.BlogViewState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@InternalCoroutinesApi
class BlogViewModel @Inject constructor(
    private val searchBlogPost: SearchBlogPostsUseCase
) : ViewModel() {

//    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
//          return when (stateEvent) {
//            is BlogStateEvent.BlogSearchEvent -> {
//                fetchBlogs()
//            }
//            is BlogStateEvent.CheckAuthorOfTheBlogPost -> {
//
//            }
//
//            is BlogStateEvent.None -> {
//
//            }
//        }
//    }

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
                Log.d("AppDebug", "handleIntent: GetAllCharacters")
                fetchBlogs()
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

//    fun setQuery(query: String) {
//        val update = getCurrentViewStateOrNew()
//        if (query == update.blogFields.searchQuery) return
//        update.blogFields.searchQuery = query
//        _viewState.value = update
//    }
//
//    fun setBlogList(blogList: List<BlogPost>) {
//        val update = getCurrentViewStateOrNew()
//        update.blogFields.blogList = blogList.map {
//            BlogPostMapper.toBlogPostDomain(it)
//        }
//        _viewState.value = update
//    }
//
//    fun setBlogPostsList(blogList: LiveData<PagingData<BlogPostDomain>>) {
//        val update = getCurrentViewStateOrNew()
//        update.blogFields.blogPosts = blogList
//        _viewState.value = update
//    }
//
//    fun setBlogPost(blogPost: BlogPost) {
//        val update = getCurrentViewStateOrNew()
//        update.viewBlogFields.blogPost = BlogPostMapper.toBlogPostDomain(blogPost)
//        _viewState.value = update
//    }
//
//    fun setAuthorOfBlogPost(isAuthorOfBlogPost: Boolean) {
//        val update = getCurrentViewStateOrNew()
//        update.viewBlogFields.isAuthorOfBlogPost = isAuthorOfBlogPost
//        _viewState.value = update
//    }
