package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.adapter

import android.util.Log
import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.adapter.producer.MateMessageItemViewProducer
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.item.MateMessageItemView
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.item.data.MateMessageItemData
import com.qubacy.utility.baserecyclerview.adapter.BaseRecyclerViewAdapter
import com.qubacy.utility.baserecyclerview.view.BaseRecyclerView

class MateMessageListAdapter(
    itemViewProducer: MateMessageItemViewProducer = MateMessageItemViewProducer()
) : BaseRecyclerViewAdapter<
    MateMessageItemData,
    MateMessageItemView,
    MateMessageItemViewProducer,
    MateMessageListAdapter.ViewHolder
>(
    itemViewProducer
) {
    class ViewHolder(
        baseItemView: MateMessageItemView
    ) : BaseRecyclerViewAdapter.ViewHolder<MateMessageItemData, MateMessageItemView>(
        baseItemView
    ) {

    }

    private lateinit var mRecyclerView: BaseRecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        mRecyclerView = recyclerView as BaseRecyclerView
    }

    override fun createViewHolder(itemView: MateMessageItemView): ViewHolder {
        return ViewHolder(itemView)
    }

    @UiThread
    fun setMateMessages(mateMessages: List<MateMessageItemData>) {
        resetItems()

        mItems.addAll(mateMessages)

        wrappedNotifyItemRangeInserted(0, mItems.size)
    }

    @UiThread
    fun insertMateMessages(mateMessages: List<MateMessageItemData>, position: Int) {
        mItems.addAll(position, mateMessages)

        wrappedNotifyItemRangeInserted(position, mItems.size)
    }

    @UiThread
    fun addNewMateMessage(mateMessage: MateMessageItemData) {
        mItems.add(0, mateMessage)

        wrappedNotifyItemInserted(0)
        runScrollToPosition(0)
    }

    @UiThread
    fun updateMateMessageChunk(mateMessages: List<MateMessageItemData>, position: Int) {
        for (i in position until position + mateMessages.size)
            mItems[i] = mateMessages[i - position]

        wrappedNotifyItemRangeChanged(position, mateMessages.size)
    }

    @UiThread
    fun deleteMateMessages(position: Int, count: Int) {
        val itemsToRemove = mItems.subList(position, position + count)

        mItems.removeAll(itemsToRemove)

        Log.d(TAG, "deleteMateMessages(): mItems.size = ${mItems.size};")

        wrappedNotifyItemRangeRemoved(position, count)
    }

    private fun runScrollToPosition(position: Int) {
        if (!mRecyclerView.isAtStart()) return

        mRecyclerView.scrollToPosition(position)
    }
}
