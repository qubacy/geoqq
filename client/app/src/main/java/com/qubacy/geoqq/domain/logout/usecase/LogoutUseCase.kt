package com.qubacy.geoqq.domain.logout.usecase

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data.auth.repository.impl.AuthDataRepositoryImpl
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain.logout.usecase.result.LogoutDomainResult
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    errorSource: LocalErrorDatabaseDataSourceImpl,
    private var mAuthDataRepository: AuthDataRepositoryImpl
) : UseCase(mErrorSource = errorSource) {
    fun logout() {
        executeLogic({
            mAuthDataRepository.logout()

            mResultFlow.emit(LogoutDomainResult())
        }, { LogoutDomainResult(error = it) })
    }
}