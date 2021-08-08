package com.blogapp.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.blogapp.ui.ViewModelProviderFactory
import com.blogapp.ui.auth.AuthViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseAuthFragment <out T: ViewBinding> : DaggerFragment(){

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: AuthViewModel

    private var _binding: ViewBinding? = null

    protected val binding: T
        get() = _binding as T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater(inflater)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.let{
            ViewModelProvider(it, providerFactory).get(AuthViewModel::class.java)
        }?: throw  Exception("Invalid Activity")

//        cancelActiveJobs()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding?.let {
            _binding=null
        }
    }

    private fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }

    protected abstract val bindingInflater: (LayoutInflater) -> ViewBinding
}