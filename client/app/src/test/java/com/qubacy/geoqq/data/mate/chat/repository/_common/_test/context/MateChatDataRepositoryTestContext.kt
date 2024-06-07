package com.qubacy.geoqq.data.mate.chat.repository._common._test.context

import com.qubacy.geoqq.data.mate.chat.model.DataMateChat
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.api.response.GetChatResponse
import com.qubacy.geoqq.data.mate.message.repository._common._test.context.MateMessageDataRepositoryTestContext
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity.MateMessageEntity
import com.qubacy.geoqq.data.user.repository._common._test.context.UserDataRepositoryTestContext

object MateChatDataRepositoryTestContext {
    private val DEFAULT_DATA_USER = UserDataRepositoryTestContext.DEFAULT_DATA_USER
    private val DEFAULT_GET_MESSAGE_RESPONSE = MateMessageDataRepositoryTestContext
        .DEFAULT_GET_MESSAGE_RESPONSE
    private val DEFAULT_LAST_MESSAGE_ENTITY = MateMessageEntity(
        0, 0, DEFAULT_DATA_USER.id, "local message", 0)

    val DEFAULT_MATE_CHAT_ENTITY = MateChatEntity(
        0, DEFAULT_DATA_USER.id, 0, DEFAULT_LAST_MESSAGE_ENTITY.id, 0L)
    val DEFAULT_GET_CHAT_RESPONSE = GetChatResponse(
        0, DEFAULT_DATA_USER.id, 0, DEFAULT_GET_MESSAGE_RESPONSE, 0L
    )

    val DEFAULT_DATA_MATE_CHAT = DataMateChat(
        0L, DEFAULT_DATA_USER, 0,
        MateMessageDataRepositoryTestContext.DEFAULT_DATA_MESSAGE, 0L
    )
}