package com.qubacy.geoqq.domain.common

import com.qubacy.geoqq.common.error.common.TypedErrorBase
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.common.repository.common.DataRepository
import com.qubacy.geoqq.domain.common.operation.InterruptOperation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.atomic.AtomicBoolean

abstract class UseCase<StateType>(

) {
    protected val mStateFlow = MutableStateFlow<StateType?>(null)
    val stateFlow: StateFlow<StateType?> = mStateFlow

    protected val mInterruptionFlag = AtomicBoolean(false)
    protected var mCurrentRepository: DataRepository? = null

    protected abstract fun generateState(operations: List<Operation>): StateType

    protected suspend fun processError(error: TypedErrorBase) {
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