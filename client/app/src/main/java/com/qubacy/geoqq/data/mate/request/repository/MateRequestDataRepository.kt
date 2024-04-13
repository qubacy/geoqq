package com.qubacy.geoqq.data.mate.request.repository

import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data._common.util.http.executor.executeNetworkRequest
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.request.model.DataMateRequest
import com.qubacy.geoqq.data.mate.request.model.toDataMateRequest
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestCountDataResult
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestsDataResult
import com.qubacy.geoqq.data.mate.request.repository.source.http.HttpMateRequestDataSource
import com.qubacy.geoqq.data.mate.request.repository.source.http.request.PostMateRequestRequest
import com.qubacy.geoqq.data.mate.request.repository.source.http.response.GetMateRequestsResponse
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient

class MateRequestDataRepository(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mErrorDataRepository: ErrorDataRepository,
    private val mTokenDataRepository: TokenDataRepository,
    private val mUserDataRepository: UserDataRepository,
    private val mHttpMateRequestDataSource: HttpMateRequestDataSource,
    private val mHttpClient: OkHttpClient
    // todo: add a websocket source;
) : ProducingDataRepository(coroutineDispatcher, coroutineScope) {
    suspend fun getMateRequests(offset: Int, count: Int): GetMateRequestsDataResult {
        val accessToken = mTokenDataRepository.getTokens().accessToken

        val getMateRequestsCall = mHttpMateRequestDataSource
            .getMateRequests(offset, count, accessToken)
        val getMateRequestsResponse = executeNetworkRequest(
            mErrorDataRepository, mHttpClient, getMateRequestsCall)

        val dataMateRequests = resolveGetMateRequestsResponse(getMateRequestsResponse)

        return GetMateRequestsDataResult(dataMateRequests)
    }

    suspend fun getMateRequestCount(): GetMateRequestCountDataResult {
        val accessToken = mTokenDataRepository.getTokens().accessToken

        val getMateRequestCountCall = mHttpMateRequestDataSource.getMateRequestCount(accessToken)
        val getMateRequestCountResponse = executeNetworkRequest(
            mErrorDataRepository, mHttpClient, getMateRequestCountCall)

        return GetMateRequestCountDataResult(getMateRequestCountResponse.count)
    }

    suspend fun createMateRequest(userId: Long) {
        val accessToken = mTokenDataRepository.getTokens().accessToken

        val postMateRequestRequestBody = PostMateRequestRequest(accessToken, userId)
        val postMateRequestCall = mHttpMateRequestDataSource
            .postMateRequest(postMateRequestRequestBody)

        executeNetworkRequest(mErrorDataRepository, mHttpClient, postMateRequestCall)
    }

    suspend fun answerMateRequest(id: Long, isAccepted: Boolean) {
        val accessToken = mTokenDataRepository.getTokens().accessToken

        val answerMateRequestCall = mHttpMateRequestDataSource
            .answerMateRequest(id, accessToken, isAccepted)

        executeNetworkRequest(mErrorDataRepository, mHttpClient, answerMateRequestCall)
    }

    private suspend fun resolveGetMateRequestsResponse(
        getMateRequestsResponse: GetMateRequestsResponse
    ) : List<DataMateRequest> {
        val userIds = getMateRequestsResponse.requests.map { it.userId }.toSet().toList()
        val userIdUserMap = mUserDataRepository.resolveUsers(userIds)

        return getMateRequestsResponse.requests.map {
            it.toDataMateRequest(userIdUserMap[it.userId]!!)
        }
    }
}