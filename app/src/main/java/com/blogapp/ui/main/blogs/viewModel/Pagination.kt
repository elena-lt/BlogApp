package com.blogapp.ui.main.blogs.viewModel

import com.blogapp.models.mappers.BlogPostMapper
import com.blogapp.ui.main.blogs.state.BlogStateEvent
import com.domain.viewState.BlogViewState

fun BlogViewModel.resetPage() {
    val update = currentState
    update.blogFields.page = 1
    setViewState(update)
}

fun BlogViewModel.loadFirstPage() {
    setQueryInProgress(true)
    setQueryExhausted(false)
    resetPage()
    setStateEvent(BlogStateEvent.BlogSearchEvent)
}

fun BlogViewModel.incrementPageNumber() {
    val update = currentState
    val page = update.copy().blogFields.page
    update.blogFields.page = page + 1
    setViewState(update)
}

fun BlogViewModel.nextPage() {
    if (!getIsQueryExhausted() && !getIsQueryExhausted()) {
        incrementPageNumber()
        setQueryInProgress(true)
        setStateEvent(BlogStateEvent.BlogSearchEvent)
    }
}

fun BlogViewModel.handleIncomingBlogListData(state: BlogViewState) {
    setQueryExhausted(state.blogFields.isQueryExhausted)
    setQueryInProgress(state.blogFields.isQueryInProgress)
    setBlogList(state.blogFields.blogList.map {
        BlogPostMapper.toBlogPost(it)
    })
}