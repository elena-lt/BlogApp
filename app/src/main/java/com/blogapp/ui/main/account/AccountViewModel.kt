package com.blogapp.ui.main.account

import androidx.lifecycle.LiveData
import com.blogapp.models.AccountProperties
import com.blogapp.models.mappers.AccountPropertiesMapper
import com.blogapp.ui.base.BaseViewModel
import com.blogapp.ui.main.account.state.AccountStateEvent
import com.blogapp.ui.main.account.state.AccountStateEvent.*
import com.data.models.AuthToken
import com.data.session.SessionManager
import com.domain.models.AuthTokenDomain
import com.domain.usecases.main.account.ChangePasswordUseCase
import com.domain.usecases.main.account.GetAccountPropertiesUseCase
import com.domain.usecases.main.account.UpdateAccountPropertiesUseCase
import com.domain.utils.AbsentLiveData
import com.domain.utils.DataState
import com.domain.viewState.AccountViewState
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    private val getAccountPropertiesUseCase: GetAccountPropertiesUseCase,
    private val updateAccountProperties: UpdateAccountPropertiesUseCase,
    private val changePassword: ChangePasswordUseCase,
    private val sessionManager: SessionManager
) : BaseViewModel<AccountStateEvent, AccountViewState>() {

    //TODO ("create mapper: AuthToken")
    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        return when (stateEvent) {
            is GetAccountPropertiesEvent -> {
                return sessionManager.cashedToken.value?.let { authToken -> //data.authToken
                    val token = AuthTokenDomain(authToken.account_primary_key, authToken.token)
                    getAccountPropertiesUseCase.invoke(token)
                } ?: AbsentLiveData.create()

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
            is None -> {
                AbsentLiveData.create()
            }
        }
    }

    fun setAccountPropertiesData(accountProperties: AccountProperties) {
        val update = getCurrentViewStateOrNew()
        if (update.accountProperties == AccountPropertiesMapper.toAccountPropertiesDomain(
                accountProperties
            )
        ) return
        else {
            update.accountProperties =
                AccountPropertiesMapper.toAccountPropertiesDomain(accountProperties)
            _viewState.value = update
        }
    }

    fun logout() {
        sessionManager.logout()
    }

    override fun initNewViewState(): AccountViewState = AccountViewState()
}