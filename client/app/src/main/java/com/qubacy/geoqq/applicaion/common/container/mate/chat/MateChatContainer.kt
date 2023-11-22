package com.qubacy.geoqq.applicaion.common.container.mate.chat

import com.qubacy.geoqq.ui.screen.mate.chat.model.MateChatViewModelFactory

abstract class MateChatContainer() {
    abstract val mateChatViewModelFactory: MateChatViewModelFactory
}