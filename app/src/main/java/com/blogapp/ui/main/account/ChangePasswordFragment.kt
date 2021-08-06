package com.blogapp.ui.main.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.blogapp.R
import com.blogapp.databinding.FragmentChangePasswordBinding

class ChangePasswordFragment : BaseAccountFragment<FragmentChangePasswordBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentChangePasswordBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}