package com.qubacy.geoqq.domain.common

import com.qubacy.geoqq.common.error.common.TypedErrorBase
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.data.common.operation.Operation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.atomic.AtomicBoolean

abstract class UseCase<StateType>(

) {
    protected val mStateFlow = MutableStateFlow<StateType?>(null)
    val stateFlow: StateFlow<StateType?> = mStateFlow

    protected val mInterruptionFlag = AtomicBoolean(false)

    protected abstract fun generateErrorState(operations: List<Operation>): StateType

    protected suspend fun processError(error: TypedErrorBase) {
        val operations = listOf(
            HandleErrorOperation(error)
        )
        val state = generateErrorState(operations)

        mStateFlow.emit(state)
    }

    fun interruptOperation() {
        mInterruptionFlag.set(true)
    }
}