package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.adapter.producer

import android.view.ViewGroup
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.adapter.producer.MessageItemViewProducer
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.item.MessageItemView
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.item.data.MessageItemData

class MateMessageItemViewProducer(

) : MessageItemViewProducer<MessageItemData, MessageItemView<MessageItemData>>() {
    override fun createItemViewProvider(
        parent: ViewGroup,
        viewType: Int
    ): MessageItemView<MessageItemData> {
        return MessageItemView(parent.context)
    }
}