package com.qubacy.geoqq.domain.common.util.generator

import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.mate.chats.model.MateChat

object MateChatGeneratorUtility {
    fun generateMateChats(
        count: Int, startId: Long = 0L, lastMessage: Message? = null, newMessageCount: Int = 0
    ): List<MateChat> {
        return LongRange(startId, startId + count - 1).reversed().map {
            val curLastMessage = lastMessage
                ?: Message(0L, 0L, "test message $it", 123123123)

            MateChat(
                it, it + 1,
                UserGeneratorUtility.DEFAULT_URI, curLastMessage, newMessageCount
            )
        }
    }
}