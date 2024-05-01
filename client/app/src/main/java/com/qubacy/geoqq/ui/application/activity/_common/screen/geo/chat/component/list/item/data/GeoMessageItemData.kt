package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.item.data

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.component.list.item.data.MessageItemData
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.component.list.item.data.side.SenderSide

class GeoMessageItemData(
    id: Long,
    senderSide: SenderSide,
    text: String?,
    timestamp: String,
    val username: String
) : MessageItemData(id, senderSide, text, timestamp) {

}