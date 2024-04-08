package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.item.animator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.adapter.MateMessageListAdapter

class MateMessageItemAnimator : SimpleItemAnimator() {
    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder?,
        newHolder: RecyclerView.ViewHolder?,
        fromLeft: Int,
        fromTop: Int,
        toLeft: Int,
        toTop: Int
    ): Boolean {
        return false
    }

    override fun runPendingAnimations() { }

    override fun endAnimation(item: RecyclerView.ViewHolder) { }

    override fun endAnimations() { }

    override fun isRunning(): Boolean {
        return false
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
        return false
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
        if (holder == null || holder !is MateMessageListAdapter.ViewHolder) return false

        val mateMessageItemView = holder.baseItemView
        val itemContentWrapperLayoutParams = mateMessageItemView.getContentWrapper().layoutParams
        val itemContentWrapperGravity = (itemContentWrapperLayoutParams as LinearLayout.LayoutParams)
            .gravity

        mateMessageItemView.apply {
            translationX =
                if (itemContentWrapperGravity == GravityCompat.END) width.toFloat()
                else -width.toFloat()
        }

        val onEndAction = {
            dispatchAddFinished(holder)

            mateMessageItemView.translationX = 0f

            Unit
        }

        mateMessageItemView.animate().apply {
            translationX(0f)

            duration = 300L
            interpolator = DecelerateInterpolator()
        }.setListener(createAnimatorListener({}, onEndAction)).start()

        return true
    }

    override fun animateMove(
        holder: RecyclerView.ViewHolder?,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ): Boolean {
        return false
    }

    private fun createAnimatorListener(
        startAction: () -> Unit,
        endAction: () -> Unit
    ): Animator.AnimatorListener {
        return object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) { startAction() }
            override fun onAnimationCancel(animation: Animator) { endAction() }
            override fun onAnimationEnd(animation: Animator) { endAction() }
        }
    }
}