package com.qubacy.geoqq.domain.login.usecase

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain.login.usecase.result.SignedInDomainResult
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    errorDataRepository: ErrorDataRepository,
    private var mTokenDataRepository: TokenDataRepository
) : UseCase(mErrorDataRepository = errorDataRepository) {
    fun signIn() {
        executeLogic({
            mTokenDataRepository.signIn()
            mResultFlow.emit(SignedInDomainResult())
        }) { SignedInDomainResult(it) }
    }

    fun signIn(login: String, password: String) {
        executeLogic({
            mTokenDataRepository.signIn(login, password)
            mResultFlow.emit(SignedInDomainResult())
        }) { SignedInDomainResult(it) }
    }

    fun signUp(login: String, password: String) {
        executeLogic({
            mTokenDataRepository.signUp(login, password)
            mResultFlow.emit(SignedInDomainResult())
        }) { SignedInDomainResult(it) }
    }
}