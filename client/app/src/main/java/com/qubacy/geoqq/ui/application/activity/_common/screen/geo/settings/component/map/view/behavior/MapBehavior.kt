package com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.component.map.view.behavior

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import com.google.android.material.appbar.AppBarLayout
import com.qubacy.geoqq.R
import com.yandex.mapkit.mapview.MapView

class MapBehavior(
    context: Context, attributeSet: AttributeSet
) : CoordinatorLayout.Behavior<MapView>(context, attributeSet) {
    companion object {
        const val TAG = "MapBehavior"
    }

    private var mAppBarLayout: AppBarLayout? = null
    private var mHintView: View? = null

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: MapView,
        dependency: View
    ): Boolean {
        return when (dependency.id) {
            R.id.fragment_geo_settings_top_bar_wrapper -> {
                mAppBarLayout = dependency as AppBarLayout

                true
            }
            R.id.component_hint_text -> {
                mHintView = dependency

                true
            }
            else -> false
        }
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: MapView,
        dependency: View
    ): Boolean {
        child.requestLayout()

        return super.onDependentViewChanged(parent, child, dependency)
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: MapView,
        layoutDirection: Int
    ): Boolean {
        val top = getTop()

        child.layout(0, top, parent.right, parent.bottom)

        return true
    }

    private fun getTop(): Int {
        return if (mHintView!!.isVisible)
            (mHintView!!.top
            + mHintView!!.translationY
            + mHintView!!.scaleY * mHintView!!.measuredHeight
            ).toInt()
        else mAppBarLayout!!.bottom
    }
}