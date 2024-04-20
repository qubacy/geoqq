package com.qubacy.geoqq.domain.mate.request.usecase

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain.mate.request.usecase.result.AnswerMateRequestDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase.result.SendMateRequestDomainResult
import javax.inject.Inject

class MateRequestUseCase @Inject constructor(
    errorDataRepository: ErrorDataRepository,
    private val mMateRequestDataRepository: MateRequestDataRepository
) : UseCase(mErrorDataRepository = errorDataRepository) {
    fun sendMateRequest(interlocutorId: Long) {
        executeLogic({
            mMateRequestDataRepository.createMateRequest(interlocutorId)

            mResultFlow.emit(SendMateRequestDomainResult())

        }) {
            SendMateRequestDomainResult(error = it)
        }
    }

    fun answerMateRequest(requestId: Long, isAccepted: Boolean) {
        executeLogic({
            mMateRequestDataRepository.answerMateRequest(requestId, isAccepted)

            mResultFlow.emit(AnswerMateRequestDomainResult(requestId = requestId))

        }) {
            AnswerMateRequestDomainResult(requestId = requestId, error = it)
        }
    }
}