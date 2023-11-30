package com.qubacy.geoqq.domain.common.util.generator

import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.mate.chats.model.MateChat

object MateChatGeneratorUtility {
    fun generateMateChats(count: Int, startId: Long = 0L): List<MateChat> {
        return LongRange(startId, startId + count - 1).map {
            val lastMessage = Message(it, 0L, "test message $it", 123123123)

            MateChat(it, it + 1, UserGeneratorUtility.DEFAULT_URI, lastMessage)
        }
    }
}