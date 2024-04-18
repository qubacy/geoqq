package com.qubacy.geoqq.domain.mate.requests.usecase

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.InterlocutorUseCase
import com.qubacy.geoqq.domain.mate.request.usecase.MateRequestUseCase
import com.qubacy.geoqq.domain.mate.request.model.toMateRequest
import com.qubacy.geoqq.domain.mate.requests.projection.MateRequestChunk
import com.qubacy.geoqq.domain.mate.requests.usecase.result.GetRequestChunkDomainResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

class MateRequestsUseCase @Inject constructor(
    errorDataRepository: ErrorDataRepository,
    private val mMateRequestUseCase: MateRequestUseCase,
    private val mInterlocutorUseCase: InterlocutorUseCase,
    private val mMateRequestDataRepository: MateRequestDataRepository
) : UseCase(mErrorDataRepository = errorDataRepository) {
    companion object {
        const val DEFAULT_REQUEST_CHUNK_SIZE = 20
    }

    override val resultFlow: Flow<DomainResult> = merge(
        mResultFlow,
        mMateRequestUseCase.resultFlow,
        mInterlocutorUseCase.resultFlow
    )

    fun getRequestChunk(offset: Int) {
        executeLogic({
            val count = DEFAULT_REQUEST_CHUNK_SIZE

            val getRequestsResult = mMateRequestDataRepository.getMateRequests(offset, count)

            val requests = getRequestsResult.requests.map { it.toMateRequest() }
            val requestChunk = MateRequestChunk(offset, requests)

            mResultFlow.emit(GetRequestChunkDomainResult(chunk = requestChunk))

        }) {
            GetRequestChunkDomainResult(error = it)
        }
    }

    fun answerRequest(requestId: Long, isAccepted: Boolean) {
        mMateRequestUseCase.answerMateRequest(requestId, isAccepted)
    }

    fun getInterlocutor(interlocutorId: Long) {
        mInterlocutorUseCase.getInterlocutor(interlocutorId)
    }

    override fun onCoroutineScopeSet() {
        super.onCoroutineScopeSet()

        mMateRequestUseCase.setCoroutineScope(mCoroutineScope)
        mInterlocutorUseCase.setCoroutineScope(mCoroutineScope)
    }
}