package com.qubacy.geoqq.data.mate.request.repository._common

import androidx.lifecycle.LiveData
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data.mate.request.repository._common.result.GetMateRequestCountDataResult
import com.qubacy.geoqq.data.mate.request.repository._common.result.GetMateRequestsDataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class MateRequestDataRepository(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher)
) : ProducingDataRepository(coroutineDispatcher, coroutineScope) {
    abstract suspend fun getMateRequests(
        offset: Int,
        count: Int
    ): LiveData<GetMateRequestsDataResult>
    abstract fun getMateRequestCount(): GetMateRequestCountDataResult
    abstract fun createMateRequest(userId: Long)
    abstract fun answerMateRequest(id: Long, isAccepted: Boolean)
}