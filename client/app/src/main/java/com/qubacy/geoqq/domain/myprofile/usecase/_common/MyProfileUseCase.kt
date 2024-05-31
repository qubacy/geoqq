package com.qubacy.geoqq.domain.myprofile.usecase._common

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain._common.usecase._common.error.middleware.ErrorMiddleware
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.AuthorizedUseCase
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.error.middleware.AuthorizedErrorMiddleware
import com.qubacy.geoqq.domain._common.usecase.base.updatable.UpdatableUseCase
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.error.handler._common.UpdateErrorHandler
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.error.handler.common.UpdateCommonErrorHandler
import com.qubacy.geoqq.domain.myprofile.model.update.MyProfileUpdateData

abstract class MyProfileUseCase(
    errorSource: LocalErrorDatabaseDataSource
) : UpdatableUseCase(errorSource = errorSource), AuthorizedUseCase {
    override fun generateErrorMiddlewares(): Array<ErrorMiddleware> {
        return arrayOf(AuthorizedErrorMiddleware(this))
    }

    override fun generateUpdateErrorHandlers(): Array<UpdateErrorHandler> {
        return arrayOf(UpdateCommonErrorHandler(this))
    }

    abstract fun getMyProfile()
    abstract fun updateMyProfile(myProfileUpdateData: MyProfileUpdateData)
    abstract fun deleteMyProfile()
    abstract fun logout()
}