package com.blogapp.ui.main.blogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding

import com.blogapp.databinding.FragmentBlogBinding

class BlogFragment : BaseBlogFragment<FragmentBlogBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentBlogBinding::inflate


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}