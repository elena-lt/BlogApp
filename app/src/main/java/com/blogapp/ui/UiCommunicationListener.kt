package com.blogapp.ui

import com.blogapp.utils.UIMessage

interface UiCommunicationListener {
    fun onUiMessageReceived(uiMessage: UIMessage)
}