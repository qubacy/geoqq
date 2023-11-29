package com.qubacy.geoqq.domain.common.util.generator

import com.qubacy.geoqq.domain.common.model.message.Message

object MessageGeneratorUtility {
    fun generateMessages(count: Int, startId: Long = 0L): List<Message> {
        return LongRange(startId, startId + count - 1).map {
            Message(it, 0L, "test message $it", 123123123)
        }
    }
}