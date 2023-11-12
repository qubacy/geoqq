package com.qubacy.geoqq.domain.common.usecase.consuming

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

abstract class ConsumingUseCase<StateType>(
    errorDataRepository: ErrorDataRepository,
    val flowableDataRepository: FlowableDataRepository
) : UseCase<StateType>(errorDataRepository) {
    private lateinit var mOriginalFlowableRepositoryFlowJob: Job

    init {
        startFlowableRepositoryFlowCollection()
    }

    private fun startFlowableRepositoryFlowCollection() {
        mOriginalFlowableRepositoryFlowJob = mCoroutineScope.launch(Dispatchers.IO) {
            flowableDataRepository.resultFlow.collect {
                processResult(it)
            }
        }
    }

    override fun setCoroutineScope(coroutineScope: CoroutineScope) {
        super.setCoroutineScope(coroutineScope)

        mOriginalFlowableRepositoryFlowJob.cancel()
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
}