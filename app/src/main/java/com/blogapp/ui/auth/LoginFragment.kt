package com.blogapp.ui.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.blogapp.R
import com.blogapp.databinding.FragmentLoginBinding
import com.blogapp.ui.auth.state.AuthStateEvent
import com.blogapp.ui.base.BaseAuthFragment
import com.domain.viewState.LoginFields
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@SuppressLint("UnsafeRepeatOnLifecycleDetector")
class LoginFragment : BaseAuthFragment<FragmentLoginBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentLoginBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleBtnClickListeners()
        subscribeToObservers()
    }

    override fun onDestroy() {
        viewModel.setLoginFields(
            LoginFields(
                binding.inputEmail.text.toString(),
                binding.inputPassword.text.toString()
            )
        )
        super.onDestroy()
    }

    private fun handleBtnClickListeners() {
        binding.loginButton.setOnClickListener {
            login()
        }

        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
    }

    private fun login() {
        viewModel.setStateEvent(
            AuthStateEvent.LoginEvent(
                binding.inputEmail.text.toString(),
                binding.inputPassword.text.toString()
            )
        )
    }

    private fun subscribeToObservers() {

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.viewState.collect {
                    it.LoginFields?.let { loginFields ->
                        loginFields.login_email?.let { email ->
                            binding.inputEmail.setText(email)
                        }
                    }
                }
            }
        }
    }

}