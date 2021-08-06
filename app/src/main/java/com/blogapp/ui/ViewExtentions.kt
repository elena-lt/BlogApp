package com.blogapp.ui

import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import com.blogapp.R


fun Context.displayToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.displayErrorDialog(message: String) {
    AlertDialog.Builder(this).apply {
        setTitle(R.string.text_error)
        create()
    }
}

fun Context.displaySuccessDialog(message: String) {
    //TODO()
}