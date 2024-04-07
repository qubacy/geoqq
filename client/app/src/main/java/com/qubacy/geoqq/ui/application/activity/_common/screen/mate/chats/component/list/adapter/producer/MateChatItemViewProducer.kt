package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.adapter.producer

import android.view.ViewGroup
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.adapter.producer.BaseItemViewProducer
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.item.MateChatItemView
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.item.data.MateChatItemData

class MateChatItemViewProducer : BaseItemViewProducer<MateChatItemData, MateChatItemView>() {
    override fun createItemView(parent: ViewGroup, viewType: Int): MateChatItemView {
        return MateChatItemView(parent.context)
    }
}