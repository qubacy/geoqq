package com.qubacy.geoqq.domain.logout.usecase.impl

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.auth.repository._common.AuthDataRepository
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import com.qubacy.geoqq.domain.logout.usecase._common.result.LogoutDomainResult
import javax.inject.Inject

class LogoutUseCaseImpl @Inject constructor(
    errorSource: LocalErrorDatabaseDataSource,
    private var mAuthDataRepository: AuthDataRepository
) : LogoutUseCase(errorSource) {
    override fun logout() {
        executeLogic({
            mAuthDataRepository.logout()

            mResultFlow.emit(LogoutDomainResult())
        }, { LogoutDomainResult(error = it) })
    }
}