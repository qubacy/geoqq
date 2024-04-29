package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.adapter

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.adapter.MessageListAdapter
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.adapter.producer.MessageItemViewProducer
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.item.MessageItemView
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.item.data.MessageItemData

class GeoMessageListAdapter(
    itemViewProducer: MessageItemViewProducer = MessageItemViewProducer()
) : MessageListAdapter(
    itemViewProducer
) {
    class ViewHolder(
        baseItemView: MessageItemView,
        val onClickAction: (Int) -> Unit
    ) : MessageListAdapter.ViewHolder(
        baseItemView
    ) {
        override fun setData(data: MessageItemData) {
            super.setData(data)

            baseItemViewProvider.setOnClickListener { onClickAction(adapterPosition) }
        }
    }
}
