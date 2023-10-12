package com.qubacy.geoqq.ui.screen.geochat.chat.animator

import android.animation.Animator
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class ChatMessageAnimator(
    private val mCallback: ChatMessageAnimatorCallback
) : DefaultItemAnimator() {
    companion object {
        const val TAG = "ChatMessageAnimator"

        const val DEFAULT_ALPHA_VALUE = 0f
        const val DEFAULT_TRANSLATION_X_VALUE = -100f

        const val ENDING_ALPHA_VALUE = 1f
        const val ENDING_TRANSLATION_X_VALUE = 0f

        const val DURATION = 500L
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
        if (holder == null) return false

        if (!mCallback.wasViewHolderAnimated(holder)) {
            holder.itemView.apply {
                alpha = DEFAULT_ALPHA_VALUE
                translationX = DEFAULT_TRANSLATION_X_VALUE
            }

            dispatchAnimationStarted(holder);

            holder.itemView.animate().apply {
                translationX(ENDING_TRANSLATION_X_VALUE)
                alpha(ENDING_ALPHA_VALUE)

                interpolator = AccelerateDecelerateInterpolator()
                duration = DURATION

                setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {
                        dispatchAddStarting(holder)
                    }

                    override fun onAnimationEnd(p0: Animator) {
                        dispatchAddFinished(holder)
                    }

                    override fun onAnimationCancel(p0: Animator) {}

                    override fun onAnimationRepeat(p0: Animator) {}
                })
            }.start()

            mCallback.setViewHolderAnimated(holder)

        } else {
            return false
        }

        return true
    }


}