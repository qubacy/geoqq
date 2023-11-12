package com.qubacy.geoqq.ui.screen.mate.chats.list.adapter

import android.view.View
import com.qubacy.geoqq.domain.mate.chats.model.MateChat

interface MateChatsAdapterCallback {
    fun onChatClicked(chatPreview: MateChat, chatView: View)
}