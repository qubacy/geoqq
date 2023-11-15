package com.qubacy.geoqq.ui.screen.mate.chats.list.adapter

import android.view.View
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.mate.chats.model.MateChat

interface MateChatsAdapterCallback {
    fun getUser(userId: Long): User
    fun onChatClicked(chatPreview: MateChat, chatView: View)
}