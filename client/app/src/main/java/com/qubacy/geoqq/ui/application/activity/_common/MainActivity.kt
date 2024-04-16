package com.qubacy.geoqq.ui.application.activity._common

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.core.view.updatePadding
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.ActivityMainBinding
import com.qubacy.geoqq.ui._common.util.view.extension.catchViewInsets
import com.qubacy.geoqq.ui.application.activity._common.component.drawer.MainNavigationDrawer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var mBinding: ActivityMainBinding

    open val topDestinations = setOf(
        R.id.loginFragment,
        R.id.mateChatsFragment,
        R.id.mateRequestsFragment
    )
    open val navigationDrawer: MainNavigationDrawer get() =
        mBinding.activityMainNavigationDrawer
    open val navigationDrawerLayout get() = mBinding.root

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
        setupWindowInsetsListener()
    }
    private fun setupEdgeToEdge() {
        // todo: think about this:
        enableEdgeToEdge(navigationBarStyle = SystemBarStyle.auto(
            Color.TRANSPARENT, Color.TRANSPARENT)) // todo: is it safe?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
    }

    private fun setupWindowInsetsListener() {
        mBinding.root.catchViewInsets(
            WindowInsetsCompat.Type.statusBars() or
                    WindowInsetsCompat.Type.navigationBars()
        ) { insets, _ ->
            mBinding.activityMainNavigationDrawer.updatePadding(top = insets.top)
        }

//        ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { view, insetsRes ->
//            var isConsumed = false
//
//            processInsetsForActivity(insetsRes)
//
//            (view as ViewGroup).forEach { child ->
//                val childResult = ViewCompat.dispatchApplyWindowInsets(child, insetsRes)
//
//                if (childResult.isConsumed) isConsumed = true
//            }
//
//            if (isConsumed) WindowInsetsCompat.CONSUMED else insetsRes
//        }
    }

//    private fun processInsetsForActivity(insetsRes: WindowInsetsCompat) {
//        val insets = insetsRes.getInsets(WindowInsetsCompat.Type.statusBars())
//
//        mBinding.activityMainNavigationDrawer.updatePadding(top = insets.top)
//    }
}