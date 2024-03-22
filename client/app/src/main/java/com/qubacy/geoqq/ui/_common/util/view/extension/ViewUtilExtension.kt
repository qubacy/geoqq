package com.qubacy.geoqq.ui._common.util.view.extension

import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun View.catchViewInsets(
    insetsToCatch: Int,
    onInsetsCaught: View.(Insets) -> Unit
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insetsRes: WindowInsetsCompat? ->
        val insets = insetsRes?.getInsets(insetsToCatch)

        if (insets != null) onInsetsCaught(insets)

        insetsRes!!
    }
}