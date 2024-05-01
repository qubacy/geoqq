package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.adapter.producer

import android.view.ViewGroup
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.component.list.adapter.producer.MessageItemViewProducer
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.item.GeoMessageItemView
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.item.data.GeoMessageItemData

class GeoMessageItemViewProducer(

) : MessageItemViewProducer<GeoMessageItemData, GeoMessageItemView>() {
    override fun createItemViewProvider(parent: ViewGroup, viewType: Int): GeoMessageItemView {
        return GeoMessageItemView(parent.context)
    }
}