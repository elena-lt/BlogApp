package com.blogapp.ui.main.blogs.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.blogapp.ui.base.BaseViewModel
import com.blogapp.ui.main.blogs.state.BlogStateEvent
import com.blogapp.ui.main.blogs.state.BlogStateEvent.*
import com.domain.usecases.main.blog.CheckAuthorOfBlogPostUseCase
import com.domain.usecases.main.blog.DeleteBlogPostUseCase
import com.domain.usecases.main.blog.SearchBlogPostsUseCase
import com.domain.usecases.main.blog.UpdateBlogPostUseCase
import com.domain.dataState.DataState
import com.domain.dataState.MessageType
import com.domain.dataState.StateMessage
import com.domain.dataState.UIComponentType
import com.domain.viewState.BlogViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

class BlogViewModel @Inject constructor(
    private val searchBlogPost: SearchBlogPostsUseCase,
    private val checkAuthorOfBlogPost: CheckAuthorOfBlogPostUseCase,
    private val updateBlogPost: UpdateBlogPostUseCase,
    private val deleteBlogPost: DeleteBlogPostUseCase
) : BaseViewModel<BlogStateEvent, BlogViewState>() {

    override fun handleStateEvent(stateEvent: BlogStateEvent) {
        when (stateEvent) {
            is BlogSearchEvent -> {
                fetchBlogPosts(getSearchQuery(), getOrder() + getFilter(), getPage())
            }
            is CheckAuthorOfTheBlogPost -> {
                checkIfCurrUserIsAuthor()
            }
            is DeleteBlogStateEvent -> {
                deleteBlogPost()
            }
            is UpdateBlogStateEvent -> {
                updateBlogPost(
                    getSlug(),
                    stateEvent.title,
                    stateEvent.body,
                    stateEvent.image
                )
            }
        }
    }

    private fun fetchBlogPosts(query: String, orderAndFilter: String, page: Int) {
        viewModelScope.launch {
            searchBlogPost.invoke(query, orderAndFilter, page).collect { dataState ->
                setDataState(dataState)
            }
        }
    }

    private fun checkIfCurrUserIsAuthor() {
        viewModelScope.launch {
            checkAuthorOfBlogPost.invoke(getSlug()).collect { dataState ->
                setDataState(dataState)
            }
        }
    }

    private fun updateBlogPost(slug: String, title: String, body: String, imageUri: Uri) {
        viewModelScope.launch {
            updateBlogPost.invoke(slug, title, body, imageUri).collect { dataState ->
                setDataState(dataState)
            }
        }
    }

    private fun deleteBlogPost() {
        viewModelScope.launch {
            deleteBlogPost.invoke(getBlogPost()).collect { dataState ->
                setDataState(dataState)
            }
        }
    }

    override fun createInitialState(): BlogViewState {
        return BlogViewState()
    }

}