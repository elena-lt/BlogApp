package com.blogapp.ui.base

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.blogapp.ui.*
import com.blogapp.utils.Const.PERMISSIONS_REQUEST_READ_STORAGE
import com.blogapp.utils.UIMessage
import com.blogapp.utils.UIMessageType
import com.data.session.SessionManager
import com.domain.dataState.DataState
import com.domain.dataState.StateMessage
import com.domain.dataState.UIComponentType
import com.domain.utils.*
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(), DataStateChangeListener,
    UiCommunicationListener {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun dataStateChange(dataState: DataState<*>) {
        dataState?.let {
            GlobalScope.launch(Dispatchers.Main) {
                showProgressBar(it.loading)

                it.stateMessage?.let {
                    handleResponseState(it)
                }
            }
        }
    }

    private fun handleResponseState(stateMessage: StateMessage) {
        stateMessage.message?.let {
            when (stateMessage.uiComponentType) {
                is UIComponentType.DIALOG -> displayErrorDialog(it)
                is UIComponentType.TOAST -> displayToast(it)
                else -> {
                }
            }
        }
    }

    override fun onUiMessageReceived(uiMessage: UIMessage) {
        when (uiMessage.uiMessageType) {
            is UIMessageType.AreYouSureDialog -> {
                areYouSureDialog(uiMessage.message, uiMessage.uiMessageType.callback)
            }
            is UIMessageType.Toast -> displayToast(uiMessage.message)
            is UIMessageType.Dialog -> displayInfoDialog(uiMessage.message)
            is UIMessageType.None -> Log.i("AppDebug", "onUiMessageReceived: ${uiMessage.message}")
        }
    }

    abstract fun showProgressBar(showPB: Boolean)

    override fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    override fun isStoragePermissionGranted(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSIONS_REQUEST_READ_STORAGE
            )
            return false
        } else return true
    }
}