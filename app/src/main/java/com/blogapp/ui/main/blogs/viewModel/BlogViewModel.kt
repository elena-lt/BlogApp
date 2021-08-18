package com.blogapp.ui.main.blogs.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import com.blogapp.models.BlogPost
import com.blogapp.models.mappers.BlogPostMapper
import com.blogapp.ui.base.BaseViewModel
import com.blogapp.ui.main.blogs.state.BlogStateEvent
import com.blogapp.ui.main.blogs.state.BlogStateEvent.*
import com.domain.usecases.main.blog.CheckAuthorOfBlogPostUseCase
import com.domain.usecases.main.blog.DeleteBlogPostUseCase
import com.domain.usecases.main.blog.SearchBlogPostsUseCase
import com.domain.utils.AbsentLiveData
import com.domain.utils.DataState
import com.domain.utils.Loading
import com.domain.viewState.BlogViewState
import javax.inject.Inject

class BlogViewModel @Inject constructor(
    private val searchBlogPost: SearchBlogPostsUseCase,
    private val checkAuthorOfBlogPost: CheckAuthorOfBlogPostUseCase,
    private val deleteBlogPost: DeleteBlogPostUseCase
) : BaseViewModel<BlogStateEvent, BlogViewState>() {

    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        return when (stateEvent) {
            is BlogSearchEvent -> {
                searchBlogPost.invoke(getSearchQuery(), getOrder()+ getFilter() , getPage())
            }
            is CheckAuthorOfTheBlogPost -> {
                checkAuthorOfBlogPost.invoke(getSlug())
            }
            is DeleteBlogStateEvent -> {
                deleteBlogPost.invoke(getBlogPost())
            }


            is None -> {
                object : LiveData<DataState<BlogViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        value = DataState(error = null, loading = Loading(false), data = null)
                    }
                }
            }
        }
    }

    override fun initNewViewState(): BlogViewState {
        return BlogViewState()
    }

}