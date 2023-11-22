package com.qubacy.geoqq.ui.screen.common.chat

import com.qubacy.geoqq.domain.common.model.message.Message

object ChatFragmentContext {
    fun generateTestMessages(count: Int): List<Message> {
        return IntRange(0, count).map {
            Message(it.toLong(), 0L, "test", 123123123)
        }
    }
}