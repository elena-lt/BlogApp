package com.blogapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.blogapp.R
import com.blogapp.ui.ViewModelProviderFactory
import com.blogapp.ui.auth.state.AuthStateEvent
import com.blogapp.ui.base.BaseActivity
import com.blogapp.ui.main.MainActivity
import com.data.models.AuthToken
import com.domain.dataState.DataState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.dataState.collect { dataState ->
                        dataStateChange(dataState)
                        when (dataState) {
                            is DataState.SUCCESS -> {
                                dataState.data?.let { viewState ->
                                    Log.d(
                                        "AppDebug",
                                        "subscribeToObservers //view state is: ${viewState.authToken?.authToken} "
                                    )
                                    viewState.authToken?.let { token ->
                                        Log.d(
                                            "AppDebug",
                                            "subscribeToObservers // logging in with session manager "
                                        )
                                        sessionManager.login(AuthToken(token.pk, token.authToken))
                                    }
                                }
                            }
                            else -> dataStateChange(dataState)
                        }
                    }
                }
            }
        }

        sessionManager.cashedToken.observe(this, { authToken ->
            Log.d("AUTH_ACTIVITY", "authToken is $authToken")
            if (authToken != null && authToken.account_primary_key != -1 && authToken.token != null) {
                Log.d("AppDebug", "subscribeToObservers: navigating to main activity")
                navToMainActivity()
            }
        })
    }

    private fun checkPrevAuthUser() {
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