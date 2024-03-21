package com.qubacy.geoqq.ui.application.activity._common

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setOnExitAnimationListener {
                it.remove()

                setupEdgeToEdge()
            }
        }

        mBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(mBinding.root)
        setupNavigationDrawer()
    }

    private fun setupEdgeToEdge() {
        // todo: think about this:
        enableEdgeToEdge(navigationBarStyle = SystemBarStyle.auto(
            Color.TRANSPARENT, Color.TRANSPARENT)) // todo: is it safe?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
    }

    private fun setupNavigationDrawer() {
        val navHostFragment = mBinding.activityMainFragmentContainer
            .getFragment<NavHostFragment>()
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph, mBinding.root)

        mBinding.activityMainNavigationDrawer.setupWithNavController(navController)
    }
}