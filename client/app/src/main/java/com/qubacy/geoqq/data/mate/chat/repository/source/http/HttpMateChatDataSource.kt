package com.qubacy.geoqq.data.mate.chat.repository.source.http

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import com.qubacy.geoqq.data.mate.chat.repository.source.http.api.HttpMateChatDataSourceApi
import com.qubacy.geoqq.data.mate.chat.repository.source.http.api.response.GetChatResponse
import com.qubacy.geoqq.data.mate.chat.repository.source.http.api.response.GetChatsResponse
import javax.inject.Inject

class HttpMateChatDataSource @Inject constructor(
    private val mHttpMateChatDataSourceApi: HttpMateChatDataSourceApi,
    private val mHttpCallExecutor: HttpCallExecutor
) {
    fun getChats(offset: Int, count: Int): GetChatsResponse {
        val getChatsCall = mHttpMateChatDataSourceApi.getChats(offset, count)
        val getChatsResponse = mHttpCallExecutor.executeNetworkRequest(getChatsCall)

        return getChatsResponse
    }

    fun getChat(id: Long): GetChatResponse {
        val getChatCall = mHttpMateChatDataSourceApi.getChat(id)
        val getChatResponse = mHttpCallExecutor.executeNetworkRequest(getChatCall)

        return getChatResponse
    }

    fun deleteChat(id: Long) {
        val deleteChatCall = mHttpMateChatDataSourceApi.deleteChat(id)

        mHttpCallExecutor.executeNetworkRequest(deleteChatCall)
    }
}
