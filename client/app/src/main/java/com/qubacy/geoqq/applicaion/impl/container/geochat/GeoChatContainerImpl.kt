package com.qubacy.geoqq.applicaion.impl.container.geochat

import com.qubacy.geoqq.applicaion.common.container.geochat.GeoChatContainer
import com.qubacy.geoqq.domain.geochat.chat.GeoChatUseCase
import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatViewModelFactory

class GeoChatContainerImpl(
    val radius: Int,
    val geoChatUseCase: GeoChatUseCase
) : GeoChatContainer() {
    override val geoChatViewModelFactory = GeoChatViewModelFactory(radius, geoChatUseCase)
}