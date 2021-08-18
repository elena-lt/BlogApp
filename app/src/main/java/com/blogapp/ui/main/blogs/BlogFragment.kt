package com.blogapp.ui.main.blogs

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blogapp.R

import com.blogapp.databinding.FragmentBlogBinding
import com.blogapp.models.BlogPost
import com.blogapp.models.mappers.BlogPostMapper
import com.blogapp.recyclerViewUtils.BlogRvAdapter
import com.blogapp.recyclerViewUtils.OnClickListener
import com.blogapp.recyclerViewUtils.TopSpacingItemDecoration
import com.blogapp.ui.main.blogs.state.BlogStateEvent
import com.blogapp.ui.main.blogs.viewModel.*
import com.blogapp.utils.ErrorPaginationDone
import com.domain.utils.DataState
import com.domain.viewState.BlogViewState

class BlogFragment : BaseBlogFragment<FragmentBlogBinding>(), OnClickListener {

    private lateinit var rvAdapter: BlogRvAdapter

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentBlogBinding::inflate

    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)

        handleOnCLickAndRefreshEvents()
        initRecyclerAdapter()
        subscribeToObservers()

        if (savedInstanceState == null) {
            viewModel.loadFirstPage()
        }
    }

    override fun onDestroy() {
//        binding.blogPostRecyclerview.adapter = null
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        initSearchView(menu)
    }

    private fun execute() {
        viewModel.setQuery("")
        viewModel.setStateEvent(BlogStateEvent.BlogSearchEvent)
    }

    private fun handleOnCLickAndRefreshEvents() {
        binding.swipeRefresh.setOnRefreshListener {
            SwipeRefreshLayout.OnRefreshListener {
                Log.d(TAG, "handleOnCLickAndRefreshEvents: refreshing......")
//                binding.swipeRefresh.isRefreshing = false
                onBlogSearchOrFilter()
            }
        }
    }

    private fun subscribeToObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, { dataState ->
            dataState?.let {
                handlePagination(dataState)
                stateChangeListener.dataStateChange(dataState)

            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, { viewState ->
            viewState?.let {
                rvAdapter.apply {
                    val list = viewState.blogFields.blogList.map {
                        BlogPostMapper.toBlogPost(it)
                    }
                    submitList(
                        list,
                        isQueryExhausted = viewState.blogFields.isQueryExhausted
                    )
                    preloadGlideItems(requestManager, list)
                }
            }
        })
    }

    private fun onBlogSearchOrFilter() {
        viewModel.loadFirstPage().let {
            resetUI()
        }
    }

    private fun resetUI() {
        binding.blogPostRecyclerview.smoothScrollToPosition(0)
        stateChangeListener.hideSoftKeyboard()
    }

    private fun handlePagination(dataState: DataState<BlogViewState>) {
        dataState.data?.let {
            it.data?.let {
                it.getContentIfNotHandled()?.let { viewState ->
                    viewModel.handleIncomingBlogListData(viewState)
                }
            }
        }
        //if invalid page -> api returns error mss "Invalid page"
        dataState.error?.let { event ->
            event.peekContent().response.message?.let { msg ->
                if (ErrorPaginationDone.isPaginationDone(msg)) {
                    event.getContentIfNotHandled()

                    viewModel.setQueryExhausted(true)
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
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == rvAdapter.itemCount.minus(1)) {
                        Log.d(TAG, "onScrollStateChanged: attempting to load next page")
                        viewModel.nextPage()
                    }
                }
            })
            adapter = rvAdapter
        }
    }

    private fun initSearchView(menu: Menu) {
        activity?.apply {
            val searchManager: SearchManager = getSystemService(SEARCH_SERVICE) as SearchManager
            searchView = menu.findItem(R.id.action_search).actionView as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.maxWidth = Integer.MAX_VALUE
            searchView.setIconifiedByDefault(true)
            searchView.isSubmitButtonEnabled = true
        }

        //PRESS ARROW ON PHONE KEYBOARD
        val searchPlate = searchView.findViewById<EditText>(R.id.search_src_text)
        searchPlate.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                || actionId == EditorInfo.IME_ACTION_SEARCH
            ) {
                val searchQuery = v.text.toString()
                viewModel.setQuery(searchQuery).let {
                    onBlogSearchOrFilter()
                }
            }
            true
        }

        //SEARCH BTN IN TOOLBAR CLICKED
        searchView.findViewById<View>(R.id.search_go_btn).setOnClickListener {
            val searchQuery = searchPlate.text.toString()
            viewModel.setQuery(searchQuery).let {
                onBlogSearchOrFilter()
            }
        }
    }

    private fun showFilterOptions() {
        activity?.let {
            val dialog = MaterialDialog(it)
                .noAutoDismiss()
                .customView(R.layout.dialog_blog_filter)

            val view = dialog.getCustomView()

            //get selected order type
            view.findViewById<TextView>(R.id.positive_button)?.setOnClickListener {
                val selectedOrder = dialog.getCustomView().findViewById<RadioButton>(
                    dialog.getCustomView()
                        .findViewById<RadioGroup>(R.id.filter_group).checkedRadioButtonId
                )

                var order = ""
                if (selectedOrder.text.toString() == getString(R.string.filter_asc)) order = "-"

                viewModel.setOrder(order)
                onBlogSearchOrFilter()
                dialog.dismiss()
            }

            view.findViewById<TextView>(R.id.negative_button).setOnClickListener {
                dialog.dismiss()

            }
            dialog.show()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter_settings -> {
                showFilterOptions()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemSelected(position: Int, item: BlogPost) {
        viewModel.setBlogPost(BlogPostMapper.toBlogPostDomain(item))
        findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
    }
}