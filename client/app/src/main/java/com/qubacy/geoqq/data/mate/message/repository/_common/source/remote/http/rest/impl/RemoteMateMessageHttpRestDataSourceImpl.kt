package com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.impl.HttpCallExecutorImpl
import com.qubacy.geoqq.data._common.repository.message.source.remote.http.response.GetMessagesResponse
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest._common.RemoteMateMessageHttpRestDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest._common.api.RemoteMateMessageHttpRestDataSourceApi
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest._common.api.request.SendMateMessageRequest
import javax.inject.Inject

class RemoteMateMessageHttpRestDataSourceImpl @Inject constructor(
    private val mRemoteMateMessageHttpRestDataSourceApi: RemoteMateMessageHttpRestDataSourceApi,
    private val mHttpCallExecutor: HttpCallExecutorImpl
) : RemoteMateMessageHttpRestDataSource {
    override fun getMateMessages(
        chatId: Long,
        offset: Int,
        count: Int
    ): GetMessagesResponse {
        val getMateMessagesCall = mRemoteMateMessageHttpRestDataSourceApi
            .getMateMessages(chatId, offset, count)
        val getMateMessagesResponse = mHttpCallExecutor.executeNetworkRequest(getMateMessagesCall)

        return getMateMessagesResponse
    }

    override fun sendMateMessage(
        chatId: Long,
        text: String
    ) {
        val sendMateMessageRequest = SendMateMessageRequest(text)
        val sendMateMessageCall = mRemoteMateMessageHttpRestDataSourceApi
            .sendMateMessage(chatId, sendMateMessageRequest)

        mHttpCallExecutor.executeNetworkRequest(sendMateMessageCall)
    }
}