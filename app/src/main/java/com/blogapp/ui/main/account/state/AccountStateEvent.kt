package com.blogapp.ui.main.account.state

import com.blogapp.ui.base.BaseStateEvent

sealed class AccountStateEvent : BaseStateEvent {

    object GetAccountPropertiesEvent: AccountStateEvent() {
        override fun errorInfo(): String {
            return "Error retrieving account properties."
        }
    }

    data class UpdateAccountProperties(
        val email: String? = null,
        val username: String? = null
    ): AccountStateEvent() {
        override fun errorInfo(): String {
            return "Error updating account properties."
        }
    }

    data class ChangePasswordEvent(
        val currentPassword: String,
        val newPassword: String,
        val confirmNewPassword: String
    ): AccountStateEvent() {
        override fun errorInfo(): String {
            return "Error changing password."
        }
    }

    object None: AccountStateEvent() {
        override fun errorInfo(): String {
            return "None"
        }
    }
}