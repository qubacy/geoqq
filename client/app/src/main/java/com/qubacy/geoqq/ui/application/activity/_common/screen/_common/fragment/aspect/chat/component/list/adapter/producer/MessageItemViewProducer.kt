package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.component.list.adapter.producer

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.component.list.item.MessageItemView
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.component.list.item.data.MessageItemData
import com.qubacy.utility.baserecyclerview.adapter.producer.BaseRecyclerViewItemViewProviderProducer

abstract class MessageItemViewProducer<
    MessageItemDataType : MessageItemData,
    MessageItemViewType : MessageItemView<MessageItemDataType>
> : BaseRecyclerViewItemViewProviderProducer<
    MessageItemDataType, MessageItemViewType
>() {

}