package com.blogapp.ui.main.account

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.blogapp.ui.DataStateChangeListener
import dagger.android.support.DaggerFragment

abstract class BaseAccountFragment <out T: ViewBinding>: DaggerFragment() {
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
        return binding.root
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            stateChangeListener = context as DataStateChangeListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement DataStateChangeListener" )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding?.let {
            _binding=null
        }
    }
    
    protected abstract val bindingInflater: (LayoutInflater) -> ViewBinding
}