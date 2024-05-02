package com.qubacy.geoqq.domain.logout.usecase

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.auth.repository.AuthDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain.logout.usecase.result.LogoutDomainResult
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    errorDataRepository: ErrorDataRepository,
    private var mTokenDataRepository: AuthDataRepository
) : UseCase(mErrorDataRepository = errorDataRepository) {
    fun logout() {
        executeLogic({
            mTokenDataRepository.logout()

            mResultFlow.emit(LogoutDomainResult())
        }, { LogoutDomainResult(error = it) })
    }
}