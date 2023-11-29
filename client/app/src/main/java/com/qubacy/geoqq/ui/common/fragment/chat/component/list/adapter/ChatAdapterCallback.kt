package com.qubacy.geoqq.ui.common.fragment.chat.component.list.adapter

import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.ui.common.component.animatedlist.adapter.AnimatedListAdapterCallback

interface ChatAdapterCallback : AnimatedListAdapterCallback {
    fun getUserById(userId: Long): User
    fun onMessageClicked(message: Message)
}