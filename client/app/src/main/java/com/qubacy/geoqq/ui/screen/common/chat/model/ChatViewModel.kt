package com.qubacy.geoqq.ui.screen.common.chat.model

interface ChatViewModel {
    fun isLocalUser(userId: Long): Boolean
}