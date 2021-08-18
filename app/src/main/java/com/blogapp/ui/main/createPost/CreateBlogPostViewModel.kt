package com.blogapp.ui.main.createPost

import android.net.Uri
import androidx.lifecycle.LiveData
import com.blogapp.ui.base.BaseViewModel
import com.blogapp.ui.main.createPost.state.CreateBlogStateEvent
import com.domain.usecases.main.createNewBlog.CreateNewBogPostUseCase
import com.domain.utils.AbsentLiveData
import com.domain.utils.DataState
import com.domain.viewState.CreateNewBlogViewState
import javax.inject.Inject

class CreateBlogPostViewModel @Inject constructor(
    private val createBlog: CreateNewBogPostUseCase
) : BaseViewModel<CreateBlogStateEvent, CreateNewBlogViewState>() {

    override fun initNewViewState(): CreateNewBlogViewState = CreateNewBlogViewState()

    override fun handleStateEvent(stateEvent: CreateBlogStateEvent): LiveData<DataState<CreateNewBlogViewState>> {
        return when (stateEvent) {
            is CreateBlogStateEvent.CreateNewBlogEvent -> {
                createBlog.invoke(stateEvent.title, stateEvent.body, stateEvent.image)
            }
            is CreateBlogStateEvent.None -> {
                AbsentLiveData.create()
            }
        }
    }

    fun setNewBlogFields(title: String?, body: String?, uri: Uri?) {
        val update = getCurrentViewStateOrNew()
        val newBlogFields = update.newBlogFields
        title?.let { newBlogFields.newBlogTitle = it }
        body?.let { newBlogFields.newBlogBody = it }
        uri?.let { newBlogFields.newBlogImageUri = it }
        update.newBlogFields = newBlogFields
        _viewState.value = update
    }

    fun clearNewBlogFields() {
        val update = getCurrentViewStateOrNew()
        update.newBlogFields = CreateNewBlogViewState.NewBlogFields()
        setViewState(update)
    }
}