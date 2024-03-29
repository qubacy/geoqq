package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.adapter

import androidx.annotation.UiThread
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list.adapter.BaseRecyclerViewAdapter
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.adapter.producer.MateChatItemViewProducer
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.item.MateChatItemView
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.item.data.MateChatItemData

class MateChatsListAdapter(
    itemViewProducer: MateChatItemViewProducer = MateChatItemViewProducer()
) : BaseRecyclerViewAdapter<
    MateChatItemData,
    MateChatItemView,
    MateChatItemViewProducer,
    MateChatsListAdapter.ViewHolder
>(
    itemViewProducer
) {
    class ViewHolder(
        baseItemView: MateChatItemView
    ) : BaseRecyclerViewAdapter.ViewHolder<MateChatItemData, MateChatItemView>(
        baseItemView
    ) {

    }

    override fun createViewHolder(itemView: MateChatItemView): ViewHolder {
        return ViewHolder(itemView)
    }

    @UiThread
    fun setMateChats(mateChats: List<MateChatItemData>) {
        resetItems()

        mItems.addAll(mateChats)

        wrappedNotifyItemRangeInserted(0, mItems.size)
    }

    @UiThread
    fun insertMateChats(mateChats: List<MateChatItemData>, position: Int) {
        mItems.addAll(position, mateChats)

        wrappedNotifyItemRangeInserted(position, mItems.size)
    }

    @UiThread
    fun addNewMateChat(mateChat: MateChatItemData) {
        mItems.add(0, mateChat)

        wrappedNotifyItemInserted(0)
    }

    @UiThread
    fun updateMateChat(mateChat: MateChatItemData): Int {
        val mateChatPosition = mItems.indexOfFirst { it.id == mateChat.id }

        mItems[mateChatPosition] = mateChat

        wrappedNotifyItemChanged(mateChatPosition)

        return mateChatPosition
    }

    @UiThread
    fun updateMateChatsChunk(mateChats: List<MateChatItemData>, position: Int) {
        for (i in position until position + mateChats.size)
            mItems[i] = mateChats[i - position]

        wrappedNotifyItemRangeChanged(position, mateChats.size)
    }

    @UiThread
    fun updateAndMoveOnTopMateChat(mateChat: MateChatItemData) {
        val mateChatPosition = updateMateChat(mateChat)

        mItems.removeAt(mateChatPosition)
        mItems.add(0, mateChat)

        wrappedNotifyItemMoved(mateChatPosition, 0)
    }

    @UiThread
    fun deleteMateChats(position: Int, count: Int) {
        val itemsToRemove = mItems.subList(position, position + count)

        mItems.removeAll(itemsToRemove)

        wrappedNotifyItemRangeRemoved(position, count)
    }
}