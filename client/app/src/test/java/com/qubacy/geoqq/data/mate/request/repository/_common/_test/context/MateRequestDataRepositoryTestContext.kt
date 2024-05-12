package com.qubacy.geoqq.data.mate.request.repository._common._test.context

import com.qubacy.geoqq.data.mate.request.model.DataMateRequest
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.response.GetMateRequestResponse
import com.qubacy.geoqq.data.user.repository._common._test.context.UserDataRepositoryTestContext

object MateRequestDataRepositoryTestContext {
    private val DEFAULT_DATA_USER = UserDataRepositoryTestContext.DEFAULT_DATA_USER

    val DEFAULT_GET_MATE_REQUEST_RESPONSE = GetMateRequestResponse(0, DEFAULT_DATA_USER.id)
    val DEFAULT_DATA_MATE_REQUEST = DataMateRequest(0L, DEFAULT_DATA_USER)
}