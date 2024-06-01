package com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket._common.event.type

enum class MateChatEventType(
    val title: String
) {
    MATE_CHAT_UPDATED_EVENT_TYPE_NAME("updated_mate_chat"),
    MATE_CHAT_ADDED_EVENT_TYPE_NAME("added_mate_chat");
}