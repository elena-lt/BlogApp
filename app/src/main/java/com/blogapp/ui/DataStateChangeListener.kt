package com.blogapp.ui

import com.domain.utils.DataState

interface DataStateChangeListener {

    fun dataStateChange(dataState: DataState<*>)
}