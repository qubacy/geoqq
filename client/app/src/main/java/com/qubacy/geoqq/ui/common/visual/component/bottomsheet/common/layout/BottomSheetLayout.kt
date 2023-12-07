package com.qubacy.geoqq.ui.common.visual.component.bottomsheet.common.layout

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.qubacy.geoqq.R

class BottomSheetLayout(context: Context, attributeSet: AttributeSet)
    : CoordinatorLayout(context, attributeSet)
{
    companion object {
        const val TAG = "BOTTOM_SHEET_LAYOUT"
    }

    private lateinit var scrimView: View
    private lateinit var bottomSheetView: View

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        Log.d(TAG, "onLayout(): left = $l, top = $top, right = $right, b = $bottom")
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        scrimView = findViewById(R.id.bottom_sheet_scrim)
        bottomSheetView = findViewById(R.id.bottom_sheet)

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) { }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset <= 0) return

                scrimView.alpha = slideOffset
            }
        })
    }
}