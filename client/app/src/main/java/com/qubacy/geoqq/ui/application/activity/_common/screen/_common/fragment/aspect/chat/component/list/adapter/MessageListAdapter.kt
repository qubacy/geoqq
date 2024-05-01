package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.component.list.adapter

import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.component.list.adapter.producer.MessageItemViewProducer
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.component.list.item.MessageItemView
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.component.list.item.data.MessageItemData
import com.qubacy.utility.baserecyclerview.adapter.BaseRecyclerViewAdapter
import com.qubacy.utility.baserecyclerview.view.BaseRecyclerView

open class MessageListAdapter<
    MessageItemDataType : MessageItemData,
    MessageItemViewType : MessageItemView<MessageItemDataType>
>(
    itemViewProducer: MessageItemViewProducer<MessageItemDataType, MessageItemViewType>
) : BaseRecyclerViewAdapter<
    MessageItemDataType,
    MessageItemViewType,
    MessageItemViewProducer<MessageItemDataType, MessageItemViewType>,
        com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.component.list.adapter.MessageListAdapter.ViewHolder<MessageItemDataType, MessageItemViewType>
>(
    itemViewProducer
) {
    open class ViewHolder<
        MessageItemDataType : MessageItemData,
        MessageItemViewType : MessageItemView<MessageItemDataType>
    >(
        baseItemView: MessageItemViewType
    ) : BaseRecyclerViewAdapter.ViewHolder<MessageItemDataType, MessageItemViewType>(
        baseItemView
    ) {

    }

    protected lateinit var mRecyclerView: BaseRecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        mRecyclerView = recyclerView as BaseRecyclerView
    }

    override fun createViewHolder(
        itemView: MessageItemViewType
    ): ViewHolder<MessageItemDataType, MessageItemViewType> {
        return ViewHolder(itemView)
    }

    @UiThread
    open fun setMessages(messages: List<MessageItemDataType>) {
        resetItems()

        mItems.addAll(messages)

        wrappedNotifyItemRangeInserted(0, mItems.size)
    }

    @UiThread
    open fun addMessages(messages: List<MessageItemDataType>) {
        mItems.addAll(messages)

        wrappedNotifyItemRangeInserted(0, messages.size)
    }

    protected open fun runScrollToPosition(position: Int) {
        if (!mRecyclerView.isAtStart()) return

        mRecyclerView.scrollToPosition(position)
    }
}
