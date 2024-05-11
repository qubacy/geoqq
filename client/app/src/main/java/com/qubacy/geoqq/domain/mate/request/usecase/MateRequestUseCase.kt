package com.qubacy.geoqq.domain.mate.request.usecase

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data.mate.request.repository.impl.MateRequestDataRepositoryImpl
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain._common.usecase.authorized.AuthorizedUseCase
import com.qubacy.geoqq.domain._common.usecase.authorized.error.middleware.authorizedErrorMiddleware
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import com.qubacy.geoqq.domain.mate.request.usecase.result.AnswerMateRequestDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase.result.SendMateRequestDomainResult
import javax.inject.Inject

class MateRequestUseCase @Inject constructor(
    errorSource: LocalErrorDatabaseDataSourceImpl,
    private val mLogoutUseCase: LogoutUseCase,
    private val mMateRequestDataRepository: MateRequestDataRepositoryImpl
) : UseCase(mErrorSource = errorSource), AuthorizedUseCase {
    fun sendMateRequest(interlocutorId: Long) {
        executeLogic({
            mMateRequestDataRepository.createMateRequest(interlocutorId)

            mResultFlow.emit(SendMateRequestDomainResult())

        }, {
            SendMateRequestDomainResult(error = it)
        }, ::authorizedErrorMiddleware)
    }

    fun answerMateRequest(requestId: Long, isAccepted: Boolean) {
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