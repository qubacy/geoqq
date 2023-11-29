package com.qubacy.geoqq.ui.screen.common.fragment.chat

import com.qubacy.geoqq.domain.common.model.message.Message

object ChatFragmentContext {
    fun generateTestMessages(count: Int): List<Message> {
        return IntRange(0, count - 1).map {
            Message(it.toLong(), 0L, "test message $it", 123123123)
        }
    }
}