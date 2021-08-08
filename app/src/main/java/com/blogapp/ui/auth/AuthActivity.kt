package com.blogapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.blogapp.R
import com.blogapp.ui.base.BaseActivity
import com.blogapp.ui.ViewModelProviderFactory
import com.blogapp.ui.auth.state.AuthStateEvent
import com.blogapp.ui.main.MainActivity
import com.data.models.AuthToken
import javax.inject.Inject

class AuthActivity : BaseActivity() {

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
    lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        setupNavigation()

        viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)

        checkPrevAuthUser()
        subscribeToObservers()

    }

    private fun setupNavigation() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun subscribeToObservers() {

        viewModel.dataState.observe(this, { dataState ->
            dataState.data?.let { data ->
                data.data?.let { event ->
                    event.getContentIfNotHandled()?.let {
                        it.authToken?.let { token ->
                            Log.d("AUTH_ACTIVITY", "DataState authToken: $token")
                            viewModel.setAuthToken(token)
                        }
                    }
                }

//                data.response?.let { event ->
//                    event.getContentIfNotHandled()?.let {
//                        when (it.responseType) {
//                            is ResponseType.Dialog -> {
//                                Log.e("AUTH_ACTIVITY", "Response: ${it.message}")
//                            }
//                            is ResponseType.Toast -> {
//                                Log.e("AUTH_ACTIVITY", "Response: ${it.message}")
//                            }
//                            is ResponseType.None -> {
//                                Log.e("AUTH_ACTIVITY", "Response: ${it.message}")
//                            }
//                        }
//                    }
//
//                }
            }
        })

        viewModel.viewState.observe(this, {
            it.authToken?.let { authToken ->
                val tokeData = AuthToken(authToken.pk, authToken.authToken)
                sessionManager.login(tokeData)
            }
        })

        sessionManager.cashedToken.observe(this, { authToken ->
            Log.d("AUTH_ACTIVITY", "authToken is $authToken")
            if (authToken != null && authToken.account_primary_key != -1 && authToken.token != null) {
                Log.d("AppDebug", "subscribeToObservers: navigation to main activity")
                navToMainActivity()
            }
        })
    }

    fun checkPrevAuthUser() {
        viewModel.setStateEvent(AuthStateEvent.CheckPreviousAuthEvent)
    }

    private fun navToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun showProgressBar(showPB: Boolean) {
        if (showPB) {
            findViewById<ProgressBar>(R.id.progress_bar).visibility = View.VISIBLE
        } else {
            findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE
        }
    }
}