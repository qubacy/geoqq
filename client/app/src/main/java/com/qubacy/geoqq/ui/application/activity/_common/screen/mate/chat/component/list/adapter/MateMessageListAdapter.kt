package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.adapter

import android.util.Log
import androidx.annotation.UiThread
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.adapter.MessageListAdapter
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.item.MessageItemView
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.item.data.MessageItemData
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.adapter.producer.MateMessageItemViewProducer

class MateMessageListAdapter(
    itemViewProducer: MateMessageItemViewProducer = MateMessageItemViewProducer()
) : MessageListAdapter<MessageItemData, MessageItemView<MessageItemData>>(
    itemViewProducer
) {
    @UiThread
    fun insertMateMessages(mateMessages: List<MessageItemData>, position: Int) {
        mItems.addAll(position, mateMessages)

        wrappedNotifyItemRangeInserted(position, mItems.size)
    }

    @UiThread
    fun addNewMateMessage(mateMessage: MessageItemData) {
        mItems.add(0, mateMessage)

        wrappedNotifyItemInserted(0)
        runScrollToPosition(0)
    }

    @UiThread
    fun updateMateMessageChunk(mateMessages: List<MessageItemData>, position: Int) {
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
}
