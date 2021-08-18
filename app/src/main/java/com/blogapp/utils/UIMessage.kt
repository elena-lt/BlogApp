package com.blogapp.utils

import com.blogapp.ui.AreYouSureCallback

data class UIMessage(
    val message: String,
    val uiMessageType: UIMessageType
)

sealed class UIMessageType {
    object Toast : UIMessageType()
    object Dialog : UIMessageType()
    data class AreYouSureDialog(
        val callback: AreYouSureCallback
    ) : UIMessageType()

    object None : UIMessageType()
}