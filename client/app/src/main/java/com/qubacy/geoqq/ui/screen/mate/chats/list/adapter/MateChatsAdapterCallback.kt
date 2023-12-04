package com.qubacy.geoqq.ui.screen.mate.chats.list.adapter

import android.view.View
import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.mate.chats.model.MateChat
import com.qubacy.geoqq.ui.common.visual.component.animatedlist.adapter.AnimatedListAdapterCallback

interface MateChatsAdapterCallback : AnimatedListAdapterCallback {
    fun getUser(userId: Long): User
    fun onChatClicked(chatPreview: MateChat, chatView: View)
}