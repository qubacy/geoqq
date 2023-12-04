package com.qubacy.geoqq.ui.common.visual.common

import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton

@BindingAdapter("menuOptionBackground")
fun setMenuOptionBackground(view: View, drawable: Drawable) {
    (view as AppCompatImageView).apply {
        setImageDrawable(drawable)
    }
}

@BindingAdapter("menuOptionRipple")
fun setMenuOptionRipple(view: View, drawable: Drawable) {
    (view as MaterialButton).apply {
        setBackgroundDrawable(drawable)
    }
}