package com.qubacy.geoqq.ui.common.visual.fragment.chat.component.list.adapter

import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.ui.common.visual.component.animatedlist.adapter.AnimatedListAdapterCallback

interface ChatAdapterCallback : AnimatedListAdapterCallback {
    fun getUserById(userId: Long): User
    fun onMessageClicked(message: Message)
}