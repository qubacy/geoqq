package com.qubacy.geoqq.domain.login.usecase

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data.auth.repository.impl.AuthDataRepositoryImpl
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain.login.usecase.result.SignedInDomainResult
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    localErrorDataSource: LocalErrorDatabaseDataSourceImpl,
    private var mAuthDataRepository: AuthDataRepositoryImpl
) : UseCase(mErrorSource = localErrorDataSource) {
    fun signIn() {
        executeLogic({
            mAuthDataRepository.signIn()
            mResultFlow.emit(SignedInDomainResult())
        }, { SignedInDomainResult(it) })
    }

    fun signIn(login: String, password: String) {
        executeLogic({
            mAuthDataRepository.signIn(login, password)
            mResultFlow.emit(SignedInDomainResult())
        }, { SignedInDomainResult(it) })
    }

    fun signUp(login: String, password: String) {
        executeLogic({
            mAuthDataRepository.signUp(login, password)
            mResultFlow.emit(SignedInDomainResult())
        }, { SignedInDomainResult(it) })
    }
}