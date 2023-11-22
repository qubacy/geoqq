package com.qubacy.geoqq.applicaion.common.container.mate.chats

import com.qubacy.geoqq.domain.mate.chats.MateChatsUseCase
import com.qubacy.geoqq.ui.screen.mate.chats.model.MateChatsViewModelFactory

class MateChatsContainer(
    private val mateChatsUseCase: MateChatsUseCase
) {
    val mateChatsViewModelFactory = MateChatsViewModelFactory(mateChatsUseCase)
}