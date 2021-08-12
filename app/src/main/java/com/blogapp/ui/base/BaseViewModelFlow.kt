package com.blogapp.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseViewModelFlow<StateEvent, ViewState> : ViewModel() {

    private val initialState: ViewState by lazy { createInitialState() }
    abstract fun createInitialState(): ViewState

    val currentState: ViewState
        get() = _state.value

    private val _event: MutableSharedFlow<StateEvent> = MutableSharedFlow()
    val event = _event.asSharedFlow()

    private val _state: MutableStateFlow<ViewState> = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    init {
        subscribeEvents()
    }

    private fun subscribeEvents(){
        viewModelScope.launch {
            event.collect {
                handleEvent(it)
            }
        }
    }

    abstract fun handleEvent(event: StateEvent)

    fun setEvent(event: StateEvent){
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    fun setState(reduce: ViewState.() -> ViewState){
        val newState = currentState.reduce()
        _state.value = newState
    }

}