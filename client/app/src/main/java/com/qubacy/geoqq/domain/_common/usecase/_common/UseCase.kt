package com.qubacy.geoqq.domain._common.usecase._common

import com.qubacy.geoqq._common.coroutine.CoroutineUser
import com.qubacy.geoqq._common.error.Error
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

abstract class UseCase(
    protected val mErrorDataRepository: ErrorDataRepository,
    mCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    mCoroutineScope: CoroutineScope = CoroutineScope(mCoroutineDispatcher)
) : CoroutineUser(mCoroutineDispatcher, mCoroutineScope) {
    companion object {
        const val TAG = "UseCase"
    }

    protected val mResultFlow: MutableSharedFlow<DomainResult> = MutableSharedFlow()
    val resultFlow: SharedFlow<DomainResult> get() = mResultFlow

    protected fun <ResultType : DomainResult>executeLogic(
        logicAction: suspend () -> Unit,
        errorResultProducer: (error: Error) -> ResultType
    ) {
        mCoroutineScope.launch(mCoroutineDispatcher) {
            try {
                logicAction()

            } catch (e: ErrorAppException) {
                mResultFlow.emit(errorResultProducer(e.error))

            } catch (e: Exception) {
                e.printStackTrace()

                throw e
            }
        }
    }
}