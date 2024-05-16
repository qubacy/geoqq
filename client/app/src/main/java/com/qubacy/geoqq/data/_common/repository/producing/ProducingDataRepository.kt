package com.qubacy.geoqq.data._common.repository.producing

import com.qubacy.geoqq.data._common.repository.adjustable.AdjustableDataRepository
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

abstract class ProducingDataRepository(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
) : AdjustableDataRepository(coroutineDispatcher, coroutineScope) {
    protected val mResultFlow: MutableSharedFlow<DataResult> = MutableSharedFlow()
    open val resultFlow: Flow<DataResult> get() = mResultFlow
}