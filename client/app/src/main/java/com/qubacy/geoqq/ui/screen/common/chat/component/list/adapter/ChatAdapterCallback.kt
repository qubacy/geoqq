package com.qubacy.geoqq.ui.screen.common.chat.component.list.adapter

import com.qubacy.geoqq.domain.common.model.message.Message

interface ChatAdapterCallback {
//    fun getUserById(userId: Long): User
    fun onMessageClicked(message: Message)
}