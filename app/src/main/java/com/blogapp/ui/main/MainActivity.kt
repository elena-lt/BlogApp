package com.blogapp.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.blogapp.R
import com.blogapp.ui.BaseActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        subscribeToObservers()

        findViewById<Button>(R.id.logout_button).setOnClickListener {
            sessionManager.logout()
        }
    }

    fun subscribeToObservers(){
        sessionManager.cashedToken.observe(this, {authToken ->
            Log.d ("MAIN_ACTIVITY", "authToken is $authToken")
            if (authToken == null || authToken.account_primary_key ==-1 || authToken.token == null){
                Log.d ("MAIN_ACTIVITY", "Navigating to auth activity")
            }
        })
    }

    override fun showProgressBar(showPB: Boolean) {
        TODO("Not yet implemented")
    }
}