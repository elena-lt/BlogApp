package com.blogapp.ui.main.blogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import com.domain.dataState.DataState
import com.domain.viewState.BlogViewState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@SuppressLint("UnsafeRepeatOnLifecycleDetector")
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
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.dataState.collect { dataState ->
                        Log.d(TAG, "subscribeToObservers: new dataState collected")
//                        stateChangeListener.dataStateChange(dataState)
                        when (dataState) {
                            is DataState.SUCCESS -> {
                                stateChangeListener.dataStateChange(dataState)
                                dataState.data?.let { viewState ->
                                    viewModel.setIsAuthorOfBlogPost(viewState.viewBlogFields.isAuthorOfBlogPost)
//                                    viewModel.setViewState(
//                                        viewModel.currentState.copy(
//                                            viewBlogFields = BlogViewState.ViewBlogFields(
//                                                viewModel.getBlogPost(),
//                                                isAuthorOfBlogPost = viewState.viewBlogFields.isAuthorOfBlogPost
//                                            )
//                                        )
//                                    )
                                }
                            }

                            is DataState.ERROR -> {
                                dataState.stateMessage?.let { stateMessage ->
                                    if (stateMessage.message == SUCCESS_BLOG_DELETED) {
                                        viewModel.removeDeletedBlogPost()
                                        findNavController().popBackStack()
                                    }
                                }
                            }
                        }
                    }
                }

                launch {
                    viewModel.viewState.collect { viewState ->
                        viewState.viewBlogFields?.blogPost?.let { blog ->
                            setBlogProperties(BlogPostMapper.toBlogPost(blog))
                        }

                        if (viewState.viewBlogFields.isAuthorOfBlogPost) {
                            adaptViewToAuthorMode()
                        }
                    }
                }
            }
        }
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
        viewModel.setViewState(
            viewModel.currentState.copy(
                updateBlogFields = BlogViewState.UpdateBlogFields(
                    viewModel.getBlogPost().title,
                    viewModel.getBlogPost().body,
                    viewModel.getBlogPost().image.toUri()
                )
            )
        )

        findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
    }
}