package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.adapter

import androidx.annotation.UiThread
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.component.list.adapter.MessageListAdapter
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.component.list.item.data.side.SenderSide
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.adapter.producer.GeoMessageItemViewProducer
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.item.GeoMessageItemView
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.item.data.GeoMessageItemData

class GeoMessageListAdapter(
    itemViewProducer: GeoMessageItemViewProducer = GeoMessageItemViewProducer(),
    geoCallback: GeoMessageListAdapterCallback
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

            if (data.senderSide != SenderSide.ME)
                baseItemViewProvider.setOnClickListener { onClickAction(adapterPosition) }
        }
    }

    private val mGeoCallback: GeoMessageListAdapterCallback = geoCallback

    override fun createViewHolder(
        itemView: GeoMessageItemView
    ): MessageListAdapter.ViewHolder<GeoMessageItemData, GeoMessageItemView> {
        return ViewHolder(itemView) {
            mGeoCallback.onGeoMessageClicked(it)
        }
    }

    @UiThread
    fun updateItems(
        positions: List<Int>,
        items: List<GeoMessageItemData>
    ) {
        for (i in positions.indices) {
            val position = positions[i]

            mItems[position] = items[i]

            notifyItemChanged(position) // todo: isn't it overwhelming for the UI?
        }
    }
}
