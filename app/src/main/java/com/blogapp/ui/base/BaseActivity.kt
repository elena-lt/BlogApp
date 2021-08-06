package com.blogapp.ui.base

import android.util.Log
import com.blogapp.ui.DataStateChangeListener
import com.blogapp.ui.displayErrorDialog
import com.blogapp.ui.displayToast
import com.data.session.SessionManager
import com.domain.utils.*
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(), DataStateChangeListener {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun dataStateChange(dataState: DataState<*>) {
        dataState?.let{
            GlobalScope.launch (Dispatchers.Main) {
                showProgressBar(it.loading.isLoading)

                it.error?.let {
                    handleErrorState(it)
                }

                it.data?.let {
                    it.response?.let { responseEvent ->
                        handleResponseState(responseEvent)
                    }
                }
            }
        }
    }

    private fun handleResponseState(responseEvent: Event<Response>) {
        responseEvent.getContentIfNotHandled()?.let {
            when (it.responseType) {
                is ResponseType.Dialog -> {
                    it.message?.let { msg ->
                        displayErrorDialog(msg)
                    }
                }
                is ResponseType.Toast -> {
                    it.message?.let { msg ->
                        displayToast(msg)
                    }
                }
                is ResponseType.None -> {
                    Log.i("AppDebug", "handleErrorState: ${it.message}")
                }
            }
        }
    }

    private fun handleErrorState(errorEvent: Event<StateError>) {
        errorEvent.getContentIfNotHandled()?.let {
            when (it.response.responseType) {
                is ResponseType.Dialog -> {
                    it.response.message?.let { msg ->
                        displayErrorDialog(msg)
                    }
                }
                is ResponseType.Toast -> {
                    it.response.message?.let { msg ->
                        displayToast(msg)

                    }
                }
                is ResponseType.None -> {
                    Log.i("AppDebug", "handleErrorState: ${it.response.message}")
                }
            }
        }
    }

    abstract fun showProgressBar(showPB: Boolean)
}