package com.blogapp.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.TranslateAnimation
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.blogapp.databinding.FragmentForgotPasswordBinding
import com.blogapp.ui.DataStateChangeListener
import com.blogapp.utils.Const.PASSWORD_RESET_URL
import com.domain.utils.DataState
import com.domain.utils.Response
import com.domain.utils.ResponseType
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ClassCastException

class ForgotPasswordFragment : BaseAuthFragment<FragmentForgotPasswordBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentForgotPasswordBinding::inflate

    lateinit var webView: WebView
    lateinit var stateChangeListener: DataStateChangeListener

    val webInteractionCallback = object : WebAppInterface.OnWebInteractionCallback {
        override fun onSuccess(email: String) {
            Log.d("AppDebug", "onSuccess: resent link will be sent to email")
            onPasswordResentLinkSent()
        }

        override fun onError(errorMsg: String) {
            val dataState =
                DataState.error<Any>(response = Response(errorMsg, ResponseType.Dialog()))
            stateChangeListener.dataStateChange(dataState)
        }

        override fun onLoading(isLoading: Boolean) {
            //web interactions may do some work on bg thread, therefore we need to make sure
            // progBar is changing on Main thread
            GlobalScope.launch(Main) {
                stateChangeListener.dataStateChange(
                    DataState.loading(
                        isLoading = isLoading,
                        cashedData = null
                    )
                )
            }
        }
    }

    private fun onPasswordResentLinkSent() {
        GlobalScope.launch(Main) {
            binding.parentView.removeView(webView)
            webView.destroy()

            val animation = TranslateAnimation(
                binding.passwordResetDoneContainer.width.toFloat(),
                0f,
                0f,
                0f,
            )
            animation.duration = 500
            binding.passwordResetDoneContainer.startAnimation(animation)
            binding.passwordResetDoneContainer.visibility  =View.VISIBLE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = binding.webview
        loadPasswordResetWebView()

        binding.returnToLauncherFragment.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            stateChangeListener = context as DataStateChangeListener
        } catch (e: ClassCastException) {
            Log.e("AppDebug", "$context must implement DataStateChangeListener")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun loadPasswordResetWebView() {
        stateChangeListener.dataStateChange(
            DataState.loading(
                isLoading = true,
                cashedData = null
            )
        )

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                stateChangeListener.dataStateChange(
                    DataState.loading(
                        isLoading = false,
                        cashedData = null
                    )
                )
            }
        }

        webView.loadUrl(PASSWORD_RESET_URL)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(
            WebAppInterface(webInteractionCallback),
            "AndroidTextListener"
        )

    }

    class WebAppInterface constructor(
        private val callback: OnWebInteractionCallback
    ) {

        @JavascriptInterface
        fun onSuccess(email: String) {
            callback.onSuccess(email)
        }

        @JavascriptInterface
        fun onError(errorMgs: String) {
            callback.onError(errorMgs)
        }

        @JavascriptInterface
        fun onLoading(isLoading: Boolean) {
            callback.onLoading(isLoading)
        }

        interface OnWebInteractionCallback {
            fun onSuccess(email: String)
            fun onError(errorMsg: String)
            fun onLoading(isLoading: Boolean)
        }
    }
}