package com.qubacy.geoqq.applicaion.common.container.geo.chat

import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatViewModelFactory

abstract class GeoChatContainer() {
    abstract val geoChatViewModelFactory: GeoChatViewModelFactory
}