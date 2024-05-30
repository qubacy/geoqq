package com.qubacy.geoqq.domain.user.usecase._common

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain._common.usecase._common.error.middleware.ErrorMiddleware
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.AuthorizedUseCase
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.error.middleware.AuthorizedErrorMiddleware
import com.qubacy.geoqq.domain._common.usecase.base._common.UseCase

abstract class UserUseCase(
    errorSource: LocalErrorDatabaseDataSource
) : UseCase(mErrorSource = errorSource), AuthorizedUseCase {
    override fun generateErrorMiddlewares(): Array<ErrorMiddleware> {
        return arrayOf(AuthorizedErrorMiddleware(this))
    }

    abstract fun getUser(userId: Long)
}