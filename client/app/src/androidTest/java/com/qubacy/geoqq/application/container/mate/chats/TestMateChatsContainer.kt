package com.qubacy.geoqq.applicaion.common.container.mate.chats

import com.qubacy.geoqq.domain.mate.chats.MateChatsUseCase
import com.qubacy.geoqq.ui.screen.mate.chats.model.MateChatsViewModelFactory

class TestMateChatsContainer(
    private val mateChatsUseCase: MateChatsUseCase
) : MateChatsContainer() {
    override val mateChatsViewModelFactory = MateChatsViewModelFactory(mateChatsUseCase)
}