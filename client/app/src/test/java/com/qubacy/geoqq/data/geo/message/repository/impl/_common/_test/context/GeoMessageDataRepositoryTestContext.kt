package com.qubacy.geoqq.data.geo.message.repository.impl._common._test.context

import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.qubacy.geoqq.data._common.repository.message.source.remote.http.response.GetMessageResponse
import com.qubacy.geoqq.data.user.repository._common._test.context.UserDataRepositoryTestContext

object GeoMessageDataRepositoryTestContext {
    private val DEFAULT_DATA_USER = UserDataRepositoryTestContext.DEFAULT_DATA_USER

    val DEFAULT_GET_MESSAGE_RESPONSE = GetMessageResponse(0L, 0L, "test 1", 0L)
    val DEFAULT_DATA_MESSAGE = DataMessage(0L, DEFAULT_DATA_USER, "test", 0L)
}