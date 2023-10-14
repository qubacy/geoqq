package com.qubacy.geoqq.ui.screen.mate.chats.list.adapter

import com.qubacy.geoqq.data.common.entity.chat.Chat

interface MateChatsAdapterCallback {
    fun onChatClicked(chat: Chat)
}