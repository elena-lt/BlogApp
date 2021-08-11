package com.blogapp.ui.main.blogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.blogapp.R
import com.blogapp.databinding.FragmentViewBlogBinding
import com.blogapp.models.BlogPost
import com.blogapp.models.mappers.BlogPostMapper


class ViewBlogFragment : BaseBlogFragment<FragmentViewBlogBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentViewBlogBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()
    }

    private fun setBlogProperties(blogPost: BlogPost) {
        requestManager.load(blogPost.image)
            .into(binding.blogImage)

        binding.blogTitle.text = blogPost.title
        binding.blogAuthor.text = blogPost.username
        binding.blogBody.text = blogPost.body
        binding.blogUpdateDate.text = blogPost.date_updated
    }

    private fun subscribeToObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, { dataState ->
            dataState?.let {
                stateChangeListener.dataStateChange(it)
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, { viewState ->
            viewState.viewBlogFields.blogPost?.let { blog ->
                setBlogProperties(BlogPostMapper.toBlogPost(blog))
            }
        })
    }
}