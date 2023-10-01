package com.qubacy.geoqq.ui.common.bottomsheet

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.qubacy.geoqq.R

class BottomSheetLayout(context: Context, attributeSet: AttributeSet)
    : CoordinatorLayout(context, attributeSet)
{
    private lateinit var scrimView: View
    private lateinit var bottomSheetView: View

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        scrimView = findViewById(R.id.bottom_sheet_scrim)
        bottomSheetView = findViewById(R.id.user_profile_bottom_sheet)

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