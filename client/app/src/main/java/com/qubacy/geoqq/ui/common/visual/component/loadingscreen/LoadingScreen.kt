package com.qubacy.geoqq.ui.common.visual.component.loadingscreen

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout

class LoadingScreen(
    context: Context,
    attributeSet: AttributeSet
) : ConstraintLayout(context, attributeSet) {
    companion object {
        const val TAG = " LOADING_SCREEN"
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        Log.d(TAG, "onMeasure(): measuredHeight = $measuredHeight, measuredWidth = $measuredWidth")
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        Log.d(TAG, "onLayout(): left = $left, top = $top, right = $right, bottom = $bottom")
    }
}