package com.qubacy.geoqq.ui.application.activity._common

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.qubacy.geoqq.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setOnExitAnimationListener {
                it.remove()

                setupEdgeToEdge()
            }
        }

        setContentView(R.layout.activity_main)
    }

    private fun setupEdgeToEdge() {
        // todo: think about this:
        enableEdgeToEdge(navigationBarStyle = SystemBarStyle.auto(
            Color.TRANSPARENT, Color.TRANSPARENT)) // todo: is it safe?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
    }
}