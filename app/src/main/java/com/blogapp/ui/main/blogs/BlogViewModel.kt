package com.blogapp.ui.main.blogs

import androidx.lifecycle.LiveData
import com.blogapp.models.BlogPost
import com.blogapp.models.mappers.BlogPostMapper
import com.blogapp.ui.base.BaseViewModel
import com.blogapp.ui.main.blogs.state.BlogStateEvent
import com.domain.usecases.main.blog.SearchBlogPostsUseCase
import com.domain.utils.AbsentLiveData
import com.domain.utils.DataState
import com.domain.viewState.BlogViewState
import javax.inject.Inject

class BlogViewModel @Inject constructor(
    private val searchBlogPost: SearchBlogPostsUseCase
) : BaseViewModel<BlogStateEvent, BlogViewState>() {

    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        return when (stateEvent) {
            is BlogStateEvent.BlogSearchEvent -> {
                searchBlogPost.invoke(viewState.value?.blogFields?.searchQuery ?:"")
            }
            is BlogStateEvent.None -> {
                AbsentLiveData.create()
            }
        }
    }

    override fun initNewViewState(): BlogViewState {
        return BlogViewState()
    }

    fun setQuery(query: String) {
        val update = getCurrentViewStateOrNew()
        if (query == update.blogFields.searchQuery) return
        update.blogFields.searchQuery = query
        _viewState.value = update
    }

    fun setBlogList(blogList: List<BlogPost>) {
        val update = getCurrentViewStateOrNew()
        update.blogFields.blogList = blogList.map {
            BlogPostMapper.toBlogPostDomain(it)
        }
        _viewState.value = update
    }


}