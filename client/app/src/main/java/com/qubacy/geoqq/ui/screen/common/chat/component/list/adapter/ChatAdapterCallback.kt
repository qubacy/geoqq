package com.qubacy.geoqq.ui.screen.common.chat.component.list.adapter

import com.qubacy.geoqq.data.common.entity.message.Message
import com.qubacy.geoqq.data.common.entity.person.user.User

interface ChatAdapterCallback {
    fun getUserById(userId: Long): User
    fun onMessageClicked(message: Message)
}