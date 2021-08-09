package com.blogapp.ui.main.blogs

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.blogapp.ui.DataStateChangeListener
import com.blogapp.ui.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseBlogFragment<out T : ViewBinding> : DaggerFragment() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
    lateinit var viewModel: BlogViewModel

    val TAG: String = "AppDebug"

    private var _binding: ViewBinding? = null
    val binding: T
        get() = _binding as T

    lateinit var stateChangeListener: DataStateChangeListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater(inflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.run {
            ViewModelProvider(this, providerFactory).get(BlogViewModel::class.java)
        } ?: throw Exception("Invalid activity")

//        cancelActiveJobs()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            stateChangeListener = context as DataStateChangeListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement DataStateChangeListener")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding?.let {
            _binding = null
        }
    }

    protected abstract val bindingInflater: (LayoutInflater) -> ViewBinding
}