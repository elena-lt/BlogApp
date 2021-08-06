package com.blogapp.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import com.blogapp.databinding.FragmentRegisterBinding
import com.blogapp.ui.auth.state.AuthStateEvent
import com.domain.viewState.RegistrationFields

class RegisterFragment : BaseAuthFragment<FragmentRegisterBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentRegisterBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerButton.setOnClickListener { register() }

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
        viewModel.viewState.observe(viewLifecycleOwner, {
            it.registrationFields?.let { regFields ->
                regFields.registrationEmail?.let { email ->
                    binding.inputEmail.setText(email)
                }
                regFields.registrationUsername?.let { username ->
                    binding.inputUsername.setText(username)
                }
            }
        })
    }
}