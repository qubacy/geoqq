package com.qubacy.geoqq.ui._common.util.view.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun View.catchViewInsets(
    insetsToCatch: Int,
    onInsetsCaught: View.(Insets, WindowInsetsCompat) -> Unit
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insetsRes: WindowInsetsCompat? ->
        if (insetsRes == null)
            return@setOnApplyWindowInsetsListener WindowInsetsCompat.CONSUMED

        val insets = insetsRes.getInsets(insetsToCatch)

        onInsetsCaught(insets, insetsRes)
        insetsRes
    }
}

fun View.runAnimation(
    duration: Long,
    prepareAnimatorAction: (animator: ViewPropertyAnimator) -> Unit,
    prepareViewAction: (() -> Unit)? = null,
    endAction: (() -> Unit)? = null,
    interpolator: Interpolator = AccelerateInterpolator()
) {
    prepareViewAction?.invoke()

    animate().apply {
        prepareAnimatorAction(this@apply)

        this.duration = duration
        this.interpolator = interpolator
    }.setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationCancel(animation: Animator) { endAction?.invoke() }
        override fun onAnimationEnd(animation: Animator) { endAction?.invoke() }
    }).start()
}

fun View.runVisibilityAnimation(
    toVisible: Boolean,
    duration: Long,
    endAction: (() -> Unit)? = null,
    interpolator: Interpolator = AccelerateInterpolator()
) {
    runAnimation(duration, {
        it.alpha(if (toVisible) 1f else 0f)
    }, {
        alpha = if (toVisible) 0f else 1f

        if (toVisible) visibility = View.VISIBLE
    }, {
        if (!toVisible) visibility = View.GONE

       endAction?.invoke()
    }, interpolator)
}