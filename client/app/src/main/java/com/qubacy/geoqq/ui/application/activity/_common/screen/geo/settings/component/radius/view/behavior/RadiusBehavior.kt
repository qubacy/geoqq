package com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.component.radius.view.behavior

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginLeft
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.qubacy.geoqq.R

class RadiusBehavior(
    context: Context, attributeSet: AttributeSet
) : CoordinatorLayout.Behavior<View>(context, attributeSet) {
    companion object {
        const val TAG = "RadiusBehavior"
    }

    private var mFAB: FloatingActionButton? = null

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        Log.d(TAG, "layoutDependsOn(): entered..")

        return when (dependency.id) {
            R.id.fragment_geo_settings_button_go -> {
                mFAB = dependency as FloatingActionButton

                true
            }
            else -> false
        }
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        Log.d(TAG, "onDependentViewChanged(): entered..")

        child.requestLayout()

        return super.onDependentViewChanged(parent, child, dependency)
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {
        if (mFAB == null) return false

        val fabVerticalCenter = mFAB!!.top + mFAB!!.measuredHeight / 2

        val left = child.marginLeft
        val top = fabVerticalCenter - child.measuredHeight / 2
        val right = left + child.measuredWidth
        val bottom = top + child.measuredHeight

        Log.d(TAG, "onLayoutChild(): left = $left; top = $top; right = $right; bottom = $bottom;")

        child.layout(left, top, right, bottom)

        return true
    }
}