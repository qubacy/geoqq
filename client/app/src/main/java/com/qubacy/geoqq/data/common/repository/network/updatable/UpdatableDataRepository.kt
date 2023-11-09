package com.qubacy.geoqq.data.common.repository.network.updatable

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.network.flowable.FlowableDataRepository
import com.qubacy.geoqq.data.common.repository.network.updatable.source.update.UpdateDataSource
import com.qubacy.geoqq.data.common.repository.network.updatable.source.update.update.Update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

abstract class UpdatableDataRepository(
    val updateDataSource: UpdateDataSource
) : FlowableDataRepository() {
    protected open suspend fun initUpdateSource(): Result {
        CoroutineScope(coroutineContext).launch(Dispatchers.IO) {
            updateDataSource.startUpdateListening()
            updateDataSource.updateFlow.collect {
                processUpdates(it)
            }
        }

        return Result()
    }

    protected abstract fun processUpdates(updates: List<Update>)
}