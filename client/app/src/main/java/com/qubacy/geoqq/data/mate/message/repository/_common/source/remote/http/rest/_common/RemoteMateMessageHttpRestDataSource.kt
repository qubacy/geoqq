package com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest._common

import com.qubacy.geoqq.data._common.repository.message.source.remote.http.response.GetMessagesResponse

interface RemoteMateMessageHttpRestDataSource {
    fun getMateMessages(chatId: Long, offset: Int, count: Int): GetMessagesResponse
    fun sendMateMessage(chatId: Long, text: String)
}