package com.blogapp.ui.main.blogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.blogapp.R
import com.blogapp.databinding.FragmentUpdateBlogBinding

class UpdateBlogFragment : BaseBlogFragment<FragmentUpdateBlogBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentUpdateBlogBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}