package com.qubacy.geoqq.domain._common.usecase.aspect.user.update.handler

import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data.user.repository._common.result.updated.UserUpdatedDataResult
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.aspect.user.UserAspectUseCase
import com.qubacy.geoqq.domain._common.usecase.base.updatable.UpdatableUseCase
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.handler.DataUpdateHandler

class UserDataUpdateHandler<UserUseCaseType>(
    useCase: UserUseCaseType
) : DataUpdateHandler<UserUseCaseType>(
    useCase
) where UserUseCaseType : UpdatableUseCase, UserUseCaseType : UserAspectUseCase {
    override fun handle(dataUpdate: DataResult): DomainResult? {
        return when (dataUpdate::class) {
            UserUpdatedDataResult::class -> {
                dataUpdate as UserUpdatedDataResult

                mUseCase.processUserUpdatedDataResult(dataUpdate)
            }
            else -> null
        }
    }
}