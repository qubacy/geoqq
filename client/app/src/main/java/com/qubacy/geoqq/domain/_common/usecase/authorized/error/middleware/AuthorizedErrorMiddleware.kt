package com.qubacy.geoqq.domain._common.usecase.authorized.error.middleware

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq._common.model.error.auth.AuthErrorType
import com.qubacy.geoqq.data._common.repository.token.error.type.DataTokenErrorType
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.authorized.AuthorizedUseCase
import com.qubacy.geoqq.domain._common.usecase.authorized.result.error.ErrorWithLogoutDomainResult

fun <ResultType : DomainResult>authorizedErrorMiddleware(
    error: Error,
    errorResultProducer: (error: Error) -> ResultType,
    useCase: UseCase
): DomainResult {
    useCase as AuthorizedUseCase

    //Log.d("TEST", "authorizedErrorMiddleware(): error.code = ${error.id};")

    if (error.id in listOf(
        AuthErrorType.INVALID_REFRESH_TOKEN.getErrorCode(),
        DataTokenErrorType.LOCAL_REFRESH_TOKEN_INVALID.getErrorCode()
    )) {
        useCase.getLogoutUseCase().logout()

        return ErrorWithLogoutDomainResult(error)
    }

    return errorResultProducer(error)
}