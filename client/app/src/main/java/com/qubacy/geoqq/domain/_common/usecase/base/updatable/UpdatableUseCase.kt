package com.qubacy.geoqq.domain._common.usecase.base.updatable

import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.domain._common.usecase.base._common.UseCase
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.handler.DataUpdateHandler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

abstract class UpdatableUseCase @OptIn(ExperimentalCoroutinesApi::class) constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(1), // todo: not good;
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    errorSource: LocalErrorDatabaseDataSource
) : UseCase(coroutineDispatcher, coroutineScope, errorSource) {
    protected val mDataUpdateHandlers: Array<DataUpdateHandler<*>>

    init {
        mDataUpdateHandlers = generateDataUpdateHandlers()
    }

    protected open fun generateDataUpdateHandlers(): Array<DataUpdateHandler<*>> {
        return arrayOf()
    }

    protected open fun getUpdatableRepositories(): Array<ProducingDataRepository> {
        return arrayOf()
    }

    fun stopUpdates() {
        val updatableRepositories = getUpdatableRepositories()

        for (updatableRepository in updatableRepositories)
            updatableRepository.
    }

    override fun onCoroutineScopeSet() {
        super.onCoroutineScopeSet()

        val updatableRepositories = getUpdatableRepositories()
        val updateFlow = merge(*updatableRepositories.map { it.resultFlow }.toTypedArray())

        mCoroutineScope.launch {
            updateFlow.collect {
                processCollectedDataResult(it)
            }
        }
    }

    protected suspend fun processCollectedDataResult(dataResult: DataResult) {
        for (dataUpdateHandler in mDataUpdateHandlers) {
            val domainResult = dataUpdateHandler.handle(dataResult)

            if (domainResult != null) return mResultFlow.emit(domainResult)
        }
    }
}