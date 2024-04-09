package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.item.animator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.adapter.MateMessageListAdapter

class MateMessageItemAnimator : SimpleItemAnimator() {
    companion object {
        const val DEFAULT_ANIMATION_DURATION = 300L
    }

    // todo: traverse it and think how to optimize:
    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder?,
        newHolder: RecyclerView.ViewHolder?,
        fromLeft: Int,
        fromTop: Int,
        toLeft: Int,
        toTop: Int
    ): Boolean {
        if (oldHolder == null || newHolder == null
         || oldHolder !is MateMessageListAdapter.ViewHolder
         || newHolder !is MateMessageListAdapter.ViewHolder
        ) {
            return false
        }

        val oldMateMessageItemView = oldHolder.baseItemView
        val newMateMessageItemView = newHolder.baseItemView

        oldMateMessageItemView.apply { alpha = 1f }
        newMateMessageItemView.apply { alpha = 0f }

        val newHolderAnimation = newMateMessageItemView.animate().apply {
            alpha(1f)

            duration = DEFAULT_ANIMATION_DURATION
            interpolator = AccelerateInterpolator()
        }.setListener(createAnimatorListener({}, {
            newMateMessageItemView.alpha = 1f

            dispatchChangeFinished(oldHolder, false)
        }))

        val onOldHolderDisappearAction = {
            oldMateMessageItemView.alpha = 1f

            dispatchChangeFinished(oldHolder, true)
            newHolderAnimation.start()
        }

        oldMateMessageItemView.animate().apply {
            alpha(0f)

            duration = DEFAULT_ANIMATION_DURATION
            interpolator = AccelerateInterpolator()
        }.setListener(createAnimatorListener({}, onOldHolderDisappearAction)).start()

        return true
    }

    override fun runPendingAnimations() { }

    override fun endAnimation(item: RecyclerView.ViewHolder) { }

    override fun endAnimations() { }

    override fun isRunning(): Boolean {
        return false
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
        if (holder == null || holder !is MateMessageListAdapter.ViewHolder) return false

        val mateMessageItemView = holder.baseItemView
        val itemContentWrapperGravity = getViewHolderViewGravity(holder)

        val endTranslationX =
            if (itemContentWrapperGravity == GravityCompat.END) mateMessageItemView.width.toFloat()
            else -mateMessageItemView.width.toFloat()

        mateMessageItemView.apply {
            translationX = 0f
        }

        val onEndAction = {
            dispatchRemoveFinished(holder)

            mateMessageItemView.translationX = endTranslationX

            Unit
        }

        mateMessageItemView.animate().apply {
            translationX(0f)

            duration = DEFAULT_ANIMATION_DURATION
            interpolator = DecelerateInterpolator()
        }.setListener(createAnimatorListener({}, onEndAction)).start()

        return true
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
        if (holder == null || holder !is MateMessageListAdapter.ViewHolder) return false

        val mateMessageItemView = holder.baseItemView
        val itemContentWrapperGravity = getViewHolderViewGravity(holder)

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

            duration = DEFAULT_ANIMATION_DURATION
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

    private fun getViewHolderViewGravity(holder: MateMessageListAdapter.ViewHolder): Int {
        val mateMessageItemView = holder.baseItemView
        val itemContentWrapperLayoutParams = mateMessageItemView.getContentWrapper().layoutParams

        return (itemContentWrapperLayoutParams as LinearLayout.LayoutParams).gravity
    }
}