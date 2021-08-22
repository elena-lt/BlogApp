package com.blogapp.ui.auth

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.blogapp.ui.base.BaseViewModel
import com.blogapp.ui.auth.state.AuthStateEvent
import com.blogapp.ui.auth.state.AuthStateEvent.*
import com.domain.usecases.auth.CheckPreviousAuthUserUseCase
import com.domain.usecases.auth.LoginUseCase
import com.domain.usecases.auth.RegisterUseCase
import com.domain.viewState.AuthViewState
import com.domain.viewState.LoginFields
import com.domain.viewState.RegistrationFields
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val login: LoginUseCase,
    private val register: RegisterUseCase,
    private val checkPrevAuthUser: CheckPreviousAuthUserUseCase
) : BaseViewModel<AuthStateEvent, AuthViewState>() {

    override fun createInitialState(): AuthViewState {
        return AuthViewState()
    }

    override fun handleStateEvent(stateEvent: AuthStateEvent) {
        when (stateEvent) {
            is LoginEvent -> {
                login(stateEvent.email, stateEvent.password)
            }
            is RegisterEvent -> {
                register(
                    stateEvent.email,
                    stateEvent.password,
                    stateEvent.confirmPassword,
                    stateEvent.username
                )
            }
            is CheckPreviousAuthEvent -> {
                checkPrevAuthUser()
            }
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            login.invoke(email, password).collect {
                setDataState(it)
            }
        }
    }

    private fun register(
        email: String,
        password: String,
        confirmPassword: String,
        username: String
    ) {
        viewModelScope.launch {
            register.invoke(email, username, password, confirmPassword).collect {
                setDataState(it)
            }
        }
    }

    private fun checkPrevAuthUser(){
       viewModelScope.launch {
           checkPrevAuthUser.invoke().collect { dataState ->
               setDataState(dataState)
           }
       }
    }

    fun setRegistrationFields(regFields: RegistrationFields) {
        val update =currentState
        if (update.registrationFields == regFields) {
            return
        }
        update.registrationFields = regFields
    }

    fun setLoginFields(loginFields: LoginFields) {
        val update = currentState
        if (update.LoginFields == loginFields) {
            return
        }
        update.LoginFields = loginFields
    }
}