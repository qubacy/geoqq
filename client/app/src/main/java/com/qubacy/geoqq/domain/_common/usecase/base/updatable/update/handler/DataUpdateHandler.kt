package com.qubacy.geoqq.domain._common.usecase.base.updatable.update.handler

import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.base.updatable.UpdatableUseCase

abstract class DataUpdateHandler<UseCaseType : UpdatableUseCase>(
    protected val mUseCase: UseCaseType
) {
    abstract fun handle(dataUpdate: DataResult): DomainResult?
}