package com.qubacy.geoqq.domain.mate.chat.projection

import com.qubacy.geoqq.domain.mate.chat.model.MateMessage

data class MateMessageChunk(
    val index: Int,
    val messages: List<MateMessage>
) {

}