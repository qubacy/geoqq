package com.qubacy.geoqq.domain.mate.chats.usecase._common._test.context

import com.qubacy.geoqq.domain.interlocutor.usecase._common._test.context.InterlocutorUseCaseTestContext
import com.qubacy.geoqq.domain.mate.chat.usecase._common._test.context.MateChatUseCaseTestContext
import com.qubacy.geoqq.domain.mate.chats.model.MateChat

object MateChatsUseCaseTestContext {
    private val DEFAULT_USER = InterlocutorUseCaseTestContext.DEFAULT_USER
    private val DEFAULT_MATE_MESSAGE = MateChatUseCaseTestContext.DEFAULT_MATE_MESSAGE

    val DEFAULT_MATE_CHAT = MateChat(0L, DEFAULT_USER, 0, DEFAULT_MATE_MESSAGE)
}