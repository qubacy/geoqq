package com.qubacy.geoqq.domain._common.usecase._common

import com.qubacy.geoqq._common.coroutine.CoroutineUser
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase._common.result.error.ErrorDomainResult
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
    protected val mResultFlow: MutableSharedFlow<DomainResult> = MutableSharedFlow()
    val resultFlow: SharedFlow<DomainResult> get() = mResultFlow

    protected fun executeLogic(logicAction: suspend () -> Unit) {
        mCoroutineScope.launch(mCoroutineDispatcher) {
            try {
                logicAction()

            } catch (e: ErrorAppException) {
                mResultFlow.emit(ErrorDomainResult(e.error))

            } catch (e: Exception) {
                e.printStackTrace()

                throw e
            }
        }
    }
}