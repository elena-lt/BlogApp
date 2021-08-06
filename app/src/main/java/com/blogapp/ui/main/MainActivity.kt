package com.blogapp.ui.main

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphNavigator
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.blogapp.R
import com.blogapp.ui.base.BaseActivity
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : BaseActivity() {

    private lateinit var navController: NavController
//    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupNavGraph()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.setupWithNavController(navController)

        bottomNavigationView.setOnItemReselectedListener {
            navController.currentDestination.

        }

        subscribeToObservers()
//
//        findViewById<Button>(R.id.logout_button).setOnClickListener {
//            sessionManager.logout()
//        }
    }

    private fun setupNavGraph() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController
    }


    private fun subscribeToObservers() {
        sessionManager.cashedToken.observe(this, { authToken ->
            Log.d("MAIN_ACTIVITY", "authToken is $authToken")
            if (authToken == null || authToken.account_primary_key == -1 || authToken.token == null) {
                Log.d("MAIN_ACTIVITY", "Navigating to auth activity")
            }
        })
    }

    override fun showProgressBar(showPB: Boolean) {
        TODO("Not yet implemented")
    }
}