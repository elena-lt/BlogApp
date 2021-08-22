package com.blogapp.ui.main.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.blogapp.R
import com.blogapp.databinding.FragmentChangePasswordBinding
import com.blogapp.ui.main.account.state.AccountStateEvent
import com.blogapp.utils.Const.RESPONSE_PASSWORD_UPDATE_SUCCESS
import kotlinx.coroutines.flow.collect

class ChangePasswordFragment : BaseAccountFragment<FragmentChangePasswordBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentChangePasswordBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleOnClickEvents()
        subscribeObservers()
    }

    private fun handleOnClickEvents() {
        binding.updatePasswordButton.setOnClickListener {
            changePassword()
        }
    }

    private fun subscribeObservers() {

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.dataState.collect { dataState ->
                stateChangeListener.dataStateChange(dataState)

                dataState.let {
                    it.stateMessage?.let { stateMessage ->
                        if (stateMessage.message == RESPONSE_PASSWORD_UPDATE_SUCCESS) {
                            stateChangeListener.hideSoftKeyboard()
                            findNavController().popBackStack()
                        }
                    }
                }

            }
        }
    }

    private fun changePassword() {
        viewModel.setStateEvent(
            AccountStateEvent.ChangePasswordEvent(
                binding.inputCurrentPassword.text.toString(),
                binding.inputNewPassword.text.toString(),
                binding.inputConfirmNewPassword.text.toString()
            )
        )
    }
}