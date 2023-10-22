package com.qubacy.geoqq.ui.screen.mate.chats.list.adapter

import android.view.View
import com.qubacy.geoqq.data.mates.chats.entity.MateChatPreview

interface MateChatsAdapterCallback {
    fun onChatClicked(chatPreview: MateChatPreview, chatView: View)
}