package com.qubacy.geoqq.domain.common

import com.qubacy.geoqq.common.error.common.Error
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.common.repository.common.DataRepository
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.error.repository.result.GetErrorForLanguageResult
import com.qubacy.geoqq.domain.common.operation.InterruptOperation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

abstract class UseCase<StateType>(
    val errorDataRepository: ErrorDataRepository
) {
    protected val mStateFlow = MutableStateFlow<StateType?>(null)
    val stateFlow: StateFlow<StateType?> = mStateFlow

    protected val mInterruptionFlag = AtomicBoolean(false)
    protected var mCurrentRepository: DataRepository? = null

    protected abstract fun generateState(operations: List<Operation>): StateType

    suspend fun getError(errorId: Long): Error {
        val languageCode = Locale.getDefault().language
        val result = errorDataRepository.getErrorForLanguage(errorId, languageCode)

        // todo: error processing?..

        return (result as GetErrorForLanguageResult).error
    }

    protected suspend fun processError(errorId: Long) {
        val error = getError(errorId)

        val operations = listOf(
            HandleErrorOperation(error)
        )
        val state = generateState(operations)

        mStateFlow.emit(state)
    }

    protected suspend fun processInterruption() {
        mInterruptionFlag.set(false)

        val operations = listOf(
            InterruptOperation()
        )
        val state = generateState(operations)

        mStateFlow.emit(state)
    }

    fun interruptOperation() {
        mInterruptionFlag.set(true)

        mCurrentRepository?.let { it.interrupt() }
    }
}