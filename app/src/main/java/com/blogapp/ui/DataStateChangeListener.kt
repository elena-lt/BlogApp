package com.blogapp.ui

import com.domain.dataState.DataState

interface DataStateChangeListener {

    fun dataStateChange(dataState: DataState<*>)

    fun hideSoftKeyboard()

    fun isStoragePermissionGranted(): Boolean
}