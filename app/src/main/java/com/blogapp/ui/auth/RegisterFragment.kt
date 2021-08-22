package com.blogapp.ui.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.blogapp.databinding.FragmentRegisterBinding
import com.blogapp.ui.auth.state.AuthStateEvent
import com.blogapp.ui.base.BaseAuthFragment
import com.domain.viewState.RegistrationFields
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@SuppressLint("UnsafeRepeatOnLifecycleDetector")
class RegisterFragment : BaseAuthFragment<FragmentRegisterBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentRegisterBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleClickEvents()
        subscribeToObservers()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setRegistrationFields(
            RegistrationFields(
                binding.inputEmail.text.toString(),
                binding.inputUsername.text.toString()
            )
        )
    }

    private fun handleClickEvents() {
        binding.registerButton.setOnClickListener { register() }
    }

    private fun register() {
        viewModel.setStateEvent(
            AuthStateEvent.RegisterEvent(
                binding.inputEmail.text.toString(),
                binding.inputUsername.text.toString(),
                binding.inputPassword.text.toString(),
                binding.inputPasswordConfirm.text.toString()
            )
        )
    }

    private fun subscribeToObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.viewState.collect {
                        it.registrationFields?.let { registrationFields ->
                            registrationFields.registrationEmail?.let { email ->
                                binding.inputEmail.setText(email)
                            }
                            registrationFields.registrationUsername?.let { username ->
                                binding.inputUsername.setText(username)
                            }
                        }
                    }
                }
            }
        }
    }
}