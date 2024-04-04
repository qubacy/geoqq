package com.qubacy.geoqq.domain._common.usecase._common

import com.qubacy.geoqq._common.coroutine.CoroutineUser
import com.qubacy.geoqq._common.model.error.Error
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

abstract class UseCase(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    protected val mErrorDataRepository: ErrorDataRepository
) : CoroutineUser(coroutineDispatcher, coroutineScope) {
    companion object {
        const val TAG = "UseCase"
    }

    protected val mResultFlow: MutableSharedFlow<DomainResult> = MutableSharedFlow()
    val resultFlow: SharedFlow<DomainResult> get() = mResultFlow

    protected fun <ResultType : DomainResult>executeLogic(
        logicAction: suspend () -> Unit,
        errorResultProducer: (error: Error) -> ResultType
    ) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            runBlocking {
                if (exception is ErrorAppException)
                    return@runBlocking mResultFlow.emit(errorResultProducer(exception.error))

                exception.printStackTrace()

                throw exception
            }
        }

        mCoroutineScope.launch(mCoroutineDispatcher.plus(exceptionHandler)) {
            logicAction()
        }
    }
}