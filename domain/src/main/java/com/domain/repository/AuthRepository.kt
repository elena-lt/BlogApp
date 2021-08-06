package com.domain.repository

import androidx.lifecycle.LiveData
import com.domain.utils.DataState
import com.domain.viewState.AuthViewState

interface AuthRepository {

    fun login(email: String, password: String): LiveData<DataState<AuthViewState>>

    fun register(email: String, username: String, password: String, confirmPassword: String):LiveData<DataState<AuthViewState>>

    fun checkPreviousAuthUser(): LiveData<DataState<AuthViewState>>
}