package com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common

import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.api.response.GetChatResponse
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.api.response.GetChatsResponse

interface RemoteMateChatHttpRestDataSource {
    fun getChats(offset: Int, count: Int): GetChatsResponse
    fun getChat(id: Long): GetChatResponse
    fun deleteChat(id: Long)
}