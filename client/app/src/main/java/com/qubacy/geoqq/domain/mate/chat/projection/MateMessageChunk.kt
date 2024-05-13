package com.qubacy.geoqq.domain.mate.chat.projection

import com.qubacy.geoqq.domain.mate._common.model.message.MateMessage

data class MateMessageChunk(
    val offset: Int,
    val messages: List<MateMessage>
) {

}