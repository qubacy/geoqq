package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.item.animator

import android.view.View
import android.view.ViewPropertyAnimator
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.RecyclerView
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.item.animator.BaseRecyclerViewItemAnimator
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.adapter.MateMessageListAdapter

class MateMessageItemAnimator : BaseRecyclerViewItemAnimator() {
    override fun prepareHolderForAddAnimation(holder: RecyclerView.ViewHolder) {
        super.prepareHolderForAddAnimation(holder)

        if (holder !is MateMessageListAdapter.ViewHolder) return

        val mateMessageItemView = holder.baseItemViewProvider
        val itemContentWrapperGravity = getViewHolderViewGravity(holder)

        mateMessageItemView.apply {
            translationX =
                if (itemContentWrapperGravity == GravityCompat.END) width.toFloat()
                else -width.toFloat()
        }
    }

    override fun prepareViewAnimatorForAddAnimation(animator: ViewPropertyAnimator) {
        super.prepareViewAnimatorForAddAnimation(animator)

        animator.translationX(0f)
    }

    override fun onAnimateAddCancelled(view: View) {
        super.onAnimateAddCancelled(view)

        view.translationX = 0f
    }

    private fun getViewHolderViewGravity(holder: MateMessageListAdapter.ViewHolder): Int {
        val mateMessageItemView = holder.baseItemViewProvider
        val itemContentWrapperLayoutParams = mateMessageItemView.getContentWrapper().layoutParams

        return (itemContentWrapperLayoutParams as LinearLayout.LayoutParams).gravity
    }
}