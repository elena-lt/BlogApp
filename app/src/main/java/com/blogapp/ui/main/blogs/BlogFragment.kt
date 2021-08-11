package com.blogapp.ui.main.blogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.blogapp.R

import com.blogapp.databinding.FragmentBlogBinding
import com.blogapp.models.BlogPost
import com.blogapp.models.mappers.BlogPostMapper
import com.blogapp.recyclerViewUtils.BlogRvAdapter
import com.blogapp.recyclerViewUtils.OnClickListener
import com.blogapp.recyclerViewUtils.TopSpacingItemDecoration
import com.blogapp.ui.main.blogs.state.BlogStateEvent
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class BlogFragment : BaseBlogFragment<FragmentBlogBinding>(), OnClickListener {

    private lateinit var rvAdapter: BlogRvAdapter

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentBlogBinding::inflate


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        execute()
        handleOnCLickEvents()
        initRecyclerAdapter()
        subscribeToObservers()

    }

    private fun execute() {
        viewModel.setQuery("")
        viewModel.setStateEvent(BlogStateEvent.BlogSearchEvent)
    }

    private fun handleOnCLickEvents() {

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
            viewState?.let {
                rvAdapter.submitList(
                    list = viewState.blogFields.blogList.map {
                        BlogPostMapper.toBlogPost(it)
                    },
                    isQueryExhausted = true
                )
            }
        })
    }

    private fun initRecyclerAdapter() {
        binding.blogPostRecyclerview.apply {
            layoutManager = LinearLayoutManager(this@BlogFragment.context)
            val topSpacingItemDecoration = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingItemDecoration)
            rvAdapter = BlogRvAdapter(
                onClickListener = this@BlogFragment,
                requestManager = requestManager
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == rvAdapter.itemCount.minus(1)) {
                        Log.d(TAG, "onScrollStateChanged: attempting to load next page")
                    }
                }
            })
            adapter = rvAdapter
        }
    }

    override fun onItemSelected(position: Int, item: BlogPost) {
        viewModel.setBlogPost(item)
        findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
    }

}