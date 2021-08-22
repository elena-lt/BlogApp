package com.blogapp.ui.main.createPost

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.blogapp.ui.base.BaseViewModel
import com.blogapp.ui.main.createPost.state.CreateBlogStateEvent
import com.domain.usecases.main.createNewBlog.CreateNewBogPostUseCase
import com.domain.dataState.DataState
import com.domain.dataState.MessageType
import com.domain.dataState.StateMessage
import com.domain.dataState.UIComponentType
import com.domain.viewState.CreateNewBlogViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateBlogPostViewModel @Inject constructor(
    private val createBlog: CreateNewBogPostUseCase
) : BaseViewModel<CreateBlogStateEvent, CreateNewBlogViewState>() {

    override fun handleStateEvent(stateEvent: CreateBlogStateEvent) {
        when (stateEvent) {
            is CreateBlogStateEvent.CreateNewBlogEvent -> {
                createNewBlogPost(stateEvent.title, stateEvent.body, stateEvent.image)
            }
        }
    }

    private fun createNewBlogPost(title: String, body: String, imageUri: Uri) {
        viewModelScope.launch {
            createBlog.invoke(title, body, imageUri).collect { dataState ->
                setDataState(dataState)
            }
        }
    }

    fun setNewBlogFields(title: String?, body: String?, uri: Uri?) {
        val newState = currentState.copy(newBlogFields = CreateNewBlogViewState.NewBlogFields(
            newBlogTitle = title?.let { it },
            newBlogBody = body?.let { it },
            newBlogImageUri = uri?.let { it } ?: currentState.newBlogFields.newBlogImageUri
        ))

        setViewState(newState)
    }

    fun clearNewBlogFields() {
        val update = currentState
        update.newBlogFields = CreateNewBlogViewState.NewBlogFields()
        setViewState(update)
    }

    override fun createInitialState(): CreateNewBlogViewState {
        return CreateNewBlogViewState()
    }
}