package com.qubacy.geoqq.applicaion.common.container.geochat

import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatViewModelFactory

abstract class GeoChatContainer() {
    abstract val geoChatViewModelFactory: GeoChatViewModelFactory
}