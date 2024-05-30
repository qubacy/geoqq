package com.qubacy.geoqq.domain._common.usecase.aspect.authorized.error.middleware

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq._common.model.error.auth.AuthErrorType
import com.qubacy.geoqq.data._common.repository.token.error.type.DataTokenErrorType
import com.qubacy.geoqq.domain._common.usecase._common.error.middleware.ErrorMiddleware
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.AuthorizedUseCase
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.result.error.ErrorWithLogoutDomainResult

class AuthorizedErrorMiddleware(
    private val mAuthorizedUseCase: AuthorizedUseCase
): ErrorMiddleware() {
    override fun processError(
        error: Error,
        errorResultProducer: (error: Error) -> DomainResult
    ): DomainResult {
        if (error.id in listOf(
            AuthErrorType.INVALID_REFRESH_TOKEN.getErrorCode(),
            DataTokenErrorType.LOCAL_REFRESH_TOKEN_INVALID.getErrorCode()
        )) {
            mAuthorizedUseCase.getLogoutUseCase().logout()

            return ErrorWithLogoutDomainResult(error)
        }

        return errorResultProducer(error)
    }
}