package com.qubacy.geoqq.data.common.repository.network.flowable

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.network.common.NetworkDataRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

abstract class FlowableDataRepository(

) : NetworkDataRepository() {
    companion object {
        const val RESULT_FLOW_BUFFER_CAPACITY = 8
    }

    protected val mResultFlow = MutableSharedFlow<Result>(
        extraBufferCapacity = RESULT_FLOW_BUFFER_CAPACITY,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val resultFlow: SharedFlow<Result> = mResultFlow

    protected suspend fun emitResult(result: Result) {
        mResultFlow.emit(result)
    }
}