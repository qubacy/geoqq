package com.qubacy.geoqq.domain.login.usecase

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.util.extension.signIn
import com.qubacy.geoqq.data.token.repository.util.extension.signUp
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain.login.usecase.result.SignedInDomainResult
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    mErrorDataRepository: ErrorDataRepository,
    private var mTokenDataRepository: TokenDataRepository
) : UseCase(mErrorDataRepository) {
    fun signIn() {
        executeLogic {
            mTokenDataRepository.getTokens()
            mResultFlow.emit(SignedInDomainResult())
        }
    }

    fun signIn(login: String, password: String) {
        executeLogic {
            mTokenDataRepository.signIn(login, password)
            mResultFlow.emit(SignedInDomainResult())
        }
    }

    fun signUp(login: String, password: String) {
        executeLogic {
            mTokenDataRepository.signUp(login, password)
            mResultFlow.emit(SignedInDomainResult())
        }
    }
}