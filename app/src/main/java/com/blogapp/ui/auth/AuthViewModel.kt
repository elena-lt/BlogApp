package com.blogapp.ui.auth

import androidx.lifecycle.ViewModel
import com.domain.usecases.LoginUseCase
import com.domain.usecases.RegisterUseCase
import javax.inject.Inject


class AuthViewModel @Inject constructor(
    private val login: LoginUseCase,
    private val register: RegisterUseCase
) : ViewModel() {
}