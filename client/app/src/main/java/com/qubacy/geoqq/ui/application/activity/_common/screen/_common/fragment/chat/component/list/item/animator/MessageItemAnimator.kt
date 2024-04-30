package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.item.animator

import android.view.View
import android.view.ViewPropertyAnimator
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.RecyclerView
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.adapter.MessageListAdapter
import com.qubacy.utility.baserecyclerview.item.animator.BaseRecyclerViewItemAnimator

class MessageItemAnimator : BaseRecyclerViewItemAnimator() {
    override fun prepareHolderForAddAnimation(holder: RecyclerView.ViewHolder) {
        super.prepareHolderForAddAnimation(holder)

        if (holder !is MessageListAdapter.ViewHolder<*, *>) return

        val messageItemView = holder.baseItemViewProvider
        val itemContentWrapperGravity = getViewHolderViewGravity(holder)

        messageItemView.apply {
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

    private fun getViewHolderViewGravity(holder: MessageListAdapter.ViewHolder<*, *>): Int {
        val mateMessageItemView = holder.baseItemViewProvider
        val itemContentWrapperLayoutParams = mateMessageItemView.getContentWrapper().layoutParams

        return (itemContentWrapperLayoutParams as LinearLayout.LayoutParams).gravity
    }
}