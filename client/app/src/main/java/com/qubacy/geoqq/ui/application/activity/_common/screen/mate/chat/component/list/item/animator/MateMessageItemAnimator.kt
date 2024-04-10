package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.item.animator

import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.adapter.MateMessageListAdapter

class MateMessageItemAnimator : DefaultItemAnimator() {
    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
        super.animateAdd(holder)

        if (holder == null || holder !is MateMessageListAdapter.ViewHolder) return true

        val mateMessageItemView = holder.baseItemView
        val itemContentWrapperGravity = getViewHolderViewGravity(holder)

        mateMessageItemView.apply {
            translationX =
                if (itemContentWrapperGravity == GravityCompat.END) width.toFloat()
                else -width.toFloat()
        }
        mateMessageItemView.animate().translationX(0f)

        return true
    }

    private fun getViewHolderViewGravity(holder: MateMessageListAdapter.ViewHolder): Int {
        val mateMessageItemView = holder.baseItemView
        val itemContentWrapperLayoutParams = mateMessageItemView.getContentWrapper().layoutParams

        return (itemContentWrapperLayoutParams as LinearLayout.LayoutParams).gravity
    }
}