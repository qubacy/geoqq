package com.qubacy.geoqq.ui.application.activity._common

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import androidx.drawerlayout.widget.DrawerLayout
import com.qubacy.geoqq.ui.application.activity._common.component.drawer.MainNavigationDrawer

class HiltTestActivity : MainActivity() {
    private lateinit var mRootView: DrawerLayout

    override val navigationDrawerLayout: DrawerLayout get() = mRootView
    override val navigationDrawer: MainNavigationDrawer? = null

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        basicOnCreate(savedInstanceState)

        mRootView = DrawerLayout(this).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }

        setContentView(mRootView)
    }
}