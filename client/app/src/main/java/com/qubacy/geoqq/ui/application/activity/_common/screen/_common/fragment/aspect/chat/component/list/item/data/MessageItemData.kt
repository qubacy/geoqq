package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.component.list.item.data

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.component.list.item.data.side.SenderSide
import com.qubacy.utility.baserecyclerview.item.data.BaseRecyclerViewItemData

open class MessageItemData(
    val id: Long,
    val senderSide: SenderSide,
    val text: String?,
    val timestamp: String
) : BaseRecyclerViewItemData {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is MessageItemData) return false

        return (other.id == id)
    }
}