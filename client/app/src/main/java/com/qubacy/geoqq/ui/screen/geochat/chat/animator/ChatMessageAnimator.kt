package com.qubacy.geoqq.ui.screen.geochat.chat.animator

import android.animation.Animator
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class ChatMessageAnimator(
    private val mCallback: ChatMessageAnimatorCallback
) : DefaultItemAnimator() {
    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
        Log.d("TEST", "entering animateAdd()..")

        if (holder != null) {
            if (!mCallback.wasViewHolderAnimated(holder)) {
                holder.itemView.alpha = 0f
                holder.itemView.translationX = -100f

                dispatchAnimationStarted(holder);

                holder.itemView.animate().apply {
                    translationX(0f)
                    alpha(1f)

                    interpolator = AccelerateDecelerateInterpolator()
                    duration = 400

                    setListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(p0: Animator) {
                            Log.d("TEST", "onAnimationStart")
                        }

                        override fun onAnimationEnd(p0: Animator) {
                            Log.d("TEST", "onAnimationEnd")

                            dispatchAddFinished(holder)
                        }

                        override fun onAnimationCancel(p0: Animator) {
                            Log.d("TEST", "onAnimationCancel")
                        }

                        override fun onAnimationRepeat(p0: Animator) {}
                    })
                }.start()

                mCallback.setViewHolderAnimated(holder)

                return true
            }
        }


        return true
        //return super.animateAdd(holder)
    }
}