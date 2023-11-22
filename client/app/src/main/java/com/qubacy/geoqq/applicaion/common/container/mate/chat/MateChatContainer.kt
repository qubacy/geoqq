package com.qubacy.geoqq.applicaion.common.container.mate.chat

import com.qubacy.geoqq.domain.mate.chat.MateChatUseCase
import com.qubacy.geoqq.ui.screen.mate.chat.model.MateChatViewModelFactory

class MateChatContainer(
    val chatId: Long,
    val interlocutorUserId: Long,
    val mateChatUseCase: MateChatUseCase
) {
    val mateChatViewModelFactory = MateChatViewModelFactory(
        chatId, interlocutorUserId, mateChatUseCase)
}