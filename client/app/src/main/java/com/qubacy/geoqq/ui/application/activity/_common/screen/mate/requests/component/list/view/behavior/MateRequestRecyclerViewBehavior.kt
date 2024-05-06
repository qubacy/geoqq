package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.view.behavior

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.textview.MaterialTextView
import com.qubacy.geoqq.R
import com.qubacy.utility.baserecyclerview.view.BaseRecyclerView

class MateRequestRecyclerViewBehavior(
    context: Context, attributeSet: AttributeSet
) : CoordinatorLayout.Behavior<BaseRecyclerView>(context, attributeSet) {
    companion object {
        const val TAG = "MateRequestRVBehavior"
    }

    private var mAppBarLayout: AppBarLayout? = null
    private var mHintTextView: MaterialTextView? = null

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: BaseRecyclerView,
        dependency: View
    ): Boolean {
        return when (dependency.id) {
            R.id.component_hint_text -> {
                mHintTextView = dependency as MaterialTextView

                true
            }
            R.id.fragment_mate_requests_top_bar_wrapper -> {
                mAppBarLayout = dependency as AppBarLayout

                true
            }
            else -> false
        }
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: BaseRecyclerView,
        dependency: View
    ): Boolean {
        child.requestLayout()

        return super.onDependentViewChanged(parent, child, dependency)
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: BaseRecyclerView,
        layoutDirection: Int
    ): Boolean {
        val top = getTop()

        child.layout(0, top, parent.right, parent.bottom)

        return true
    }

    private fun getTop(): Int {
        return if (mHintTextView!!.isVisible)
            (mHintTextView!!.top
                + mHintTextView!!.translationY
                + mHintTextView!!.scaleY * mHintTextView!!.measuredHeight
            ).toInt()
        else mAppBarLayout!!.bottom
    }
}