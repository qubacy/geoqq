package com.qubacy.geoqq.domain.mate._common._test.context

import com.qubacy.geoqq.domain._common._test.context.UseCaseTestContext
import com.qubacy.geoqq.domain.mate._common.model.chat.MateChat
import com.qubacy.geoqq.domain.mate._common.model.message.MateMessage
import com.qubacy.geoqq.domain.mate._common.model.request.MateRequest

object MateUseCaseTestContext {
    private val DEFAULT_USER = UseCaseTestContext.DEFAULT_USER

    val DEFAULT_MATE_MESSAGE = MateMessage(0L, DEFAULT_USER, "test", 0L)
    val DEFAULT_MATE_CHAT = MateChat(0L, DEFAULT_USER, 0, DEFAULT_MATE_MESSAGE)
    val DEFAULT_MATE_REQUEST = MateRequest(0L, DEFAULT_USER)
}