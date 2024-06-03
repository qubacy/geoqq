package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.adapter

import androidx.annotation.UiThread
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.adapter.producer.MateChatItemViewProducer
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.item.MateChatItemView
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.item.data.MateChatItemData
import com.qubacy.utility.baserecyclerview.adapter.BaseRecyclerViewAdapter

class MateChatsListAdapter(
    itemViewProducer: MateChatItemViewProducer = MateChatItemViewProducer(),
    callback: MateChatsListAdapterCallback
) : BaseRecyclerViewAdapter<
    MateChatItemData,
    MateChatItemView,
    MateChatItemViewProducer,
    MateChatsListAdapter.ViewHolder
>(
    itemViewProducer
) {
    class ViewHolder(
        baseItemView: MateChatItemView,
        val onClickAction: (Long) -> Unit
    ) : BaseRecyclerViewAdapter.ViewHolder<MateChatItemData, MateChatItemView>(
        baseItemView
    ) {
        override fun setData(data: MateChatItemData) {
            super.setData(data)

            baseItemViewProvider.setOnClickListener { onClickAction(data.id) }
        }
    }

    private val mCallback: MateChatsListAdapterCallback = callback

    override fun createViewHolder(itemView: MateChatItemView): ViewHolder {
        return ViewHolder(itemView) { mCallback.onChatPreviewClicked(it) }
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
    fun updateAndMoveToPositionMateChat(mateChat: MateChatItemData, position: Int): Int {
        val mateChatPosition = mItems.indexOfFirst { it.id == mateChat.id }

        mItems.removeAt(mateChatPosition)
        mItems.add(position, mateChat)

        // todo: right?:
        wrappedNotifyItemMoved(mateChatPosition, position)
        wrappedNotifyItemChanged(position)

        return mateChatPosition
    }

    @UiThread
    fun updateMateChatsChunk(mateChats: List<MateChatItemData>, position: Int) {
        for (i in position until position + mateChats.size)
            mItems[i] = mateChats[i - position]

        wrappedNotifyItemRangeChanged(position, mateChats.size)
    }

    @UiThread
    fun deleteMateChats(position: Int, count: Int) {
        val itemsToRemove = mItems.subList(position, position + count)

        mItems.removeAll(itemsToRemove)

        wrappedNotifyItemRangeRemoved(position, count)
    }
}