package com.qubacy.geoqq.data._common.repository.producing

import android.util.Log
import com.qubacy.geoqq.data._common.repository.adjustable.AdjustableDataRepository
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data._common.repository.producing.source.ProducingDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

abstract class ProducingDataRepository(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
) : AdjustableDataRepository(coroutineDispatcher, coroutineScope) {
    companion object {
        const val TAG = "ProducingDataRepository"
    }

    protected val mResultFlow: MutableSharedFlow<DataResult> = MutableSharedFlow()
    open val resultFlow: Flow<DataResult> get() = mResultFlow

    open fun getProducingDataSources(): Array<ProducingDataSource> {
        return arrayOf()
    }

    open fun startProducingUpdates() {
        Log.d(TAG, "startProducingUpdates(): class = ${this.javaClass.simpleName};")

        val producingDataSources = getProducingDataSources()

        Log.d(TAG, "startProducingUpdates(): sources = ${producingDataSources.map { it.javaClass.simpleName + ' '}}")

        for (producingDataSource in producingDataSources)
            producingDataSource.startProducing()
    }

    open fun stopProducingUpdates() {
        val producingDataSources = getProducingDataSources()

        for (producingDataSource in producingDataSources)
            producingDataSource.stopProducing()
    }
}