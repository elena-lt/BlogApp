package com.blogapp.ui.main.account

import com.blogapp.models.AccountProperties
import com.blogapp.models.mappers.AccountPropertiesMapper
import com.blogapp.ui.base.BaseViewModel
import com.blogapp.ui.main.account.state.AccountStateEvent
import com.blogapp.ui.main.account.state.AccountStateEvent.*
import com.data.session.SessionManager
import com.domain.models.AuthTokenDomain
import com.domain.usecases.main.account.ChangePasswordUseCase
import com.domain.usecases.main.account.GetAccountPropertiesUseCase
import com.domain.usecases.main.account.UpdateAccountPropertiesUseCase
import com.domain.dataState.DataState
import com.domain.dataState.MessageType
import com.domain.dataState.StateMessage
import com.domain.dataState.UIComponentType
import com.domain.viewState.AccountViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    private val getAccountPropertiesUseCase: GetAccountPropertiesUseCase,
    private val updateAccountProperties: UpdateAccountPropertiesUseCase,
    private val changePassword: ChangePasswordUseCase,
    private val sessionManager: SessionManager
) : BaseViewModel<AccountStateEvent, AccountViewState>() {

    override fun handleStateEvent(stateEvent: AccountStateEvent) {
         when (stateEvent) {
            is GetAccountPropertiesEvent -> {
                 sessionManager.cashedToken.value?.let { authToken -> //data.authToken
                    val token = AuthTokenDomain(authToken.account_primary_key, authToken.token)
                    getAccountPropertiesUseCase.invoke(token)
                } ?: flowOf(
                    DataState.ERROR(
                        stateMessage = StateMessage(
                            "null",
                            UIComponentType.NONE,
                            messageType = MessageType.NONE
                        )
                    )
                )

            }
            is UpdateAccountProperties -> {
                updateAccountProperties.invoke(stateEvent.email!!, stateEvent.username!!)
            }
            is ChangePasswordEvent -> {
                changePassword.involke(
                    stateEvent.currentPassword,
                    stateEvent.newPassword,
                    stateEvent.confirmNewPassword
                )
            }
            else -> {
                flowOf(
                    DataState.ERROR(
                        stateMessage = StateMessage(
                            "null",
                            UIComponentType.NONE,
                            messageType = MessageType.NONE
                        )
                    )
                )
            }
        }
    }

    fun setAccountPropertiesData(accountProperties: AccountProperties) {
        val update = currentState
        if (update.accountProperties == AccountPropertiesMapper.toAccountPropertiesDomain(
                accountProperties
            )
        ) return
        else {
            update.accountProperties =
                AccountPropertiesMapper.toAccountPropertiesDomain(accountProperties)
        }
    }

    fun logout() {
        sessionManager.logout()
    }

    override fun createInitialState(): AccountViewState {
        return AccountViewState()
    }
}