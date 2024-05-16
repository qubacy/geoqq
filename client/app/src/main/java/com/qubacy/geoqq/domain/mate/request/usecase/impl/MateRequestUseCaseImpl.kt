package com.qubacy.geoqq.domain.mate.request.usecase.impl

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.mate.request.repository._common.MateRequestDataRepository
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.error.middleware.authorizedErrorMiddleware
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import com.qubacy.geoqq.domain.mate.request.usecase._common.MateRequestUseCase
import com.qubacy.geoqq.domain.mate.request.usecase._common.result.AnswerMateRequestDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase._common.result.SendMateRequestDomainResult
import javax.inject.Inject

class MateRequestUseCaseImpl @Inject constructor(
    errorSource: LocalErrorDatabaseDataSource,
    private val mLogoutUseCase: LogoutUseCase,
    private val mMateRequestDataRepository: MateRequestDataRepository
) : MateRequestUseCase(errorSource) {
    override fun sendMateRequest(interlocutorId: Long) {
        executeLogic({
            mMateRequestDataRepository.createMateRequest(interlocutorId)

            mResultFlow.emit(SendMateRequestDomainResult())

        }, {
            SendMateRequestDomainResult(error = it)
        }, ::authorizedErrorMiddleware)
    }

    override fun answerMateRequest(requestId: Long, isAccepted: Boolean) {
        executeLogic({
            mMateRequestDataRepository.answerMateRequest(requestId, isAccepted)

            mResultFlow.emit(AnswerMateRequestDomainResult(requestId = requestId))

        }, {
            AnswerMateRequestDomainResult(requestId = requestId, error = it)
        }, ::authorizedErrorMiddleware)
    }

    override fun getLogoutUseCase(): LogoutUseCase {
        return mLogoutUseCase
    }
}