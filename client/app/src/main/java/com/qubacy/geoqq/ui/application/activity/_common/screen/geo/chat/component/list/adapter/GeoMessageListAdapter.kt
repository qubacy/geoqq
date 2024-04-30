package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.adapter

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.adapter.MessageListAdapter
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.adapter.producer.GeoMessageItemViewProducer
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.item.GeoMessageItemView
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.item.data.GeoMessageItemData

class GeoMessageListAdapter(
    itemViewProducer: GeoMessageItemViewProducer = GeoMessageItemViewProducer()
) : MessageListAdapter<GeoMessageItemData, GeoMessageItemView>(
    itemViewProducer
) {
    class ViewHolder(
        baseItemView: GeoMessageItemView,
        val onClickAction: (Int) -> Unit
    ) : MessageListAdapter.ViewHolder<GeoMessageItemData, GeoMessageItemView>(
        baseItemView
    ) {
        override fun setData(data: GeoMessageItemData) {
            super.setData(data)

            baseItemViewProvider.setOnClickListener { onClickAction(adapterPosition) }
        }
    }
}
