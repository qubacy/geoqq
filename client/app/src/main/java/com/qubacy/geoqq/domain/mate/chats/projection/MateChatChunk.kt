package com.qubacy.geoqq.domain.mate.chats.projection

import com.qubacy.geoqq.domain.mate._common.model.chat.MateChat

data class MateChatChunk(
    val offset: Int,
    val chats: List<MateChat>
) {

}