package com.qubacy.geoqq.applicaion.container.mate.chat

import com.qubacy.geoqq.ui.screen.mate.chat.model.MateChatViewModelFactory

class MateChatContainer(
    val chatId: Long
) {
    val mateChatViewModelFactory = MateChatViewModelFactory(chatId)
}