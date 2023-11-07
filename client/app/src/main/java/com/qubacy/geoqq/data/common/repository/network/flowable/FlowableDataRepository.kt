package com.qubacy.geoqq.data.common.repository.network.flowable

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.network.common.NetworkDataRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

abstract class FlowableDataRepository(

) : NetworkDataRepository() {
    protected val mResultFlow = MutableSharedFlow<Result>()
    val resultFlow: SharedFlow<Result> = mResultFlow

    protected suspend fun emitResult(result: Result) {
        mResultFlow.emit(result)
    }
}