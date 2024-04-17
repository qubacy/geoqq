package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.item.data

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list.message.item.data.side.SenderSide
import com.qubacy.utility.baserecyclerview.item.data.BaseRecyclerViewItemData

data class MateMessageItemData(
    val id: Long,
    val senderSide: SenderSide,
    val text: String?,
    val timestamp: String
) : BaseRecyclerViewItemData {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is MateMessageItemData) return false

        return (other.id == id)
    }
}