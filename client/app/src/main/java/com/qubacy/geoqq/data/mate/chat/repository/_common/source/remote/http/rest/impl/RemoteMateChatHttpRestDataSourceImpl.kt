package com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.RemoteMateChatHttpRestDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.api.RemoteMateChatHttpRestDataSourceApi
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.api.response.GetChatResponse
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.api.response.GetChatsResponse
import javax.inject.Inject

class RemoteMateChatHttpRestDataSourceImpl @Inject constructor(
    private val mHttpMateChatDataSourceApi: RemoteMateChatHttpRestDataSourceApi,
    private val mHttpCallExecutor: HttpCallExecutor
) : RemoteMateChatHttpRestDataSource {
    override fun getChats(offset: Int, count: Int): GetChatsResponse {
        val getChatsCall = mHttpMateChatDataSourceApi.getChats(offset, count)
        val getChatsResponse = mHttpCallExecutor.executeNetworkRequest(getChatsCall)

        return getChatsResponse
    }

    override fun getChat(id: Long): GetChatResponse {
        val getChatCall = mHttpMateChatDataSourceApi.getChat(id)
        val getChatResponse = mHttpCallExecutor.executeNetworkRequest(getChatCall)

        return getChatResponse
    }

    override fun deleteChat(id: Long) {
        val deleteChatCall = mHttpMateChatDataSourceApi.deleteChat(id)

        mHttpCallExecutor.executeNetworkRequest(deleteChatCall)
    }
}
