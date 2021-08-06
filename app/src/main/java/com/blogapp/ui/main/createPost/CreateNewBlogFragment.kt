package com.blogapp.ui.main.createPost

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.blogapp.R
import com.blogapp.databinding.FragmentCreateNewBlogBinding

class CreateNewBlogFragment : BaseCreateBlogFragment<FragmentCreateNewBlogBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentCreateNewBlogBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}