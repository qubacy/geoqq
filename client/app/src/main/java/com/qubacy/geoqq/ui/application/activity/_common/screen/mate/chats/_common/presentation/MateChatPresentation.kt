package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats._common.presentation

import com.qubacy.geoqq.domain.mate.chats.model.MateChat
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.toMateMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.item.data.MateChatItemData

data class MateChatPresentation(
    val id: Long,
    val user: UserPresentation,
    val newMessageCount: Int,
    val lastMessage: MateMessagePresentation?
) {

}

fun MateChat.toMateChatPresentation(): MateChatPresentation {
    return MateChatPresentation(
        id,
        user.toUserPresentation(),
        newMessageCount,
        lastMessage?.toMateMessagePresentation()
    )
}

fun MateChatPresentation.toMateChatItemData(): MateChatItemData {
    val lastMessageText = lastMessage?.text ?: String()

    return MateChatItemData(id, user.avatar.uri, user.username, lastMessageText, newMessageCount)
}