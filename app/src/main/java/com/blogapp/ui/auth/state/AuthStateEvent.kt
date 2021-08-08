package com.blogapp.ui.auth.state

sealed class AuthStateEvent {

    data class LoginEvent(
        val email: String,
        val password: String
    ) : AuthStateEvent()

    data class RegisterEvent(
        val email: String,
        val username: String,
        val password: String,
        val confirmPassword: String
    ) : AuthStateEvent()

    object CheckPreviousAuthEvent : AuthStateEvent()

    object None: AuthStateEvent()
}