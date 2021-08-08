package com.blogapp.ui.main.account.state

sealed class AccountStateEvent {

    object GetAccountPropertiesEvent: AccountStateEvent()

    data class UpdateAccountProperties(
        val email: String? = null,
        val username: String? = null
    ): AccountStateEvent()

    data class ChangePasswordEvent(
        val currentPassword: String,
        val newPassword: String,
        val confirmNewPassword: String
    ): AccountStateEvent()

    object None: AccountStateEvent()
}