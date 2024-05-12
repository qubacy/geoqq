package com.qubacy.geoqq.domain.mate.chat.usecase._common._test.context

import com.qubacy.geoqq.domain.interlocutor.usecase._common._test.context.InterlocutorUseCaseTestContext
import com.qubacy.geoqq.domain.mate.chat.model.MateMessage

object MateChatUseCaseTestContext {
    private val DEFAULT_USER = InterlocutorUseCaseTestContext.DEFAULT_USER

    val DEFAULT_MATE_MESSAGE = MateMessage(0L, DEFAULT_USER, "test", 0L)
}