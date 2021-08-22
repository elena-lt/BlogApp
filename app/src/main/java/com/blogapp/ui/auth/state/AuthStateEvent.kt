package com.blogapp.ui.auth.state

import com.blogapp.ui.base.BaseStateEvent

sealed class AuthStateEvent : BaseStateEvent {

    data class LoginEvent(
        val email: String,
        val password: String
    ) : AuthStateEvent() {
        override fun errorInfo(): String {
            return "Login attempt failed."
        }
    }

    data class RegisterEvent(
        val email: String,
        val username: String,
        val password: String,
        val confirmPassword: String
    ) : AuthStateEvent() {
        override fun errorInfo(): String {
            return "Register attempt failed."
        }
    }

    object CheckPreviousAuthEvent : AuthStateEvent() {
        override fun errorInfo(): String {
            return "Error checking for previously authenticated user."
        }
    }

//    object None: AuthStateEvent() {
//        override fun errorInfo(): String {
//            return "None"
//        }
//    }
}