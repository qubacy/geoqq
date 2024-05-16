package com.qubacy.geoqq.domain.login.usecase._common

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain._common.usecase.base._common.UseCase

abstract class LoginUseCase(
    localErrorDataSource: LocalErrorDatabaseDataSource,
) : UseCase(mErrorSource = localErrorDataSource) {
    abstract fun signIn()
    abstract fun signIn(login: String, password: String)
    abstract fun signUp(login: String, password: String)
}