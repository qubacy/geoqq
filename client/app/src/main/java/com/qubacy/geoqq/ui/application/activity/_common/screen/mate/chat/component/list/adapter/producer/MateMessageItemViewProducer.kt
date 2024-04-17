package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.adapter.producer

import android.view.ViewGroup
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.item.MateMessageItemView
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.item.data.MateMessageItemData
import com.qubacy.utility.baserecyclerview.adapter.producer.BaseRecyclerViewItemViewProviderProducer

class MateMessageItemViewProducer : BaseRecyclerViewItemViewProviderProducer<MateMessageItemData, MateMessageItemView>() {
    override fun createItemViewProvider(parent: ViewGroup, viewType: Int): MateMessageItemView {
        return MateMessageItemView(parent.context)
    }
}