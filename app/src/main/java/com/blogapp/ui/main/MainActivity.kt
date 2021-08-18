package com.blogapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.blogapp.R
import com.blogapp.ui.auth.AuthActivity
import com.blogapp.ui.base.BaseActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : BaseActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupNavGraph()
        setupActionBarWithNavController()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.setupWithNavController(navController)

        bottomNavigationView.setOnItemReselectedListener {

        }

        subscribeToObservers()
    }

    private fun setupNavGraph() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController
    }


    private fun subscribeToObservers() {
        sessionManager.cashedToken.observe(this, { authToken ->
            if (authToken == null || authToken.account_primary_key == -1 || authToken.token == null) {
                navToAuthActivity()
            }
        })
    }

    override fun showProgressBar(showPB: Boolean) {
        val progress = findViewById<ProgressBar>(R.id.progress_bar_main)
        if (showPB) progress.visibility = View.VISIBLE else progress.visibility = View.GONE
    }

    private fun setupActionBarWithNavController() {
        val actionbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.tool_bar)
        setSupportActionBar(actionbar)

        val appBarConfiguration = AppBarConfiguration.Builder(
            setOf(
                R.id.blogFragment,
                R.id.createNewBlogFragment,
                R.id.accountFragment
            )
        ).build()
        setupActionBarWithNavController( navController, appBarConfiguration)

    }

    private fun navToAuthActivity(){
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}