package com.qubacy.geoqq.domain.common.usecase.common

import com.qubacy.geoqq.common.error.common.Error
import com.qubacy.geoqq.domain.common.operation.error.HandleErrorOperation
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.data.common.repository.common.DataRepository
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.error.repository.result.GetErrorForLanguageResult
import com.qubacy.geoqq.domain.common.operation.interrupt.InterruptOperation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

abstract class UseCase<StateType>(
    val errorDataRepository: ErrorDataRepository
) {
    protected var mCoroutineScope: CoroutineScope = GlobalScope

    protected val mStateFlow = MutableStateFlow<StateType?>(null)
    val stateFlow: StateFlow<StateType?> = mStateFlow

    protected val mInterruptionFlag = AtomicBoolean(false)
    protected var mCurrentRepository: DataRepository? = null

    open fun setCoroutineScope(coroutineScope: CoroutineScope) {
        mCoroutineScope = coroutineScope
    }

    protected abstract fun generateState(
        operations: List<Operation>,
        prevState: StateType? = null
    ): StateType

    // todo: think of this:
    suspend fun getError(errorId: Long): Error {
        val languageCode = Locale.getDefault().language
        val result = errorDataRepository.getErrorForLanguage(errorId, languageCode)

        // todo: error processing?..

        return (result as GetErrorForLanguageResult).error
    }

    protected fun processError(errorId: Long) {
        mCoroutineScope.launch(Dispatchers.IO) {
            val error = getError(errorId)

            val operations = listOf(
                HandleErrorOperation(error)
            )
            val state = generateState(operations)

            mStateFlow.emit(state)
        }
    }

    protected fun processInterruption() {
        mCoroutineScope.launch(Dispatchers.IO) {
            mInterruptionFlag.set(false)

            val operations = listOf(
                InterruptOperation()
            )
            val state = generateState(operations)

            mStateFlow.emit(state)
        }
    }

    fun interruptOperation() {
        mInterruptionFlag.set(true)

        mCurrentRepository?.let { it.interrupt() }
    }
}