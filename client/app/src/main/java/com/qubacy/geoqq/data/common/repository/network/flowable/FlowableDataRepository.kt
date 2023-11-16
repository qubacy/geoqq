package com.qubacy.geoqq.data.common.repository.network.flowable

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.network.common.NetworkDataRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

abstract class FlowableDataRepository(

) : NetworkDataRepository() {
    companion object {
        const val RESULT_FLOW_BUFFER_CAPACITY = 16

        private fun generateResultFlow(): MutableSharedFlow<Result> {
            return MutableSharedFlow(
                extraBufferCapacity = RESULT_FLOW_BUFFER_CAPACITY,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )
        }
    }

    protected var mResultFlow = generateResultFlow()
    val resultFlow: SharedFlow<Result> get() = mResultFlow

    override fun reset() {
        super.reset()

        // todo: should we drop the containing values?
        //mResultFlow.resetReplayCache()
        mResultFlow = generateResultFlow()
    }

    protected suspend fun emitResult(result: Result) {
        mResultFlow.emit(result)
    }
}