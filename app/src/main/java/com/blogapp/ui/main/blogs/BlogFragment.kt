package com.blogapp.ui.main.blogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.blogapp.R

import com.blogapp.databinding.FragmentBlogBinding
import com.blogapp.models.mappers.BlogPostMapper
import com.blogapp.ui.main.blogs.state.BlogStateEvent

class BlogFragment : BaseBlogFragment<FragmentBlogBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentBlogBinding::inflate


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        execute()
        handleOnCLickEvents()
        subscribeToObservers()

    }
    private fun execute(){
        viewModel.setQuery("")
        viewModel.setStateEvent(BlogStateEvent.BlogSearchEvent)
    }

    private fun handleOnCLickEvents(){
        binding.goViewBlogFragment.setOnClickListener {
            findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
        }
    }

    private fun subscribeToObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, { dataState ->
            dataState?.let {
                stateChangeListener.dataStateChange(dataState)
                dataState.data?.let {
                    it.data?.let { event ->
                        event.getContentIfNotHandled()?.let {
                            Log.d(TAG, "subscribeToObservers: dataState: ${it}")
                            viewModel.setBlogList(it.blogFields.blogList.map { blogPost ->
                                BlogPostMapper.toBlogPost(blogPost)
                            })
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, { viewState ->
            Log.d(TAG, "subscribeToObservers: ViewState: ${viewState}")
        })
    }

}