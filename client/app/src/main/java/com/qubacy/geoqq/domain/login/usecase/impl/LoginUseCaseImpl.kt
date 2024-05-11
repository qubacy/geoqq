package com.qubacy.geoqq.domain.login.usecase.impl

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.auth.repository._common.AuthDataRepository
import com.qubacy.geoqq.domain.login.usecase._common.LoginUseCase
import com.qubacy.geoqq.domain.login.usecase._common.result.SignedInDomainResult
import javax.inject.Inject

class LoginUseCaseImpl @Inject constructor(
    localErrorDataSource: LocalErrorDatabaseDataSource,
    private var mAuthDataRepository: AuthDataRepository
) : LoginUseCase(localErrorDataSource) {
    override fun signIn() {
        executeLogic({
            mAuthDataRepository.signIn()
            mResultFlow.emit(SignedInDomainResult())
        }, { SignedInDomainResult(it) })
    }

    override fun signIn(login: String, password: String) {
        executeLogic({
            mAuthDataRepository.signIn(login, password)
            mResultFlow.emit(SignedInDomainResult())
        }, { SignedInDomainResult(it) })
    }

    override fun signUp(login: String, password: String) {
        executeLogic({
            mAuthDataRepository.signUp(login, password)
            mResultFlow.emit(SignedInDomainResult())
        }, { SignedInDomainResult(it) })
    }
}