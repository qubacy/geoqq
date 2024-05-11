package com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.RemoteMateRequestHttpRestDataSource
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.RemoteMateRequestHttpRestDataSourceApi
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.request.PostMateRequestRequest
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.response.GetMateRequestCountResponse
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.response.GetMateRequestsResponse
import javax.inject.Inject

class RemoteMateRequestHttpRestDataSourceImpl @Inject constructor(
    private val mRemoteMateRequestHttpRestDataSourceApi: RemoteMateRequestHttpRestDataSourceApi,
    private val mHttpCallExecutor: HttpCallExecutor
) : RemoteMateRequestHttpRestDataSource {
    override fun getMateRequests(
        offset: Int,
        count: Int
    ): GetMateRequestsResponse {
        val getMateRequestsCall = mRemoteMateRequestHttpRestDataSourceApi
            .getMateRequests(offset, count)
        val getMateRequestsResponse = mHttpCallExecutor.executeNetworkRequest(getMateRequestsCall)

        return getMateRequestsResponse
    }

    override fun getMateRequestCount(): GetMateRequestCountResponse {
        val getMateRequestCountCall = mRemoteMateRequestHttpRestDataSourceApi.getMateRequestCount()
        val getMateRequestCountResponse = mHttpCallExecutor
            .executeNetworkRequest(getMateRequestCountCall)

        return getMateRequestCountResponse
    }

    override fun postMateRequest(userId: Long) {
        val postMateRequestRequest = PostMateRequestRequest(userId)
        val postMateRequestCall = mRemoteMateRequestHttpRestDataSourceApi
            .postMateRequest(postMateRequestRequest)

        mHttpCallExecutor.executeNetworkRequest(postMateRequestCall)
    }

    override fun answerMateRequest(
        id: Long,
        accepted: Boolean
    ) {
        val answerMateRequestCall = mRemoteMateRequestHttpRestDataSourceApi
            .answerMateRequest(id, accepted)

        mHttpCallExecutor.executeNetworkRequest(answerMateRequestCall)
    }
}