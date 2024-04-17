package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.adapter.producer

import android.view.ViewGroup
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.item.MateChatItemView
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.item.data.MateChatItemData
import com.qubacy.utility.baserecyclerview.adapter.producer.BaseRecyclerViewItemViewProviderProducer

class MateChatItemViewProducer : BaseRecyclerViewItemViewProviderProducer<MateChatItemData, MateChatItemView>() {
    override fun createItemViewProvider(parent: ViewGroup, viewType: Int): MateChatItemView {
        return MateChatItemView(parent.context)
    }
}