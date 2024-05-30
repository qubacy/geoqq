package com.qubacy.geoqq.domain._common.usecase.base._common

import com.qubacy.geoqq._common.coroutine.CoroutineUser
import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain._common.usecase._common.error.middleware.ErrorMiddleware
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

abstract class UseCase @OptIn(ExperimentalCoroutinesApi::class) constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(1), // todo: not good;
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    protected val mErrorSource: LocalErrorDatabaseDataSource
) : CoroutineUser(coroutineDispatcher, coroutineScope) {
    companion object {
        const val TAG = "UseCase"
    }

    protected val mResultFlow: MutableSharedFlow<DomainResult> = MutableSharedFlow()
    open val resultFlow: Flow<DomainResult> get() = mResultFlow

    private val mErrorMiddlewares: Array<ErrorMiddleware>

    init {
        mErrorMiddlewares = generateErrorMiddlewares()
    }

    protected open fun generateErrorMiddlewares(): Array<ErrorMiddleware> {
        return arrayOf(ErrorMiddleware())
    }

    protected fun <ResultType : DomainResult>executeLogic(
        logicAction: suspend () -> Unit,
        errorResultProducer: (error: Error) -> ResultType
    ) {
        val exceptionHandler = createCoroutineExceptionHandler(errorResultProducer)

        mCoroutineScope.launch(mCoroutineDispatcher.plus(exceptionHandler)) {
            logicAction()
        }
    }

    protected fun <ResultType : DomainResult>createCoroutineExceptionHandler(
        errorResultProducer: (error: Error) -> ResultType
    ): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, exception ->
            runBlocking {
                if (exception is ErrorAppException) {
                    for (errorMiddleware in mErrorMiddlewares) {
                        val domainResult = errorMiddleware
                            .processError(exception.error, errorResultProducer) ?: continue

                        return@runBlocking mResultFlow.emit(domainResult)
                    }
                }

                exception.printStackTrace()

                throw exception
            }
        }
    }
}