package com.blogapp.ui.base

import android.util.Log
import androidx.lifecycle.*
import com.domain.dataState.DataState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.log

abstract class BaseViewModel<StateEvent, ViewState> : ViewModel() {

    private val initialState: ViewState by lazy { createInitialState() }
    abstract fun createInitialState(): ViewState

    private val _stateEvent: MutableSharedFlow<StateEvent> = MutableSharedFlow()
    val stateEvent = _stateEvent.asSharedFlow()

    private val _dataState: MutableSharedFlow<DataState<ViewState>> = MutableSharedFlow()
    val dataState = _dataState.asSharedFlow()

    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(initialState)
    val viewState = _viewState.asStateFlow()

    val currentState: ViewState
        get() = _viewState.value

    init {
        subscribeEvents()
    }

    private fun subscribeEvents() {
        viewModelScope.launch {
            stateEvent.collect {
                handleStateEvent(it)
            }
        }
    }

    fun setStateEvent(event: StateEvent) {
        viewModelScope.launch {
            _stateEvent.emit(event)
        }
    }

    fun setDataState(dataState: DataState<ViewState>) {
        viewModelScope.launch {
            _dataState.emit(dataState)
        }
    }

    fun setViewState(state: ViewState) {
        _viewState.value = state
    }
//
//    fun setNewViewState(reduce: ViewState.() -> ViewState) {
//        viewModelScope.launch {
//            val newState = currentState.reduce()
//            _viewState.emit(newState)
//        }
//    }

    abstract fun handleStateEvent(stateEvent: StateEvent)

}