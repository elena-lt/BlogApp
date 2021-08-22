package com.blogapp.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.blogapp.R
import com.blogapp.databinding.FragmentAccountBinding
import com.blogapp.models.AccountProperties
import com.blogapp.models.mappers.AccountPropertiesMapper
import com.blogapp.ui.main.account.state.AccountStateEvent

class AccountFragment : BaseAccountFragment<FragmentAccountBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentAccountBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleOnClickEvents()
        subscribeToObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.setStateEvent(AccountStateEvent.GetAccountPropertiesEvent)
    }

    private fun handleOnClickEvents() {
        binding.changePassword.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
        }

        binding.logoutButton.setOnClickListener {
            viewModel.logout()
        }

        binding.updateAccount.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_updateAccountFragment)
        }
    }

    private fun subscribeToObservers() {
//
//
//        viewModel.dataState.observe(viewLifecycleOwner, { dataState ->
//            stateChangeListener.dataStateChange(dataState)
//            dataState?.let {
//                it.data?.let { data ->
//                    data.data?.let { event ->
//                        event.getContentIfNotHandled()?.let { accountViewState ->
//                            accountViewState.accountProperties?.let { accountProperties ->
//                                viewModel.setAccountPropertiesData(
//                                    AccountPropertiesMapper.toAccountProperties(
//                                        accountProperties
//                                    )
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        })
//
//        viewModel.viewState.observe(viewLifecycleOwner, { viewState ->
//            viewState?.let {
//                it.accountProperties?.let { accountProperties ->
//                    setAccountProperties(
//                        AccountPropertiesMapper.toAccountProperties(
//                            accountProperties
//                        )
//                    )
//                }
//            }
//        })
    }

    private fun setAccountProperties(accountProperties: AccountProperties) {
        Log.d(TAG, "setAccountProperties: $accountProperties")
        binding.email.text = accountProperties.email
        binding.username.text = accountProperties.username
    }


}