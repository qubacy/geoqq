package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.adapter.producer

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.item.MessageItemView
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.item.data.MessageItemData
import com.qubacy.utility.baserecyclerview.adapter.producer.BaseRecyclerViewItemViewProviderProducer

abstract class MessageItemViewProducer<
    MessageItemDataType : MessageItemData,
    MessageItemViewType : MessageItemView<MessageItemDataType>
> : BaseRecyclerViewItemViewProviderProducer<
    MessageItemDataType, MessageItemViewType
>() {
//    override fun createItemViewProvider(parent: ViewGroup, viewType: Int): MessageItemViewType {
//        return MessageItemViewType(parent.context)
//    }
}