package com.qubacy.geoqq.domain._common.usecase.base.updatable

import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.domain._common.usecase.base._common.UseCase
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.error.handler._common.UpdateErrorHandler
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.error.handler.failure.UpdateFailureErrorHandler
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.handler.DataUpdateHandler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

abstract class UpdatableUseCase @OptIn(ExperimentalCoroutinesApi::class) constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(1), // todo: not good;
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    errorSource: LocalErrorDatabaseDataSource
) : UseCase(coroutineDispatcher, coroutineScope, errorSource) {
    protected val mDataUpdateHandlers: Array<DataUpdateHandler<*>>
    protected val mUpdateErrorHandlers: Array<UpdateErrorHandler>

    init {
        mDataUpdateHandlers = generateDataUpdateHandlers()
        mUpdateErrorHandlers = generateUpdateErrorHandlers()
    }

    protected open fun generateDataUpdateHandlers(): Array<DataUpdateHandler<*>> {
        return arrayOf()
    }

    protected open fun generateUpdateErrorHandlers(): Array<UpdateErrorHandler> {
        return arrayOf(UpdateFailureErrorHandler())
    }

    protected open fun getUpdatableRepositories(): Array<ProducingDataRepository> {
        return arrayOf()
    }

    open fun startUpdates() {
        val updatableRepositories = getUpdatableRepositories()

        for (updatableRepository in updatableRepositories)
            updatableRepository.startProducingUpdates()
    }

    open fun stopUpdates() {
        val updatableRepositories = getUpdatableRepositories()

        for (updatableRepository in updatableRepositories)
            updatableRepository.stopProducingUpdates()
    }

    override fun onCoroutineScopeSet() {
        super.onCoroutineScopeSet()

        val updatableRepositories = getUpdatableRepositories()
        val updateFlow = merge(*updatableRepositories.map { it.resultFlow }.toTypedArray())

        val coroutineExceptionHandler = createUpdateCoroutineExceptionHandler()

        mCoroutineScope.launch(mCoroutineDispatcher + coroutineExceptionHandler) {
            updateFlow.collect {
                processCollectedDataResult(it)
            }
        }
    }

    private fun createUpdateCoroutineExceptionHandler(): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, exception ->
            runBlocking {
                if (exception is ErrorAppException) {
                    for (updateErrorHandler in mUpdateErrorHandlers) {
                        if (updateErrorHandler.handleError(exception.error))
                            return@runBlocking
                    }

                    throw IllegalStateException()
                }

                exception.printStackTrace()

                throw exception
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