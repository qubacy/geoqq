package com.qubacy.geoqq.data.common.repository.network.updatable

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.network.common.NetworkDataRepository
import com.qubacy.geoqq.data.common.repository.network.updatable.source.update.UpdateDataSource
import com.qubacy.geoqq.data.common.repository.network.updatable.source.update.update.Update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

abstract class UpdatableDataRepository(
    val updateDataSource: UpdateDataSource
) : NetworkDataRepository() {
    protected open suspend fun initUpdateSource(): Result {
        CoroutineScope(coroutineContext).launch {
            updateDataSource.updateFlow.map { processUpdates(it) }
            updateDataSource.startUpdateListening()
        }

        return Result()
    }

    protected abstract fun processUpdates(updates: List<Update>)
}