package com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common

import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.response.GetMateRequestCountResponse
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.response.GetMateRequestsResponse

interface RemoteMateRequestHttpRestDataSource {
    fun getMateRequests(offset: Int, count: Int): GetMateRequestsResponse
    fun getMateRequestCount(): GetMateRequestCountResponse
    fun postMateRequest(userId: Long)
    fun answerMateRequest(id: Long, accepted: Boolean)
}