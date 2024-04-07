package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.adapter.producer

import android.view.ViewGroup
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.adapter.producer.BaseItemViewProducer
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.item.MateMessageItemView
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.item.data.MateMessageItemData

class MateMessageItemViewProducer : BaseItemViewProducer<MateMessageItemData, MateMessageItemView>() {
    override fun createItemView(parent: ViewGroup, viewType: Int): MateMessageItemView {
        return MateMessageItemView(parent.context)
    }
}