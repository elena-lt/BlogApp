package com.blogapp.ui.main.blogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.blogapp.R

import com.blogapp.databinding.FragmentBlogBinding
import com.blogapp.models.BlogPost
import com.blogapp.models.mappers.BlogPostMapper
import com.blogapp.recyclerViewUtils.BlogRvAdapter
import com.blogapp.recyclerViewUtils.OnClickListener
import com.blogapp.recyclerViewUtils.PagingLoadStateAdapter
import com.blogapp.recyclerViewUtils.TopSpacingItemDecoration
import com.blogapp.ui.main.blogs.state.BlogStateEvent
import com.bumptech.glide.RequestManager
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@InternalCoroutinesApi
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
        viewModel.setEvent(BlogStateEvent.BlogSearchEvent)
    }

    private fun handleOnCLickEvents() {
        binding.retryButton.setOnClickListener { rvAdapter.retry() }
    }

    private fun subscribeToObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.state.collect {
                it.blogFields.blogPosts?.let{pagingData ->
                    val blogs = pagingData.map{blog ->
                        BlogPostMapper.toBlogPost(blog)
                    }
                    rvAdapter.submitData(blogs)

                }
            }
        }
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
            rvAdapter.withLoadStateHeaderAndFooter(
                header = PagingLoadStateAdapter { rvAdapter.retry() },
                footer = PagingLoadStateAdapter { rvAdapter.retry() }
            )

            rvAdapter.addLoadStateListener { loadState ->

                // Show loading spinner during initial load or refresh.
                binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading

                // Show the retry state if initial load or refresh fails.
                binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error

                // Only show the list if refresh succeeds.
                binding.blogPostRecyclerview.isVisible =
                    loadState.source.refresh is LoadState.NotLoading

                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        "\uD83D\uDE28 Whoops ${it.error}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                adapter = rvAdapter
            }
        }
    }

    override fun onItemSelected(position: Int, item: BlogPost) {
//        viewModel.setBlogPost(item)
        findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
    }

}