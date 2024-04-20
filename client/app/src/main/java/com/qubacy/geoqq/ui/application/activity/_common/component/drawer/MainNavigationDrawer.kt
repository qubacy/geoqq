package com.qubacy.geoqq.ui.application.activity._common.component.drawer

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.StringRes
import com.google.android.material.navigation.NavigationView
import com.qubacy.geoqq.databinding.ComponentMainNavigationDrawerHeaderBinding

class MainNavigationDrawer(
    context: Context,
    attrs: AttributeSet? = null
) : NavigationView(context, attrs) {
    private lateinit var mHeaderView: ComponentMainNavigationDrawerHeaderBinding

    override fun onFinishInflate() {
        super.onFinishInflate()

        val headerView = getHeaderView(0)

        mHeaderView = ComponentMainNavigationDrawerHeaderBinding.bind(headerView)
    }

    fun setHeaderTitle(title: String) {
        mHeaderView.componentMainNavigationDrawerHeaderTitle.text = title
    }

    fun setHeaderTitle(@StringRes titleResId: Int) {
        val title = context.getString(titleResId)

        setHeaderTitle(title)
    }
}