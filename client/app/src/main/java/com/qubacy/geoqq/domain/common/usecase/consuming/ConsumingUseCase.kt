package com.qubacy.geoqq.domain.common.usecase.consuming

import android.util.Log
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.network.flowable.FlowableDataRepository
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain.common.usecase.common.UseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

abstract class ConsumingUseCase<StateType>(
    errorDataRepository: ErrorDataRepository,
    val flowableDataRepositories: List<FlowableDataRepository>
) : UseCase<StateType>(errorDataRepository) {
    companion object {
        const val TAG = "ConsumingUseCase"
    }

    private val mOriginalFlowableRepositoryFlowJobList = mutableListOf<Job>()
    protected val mStateMutex = Mutex()

    init {
        startFlowableRepositoryFlowCollection()
    }

    private fun startFlowableRepositoryFlowCollection() {
        mOriginalFlowableRepositoryFlowJobList.clear()

        for (flowableDataRepository in flowableDataRepositories) {
            val originalFlowableRepositoryFlowJob = mCoroutineScope.launch(Dispatchers.IO) {
                flowableDataRepository.resultFlow.collect {
                    processResult(it)
                }
            }

            mOriginalFlowableRepositoryFlowJobList.add(originalFlowableRepositoryFlowJob)
        }
    }

    override fun setCoroutineScope(coroutineScope: CoroutineScope) {
        super.setCoroutineScope(coroutineScope)

        for (originalFlowableRepositoryFlowJob in mOriginalFlowableRepositoryFlowJobList) {
            originalFlowableRepositoryFlowJob.cancel()
        }

        startFlowableRepositoryFlowCollection()
    }

    protected open suspend fun processResult(result: Result): Boolean {
        when (result::class) {
            InterruptionResult::class -> {
                val interruptionResult = result as InterruptionResult

                processInterruption()
            }
            ErrorResult::class -> {
                val errorResult = result as ErrorResult

                processError(errorResult.errorId)
            }
            else -> { return false }
        }

        return true
    }

    protected suspend fun lockLastState(): StateType? {
        mStateMutex.lock()

        return stateFlow.value
    }

    protected suspend fun postState(state: StateType) {
        mStateFlow.emit(state)

        mStateMutex.unlock()
    }
}