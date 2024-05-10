package com.qubacy.geoqq.data.mate.request.repository.source.http

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data.mate.request.repository.source.http.api.HttpMateRequestDataSourceApi
import com.qubacy.geoqq.data.mate.request.repository.source.http.api.request.PostMateRequestRequest
import com.qubacy.geoqq.data.mate.request.repository.source.http.api.response.GetMateRequestCountResponse
import com.qubacy.geoqq.data.mate.request.repository.source.http.api.response.GetMateRequestsResponse
import javax.inject.Inject

class HttpMateRequestDataSource @Inject constructor(
    private val mMateRequestDataSourceApi: HttpMateRequestDataSourceApi,
    private val mHttpCallExecutor: HttpCallExecutor
) {
    fun getMateRequests(
        offset: Int,
        count: Int
    ): GetMateRequestsResponse {
        val getMateRequestsCall = mMateRequestDataSourceApi.getMateRequests(offset, count)
        val getMateRequestsResponse = mHttpCallExecutor.executeNetworkRequest(getMateRequestsCall)

        return getMateRequestsResponse
    }

    fun getMateRequestCount(): GetMateRequestCountResponse {
        val getMateRequestCountCall = mMateRequestDataSourceApi.getMateRequestCount()
        val getMateRequestCountResponse = mHttpCallExecutor
            .executeNetworkRequest(getMateRequestCountCall)

        return getMateRequestCountResponse
    }

    fun postMateRequest(userId: Long) {
        val postMateRequestRequest = PostMateRequestRequest(userId)
        val postMateRequestCall = mMateRequestDataSourceApi.postMateRequest(postMateRequestRequest)

        mHttpCallExecutor.executeNetworkRequest(postMateRequestCall)
    }

    fun answerMateRequest(
        id: Long,
        accepted: Boolean
    ) {
        val answerMateRequestCall = mMateRequestDataSourceApi.answerMateRequest(id, accepted)

        mHttpCallExecutor.executeNetworkRequest(answerMateRequestCall)
    }
}