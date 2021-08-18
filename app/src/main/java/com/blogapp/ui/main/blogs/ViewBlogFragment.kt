package com.blogapp.ui.main.blogs

import android.os.Bundle
import android.view.*
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.blogapp.R
import com.blogapp.databinding.FragmentViewBlogBinding
import com.blogapp.models.BlogPost
import com.blogapp.models.mappers.BlogPostMapper
import com.blogapp.ui.AreYouSureCallback
import com.blogapp.ui.main.blogs.state.BlogStateEvent
import com.blogapp.ui.main.blogs.viewModel.*
import com.blogapp.utils.Const.SUCCESS_BLOG_DELETED
import com.blogapp.utils.UIMessage
import com.blogapp.utils.UIMessageType


class ViewBlogFragment : BaseBlogFragment<FragmentViewBlogBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentViewBlogBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        handleClickEvents()
        checkAuthorOfBlogPost()
        subscribeToObservers()
    }

    private fun handleClickEvents() {
        binding.deleteButton.setOnClickListener {
            confirmDeleteRequest()
        }
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
                dataState.data?.let { data ->
                    data.data?.getContentIfNotHandled()?.let { viewState ->
                        viewModel.setIsAuthorOfBlogPost(viewState.viewBlogFields.isAuthorOfBlogPost)
                    }

                    data.response?.peekContent()?.let { response ->
                        if (response.message == SUCCESS_BLOG_DELETED) {
                            viewModel.removeDeletedBlogPost()
                            findNavController().popBackStack()
                        }
                    }
                }

            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, { viewState ->
            viewState.viewBlogFields.blogPost?.let { blog ->
                setBlogProperties(BlogPostMapper.toBlogPost(blog))
            }

            if (viewState.viewBlogFields.isAuthorOfBlogPost) {
                adaptViewToAuthorMode()
            }
        })
    }

    private fun checkAuthorOfBlogPost() {
        viewModel.setStateEvent(BlogStateEvent.CheckAuthorOfTheBlogPost)
    }

    private fun adaptViewToAuthorMode() {
        activity?.invalidateOptionsMenu()
        binding.deleteButton.visibility = View.VISIBLE
        binding.deleteButton.isEnabled = true
    }

    private fun deleteBlogPost() {
        viewModel.setStateEvent(BlogStateEvent.DeleteBlogStateEvent)
    }

    private fun confirmDeleteRequest() {
        val callback: AreYouSureCallback = object : AreYouSureCallback {
            override fun proceed() {
                deleteBlogPost()
            }

            override fun cancel() {
                /*no-ops*/
            }
        }

        uiCommunicationListener.onUiMessageReceived(
            UIMessage(
                getString(R.string.are_you_sure_delete),
                UIMessageType.AreYouSureDialog(callback)
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (viewModel.isAuthorOfBlogPost()) {
            inflater.inflate(R.menu.edit_blogpost_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (viewModel.isAuthorOfBlogPost()) {
            when (item.itemId) {
                R.id.edit -> {
                    navigateToUpdateBlogFragment()
                    return true
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun navigateToUpdateBlogFragment() {
        viewModel.setUpdatedBlogFields(
            viewModel.getBlogPost().title,
            viewModel.getBlogPost().body,
            viewModel.getBlogPost().image.toUri(),
            )
        findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
    }
}