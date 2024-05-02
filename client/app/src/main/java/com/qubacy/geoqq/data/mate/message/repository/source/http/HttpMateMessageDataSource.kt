package com.qubacy.geoqq.data.mate.message.repository.source.http

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import com.qubacy.geoqq.data._common.repository.message.source.remote.http.response.GetMessagesResponse
import com.qubacy.geoqq.data.mate.message.repository.source.http.api.HttpMateMessageDataSourceApi
import com.qubacy.geoqq.data.mate.message.repository.source.http.api.request.SendMateMessageRequest
import javax.inject.Inject

class HttpMateMessageDataSource @Inject constructor(
    private val mHttpMateMessageDataSourceApi: HttpMateMessageDataSourceApi,
    private val mHttpCallExecutor: HttpCallExecutor
) {
    fun getMateMessages(
        chatId: Long,
        offset: Int,
        count: Int
    ): GetMessagesResponse {
        val getMateMessagesCall = mHttpMateMessageDataSourceApi.getMateMessages(chatId, offset, count)
        val getMateMessagesResponse = mHttpCallExecutor.executeNetworkRequest(getMateMessagesCall)

        return getMateMessagesResponse
    }

    fun sendMateMessage(
        chatId: Long,
        text: String
    ) {
        val sendMateMessageRequest = SendMateMessageRequest(text)
        val sendMateMessageCall = mHttpMateMessageDataSourceApi
            .sendMateMessage(chatId, sendMateMessageRequest)

        mHttpCallExecutor.executeNetworkRequest(sendMateMessageCall)
    }
}