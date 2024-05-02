package com.qubacy.geoqq.data.mate.request.repository

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data.mate.request.model.DataMateRequest
import com.qubacy.geoqq.data.mate.request.model.toDataMateRequest
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestCountDataResult
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestsDataResult
import com.qubacy.geoqq.data.mate.request.repository.source.http.api.response.GetMateRequestsResponse
import com.qubacy.geoqq.data.mate.request.repository.source.http.HttpMateRequestDataSource
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MateRequestDataRepository(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mErrorSource: LocalErrorDataSource,
    private val mUserDataRepository: UserDataRepository,
    private val mHttpMateRequestDataSource: HttpMateRequestDataSource
    // todo: add a websocket source;
) : ProducingDataRepository(coroutineDispatcher, coroutineScope) {
    suspend fun getMateRequests(offset: Int, count: Int): GetMateRequestsDataResult {
        val getMateRequestsResponse = mHttpMateRequestDataSource.getMateRequests(offset, count)

        val dataMateRequests = resolveGetMateRequestsResponse(getMateRequestsResponse)

        return GetMateRequestsDataResult(dataMateRequests)
    }

    fun getMateRequestCount(): GetMateRequestCountDataResult {
        val getMateRequestCountResponse = mHttpMateRequestDataSource.getMateRequestCount()

        return GetMateRequestCountDataResult(getMateRequestCountResponse.count)
    }

    fun createMateRequest(userId: Long) {
        mHttpMateRequestDataSource.postMateRequest(userId)
    }

    fun answerMateRequest(id: Long, isAccepted: Boolean) {
        mHttpMateRequestDataSource.answerMateRequest(id, isAccepted)
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