package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.item.data

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.item.data.RecyclerViewItemData
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list.message.item.data.side.SenderSide

data class MateMessageItemData(
    val id: Long,
    val senderSide: SenderSide,
    val text: String?,
    val timestamp: String
) : RecyclerViewItemData {

}