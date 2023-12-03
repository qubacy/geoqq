package com.qubacy.geoqq.data.mate.request.repository

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.common.result.ExecuteNetworkRequestResult
import com.qubacy.geoqq.data.common.repository.network.updatable.UpdatableDataRepository
import com.qubacy.geoqq.data.common.repository.network.updatable.source.update.update.Update
import com.qubacy.geoqq.data.mate.request.repository.result.AnswerMateResponseResult
import com.qubacy.geoqq.data.mate.request.repository.result.CreateMateRequestResult
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestCountResult
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestCountWithNetworkResult
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestsResult
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestsWithNetworkResult
import com.qubacy.geoqq.data.mate.request.repository.source.network.NetworkMateRequestDataSource
import com.qubacy.geoqq.data.mate.request.repository.source.network.model.common.toDataMateRequest
import com.qubacy.geoqq.data.mate.request.repository.source.network.model.response.AnswerMateRequestResponse
import com.qubacy.geoqq.data.mate.request.repository.source.network.model.response.CreateMateRequestResponse
import com.qubacy.geoqq.data.mate.request.repository.source.network.model.response.GetMateRequestCountResponse
import com.qubacy.geoqq.data.mate.request.repository.source.network.model.response.GetMateRequestsResponse
import com.qubacy.geoqq.data.mate.request.repository.source.websocket.WebSocketMateRequestDataSource
import retrofit2.Call

open class MateRequestDataRepository(
    val networkMateRequestDataSource: NetworkMateRequestDataSource,
    webSocketUpdateMateRequestDataSource: WebSocketMateRequestDataSource
) : UpdatableDataRepository(webSocketUpdateMateRequestDataSource) {
    companion object {
        const val TAG = "MateRequestDataRep"
    }

    private var mIsInitial = true

    private fun getMateRequestsWithNetwork(
        offset: Int, count: Int, accessToken: String
    ): Result {
        val networkCall = networkMateRequestDataSource
            .getMateRequests(offset, count, accessToken) as Call<Response>
        val networkRequestResult = executeNetworkRequest(networkCall)

        if (networkRequestResult is ErrorResult) return networkRequestResult
        if (networkRequestResult is InterruptionResult) return networkRequestResult

        val networkResponseBody = (networkRequestResult as ExecuteNetworkRequestResult)
            .response as GetMateRequestsResponse

        return GetMateRequestsWithNetworkResult(
            networkResponseBody.requests.map { it.toDataMateRequest() })
    }

    private fun getMateRequestCountWithNetwork(accessToken: String): Result {
        val networkCall = networkMateRequestDataSource
            .getMateRequestCount(accessToken) as Call<Response>
        val networkRequestResult = executeNetworkRequest(networkCall)

        if (networkRequestResult is ErrorResult) return networkRequestResult
        if (networkRequestResult is InterruptionResult) return networkRequestResult

        val networkResponseBody = (networkRequestResult as ExecuteNetworkRequestResult)
            .response as GetMateRequestCountResponse

        return GetMateRequestCountWithNetworkResult(networkResponseBody.count)
    }

    suspend fun getMateRequests(accessToken: String, count: Int, offset: Int, isInit: Boolean) {
        if (mIsInitial) mIsInitial = false

        val getMateRequestsWithNetworkResult = getMateRequestsWithNetwork(offset, count, accessToken)

        if (getMateRequestsWithNetworkResult is ErrorResult)
            return emitResult(getMateRequestsWithNetworkResult)
        if (getMateRequestsWithNetworkResult is InterruptionResult)
            return emitResult(getMateRequestsWithNetworkResult)

        val getMateRequestsWithNetworkResultCast = getMateRequestsWithNetworkResult
                as GetMateRequestsWithNetworkResult

        emitResult(GetMateRequestsResult(getMateRequestsWithNetworkResultCast.mateRequests, isInit))

        if (mIsInitial) {
            val initUpdateSourceResult = initUpdateSource()

            if (initUpdateSourceResult is ErrorResult) return emitResult(initUpdateSourceResult)
        }
    }

    suspend fun getMateRequestCount(accessToken: String): Result {
        val getMateRequestsWithNetworkResult = getMateRequestCountWithNetwork(accessToken)

        if (getMateRequestsWithNetworkResult is ErrorResult) return getMateRequestsWithNetworkResult
        if (getMateRequestsWithNetworkResult is InterruptionResult) return getMateRequestsWithNetworkResult

        val getMateRequestsWithNetworkResultCast = getMateRequestsWithNetworkResult
                as GetMateRequestCountWithNetworkResult

        return GetMateRequestCountResult(getMateRequestsWithNetworkResultCast.count)
    }

    suspend fun createMateRequest(accessToken: String, userId: Long): Result {
        val createMateRequestNetworkCall = networkMateRequestDataSource
            .createMateRequest(accessToken, userId) as Call<Response>
        val createMateRequestNetworkCallResult = executeNetworkRequest(createMateRequestNetworkCall)

        if (createMateRequestNetworkCallResult is ErrorResult)
            return createMateRequestNetworkCallResult
        if (createMateRequestNetworkCallResult is InterruptionResult)
            return createMateRequestNetworkCallResult

        val createMateRequestNetworkResponse = (createMateRequestNetworkCallResult
            as ExecuteNetworkRequestResult).response as CreateMateRequestResponse

        return CreateMateRequestResult()
    }

    suspend fun answerMateRequest(
        accessToken: String, requestId: Long, isAccepted: Boolean
    ): Result {
        val answerMateRequestNetworkCall = networkMateRequestDataSource
            .answerMateRequest(requestId, accessToken, isAccepted) as Call<Response>
        val answerMateRequestNetworkCallResult = executeNetworkRequest(answerMateRequestNetworkCall)

        if (answerMateRequestNetworkCallResult is ErrorResult)
            return answerMateRequestNetworkCallResult
        if (answerMateRequestNetworkCallResult is InterruptionResult)
            return answerMateRequestNetworkCallResult

        val answerMateRequestNetworkResponse = (answerMateRequestNetworkCallResult
                as ExecuteNetworkRequestResult).response as AnswerMateRequestResponse

        return AnswerMateResponseResult()
    }

    override fun processUpdates(updates: List<Update>) {
        TODO("Not yet implemented")
    }
}