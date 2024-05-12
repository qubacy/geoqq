package com.qubacy.geoqq.data.mate.message.repository._common._test.context

import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.qubacy.geoqq.data._common.repository.message.source.remote.http.response.GetMessageResponse
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity.MateMessageEntity
import com.qubacy.geoqq.data.user.repository._common._test.context.UserDataRepositoryTestContext

object MateMessageDataRepositoryTestContext {
    private val DEFAULT_DATA_USER = UserDataRepositoryTestContext.DEFAULT_DATA_USER

    val DEFAULT_MESSAGE_ENTITY = MateMessageEntity(
        0, 0, DEFAULT_DATA_USER.id, "local message", 0)
    val DEFAULT_GET_MESSAGE_RESPONSE =
        GetMessageResponse(0, DEFAULT_DATA_USER.id, "remote one", 0)

    val DEFAULT_DATA_MESSAGE = DataMessage(0L, DEFAULT_DATA_USER, "test", 0L)
}