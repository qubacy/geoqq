package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.adapter.producer

import android.view.ViewGroup
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.item.MessageItemView
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.item.data.MessageItemData
import com.qubacy.utility.baserecyclerview.adapter.producer.BaseRecyclerViewItemViewProviderProducer

open class MessageItemViewProducer : BaseRecyclerViewItemViewProviderProducer<
        MessageItemData, MessageItemView
>() {
    override fun createItemViewProvider(parent: ViewGroup, viewType: Int): MessageItemView {
        return MessageItemView(parent.context)
    }
}