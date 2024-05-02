package com.qubacy.geoqq.domain.login.usecase

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data.auth.repository.AuthDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain.login.usecase.result.SignedInDomainResult
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    localErrorDataSource: LocalErrorDataSource,
    private var mTokenDataRepository: AuthDataRepository
) : UseCase(mErrorSource = localErrorDataSource) {
    fun signIn() {
        executeLogic({
            mTokenDataRepository.signIn()
            mResultFlow.emit(SignedInDomainResult())
        }, { SignedInDomainResult(it) })
    }

    fun signIn(login: String, password: String) {
        executeLogic({
            mTokenDataRepository.signIn(login, password)
            mResultFlow.emit(SignedInDomainResult())
        }, { SignedInDomainResult(it) })
    }

    fun signUp(login: String, password: String) {
        executeLogic({
            mTokenDataRepository.signUp(login, password)
            mResultFlow.emit(SignedInDomainResult())
        }, { SignedInDomainResult(it) })
    }
}