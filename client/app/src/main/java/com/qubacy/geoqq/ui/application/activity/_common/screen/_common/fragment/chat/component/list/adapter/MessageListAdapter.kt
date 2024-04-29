package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.adapter

import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.adapter.producer.MessageItemViewProducer
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.item.MessageItemView
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.item.data.MessageItemData
import com.qubacy.utility.baserecyclerview.adapter.BaseRecyclerViewAdapter
import com.qubacy.utility.baserecyclerview.view.BaseRecyclerView

open class MessageListAdapter(
    itemViewProducer: MessageItemViewProducer = MessageItemViewProducer()
) : BaseRecyclerViewAdapter<
    MessageItemData,
    MessageItemView,
    MessageItemViewProducer,
    MessageListAdapter.ViewHolder
>(
    itemViewProducer
) {
    open class ViewHolder(
        baseItemView: MessageItemView
    ) : BaseRecyclerViewAdapter.ViewHolder<MessageItemData, MessageItemView>(
        baseItemView
    ) {

    }

    protected lateinit var mRecyclerView: BaseRecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        mRecyclerView = recyclerView as BaseRecyclerView
    }

    override fun createViewHolder(itemView: MessageItemView): ViewHolder {
        return ViewHolder(itemView)
    }

    @UiThread
    open fun setMessages(messages: List<MessageItemData>) {
        resetItems()

        mItems.addAll(messages)

        wrappedNotifyItemRangeInserted(0, mItems.size)
    }

    @UiThread
    open fun addMessages(messages: List<MessageItemData>) {
        mItems.addAll(messages)

        wrappedNotifyItemRangeInserted(0, messages.size)
    }

    protected open fun runScrollToPosition(position: Int) {
        if (!mRecyclerView.isAtStart()) return

        mRecyclerView.scrollToPosition(position)
    }
}
