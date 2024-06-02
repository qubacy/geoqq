package com.qubacy.geoqq.domain.mate.requests.usecase._common.update.handler

import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data.mate.request.repository._common.result.added.MateRequestAddedDataResult
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.handler.DataUpdateHandler
import com.qubacy.geoqq.domain.mate.requests.usecase._common.MateRequestsUseCase

class MateRequestDataUpdateHandler(
    mateRequestsUseCase: MateRequestsUseCase
) : DataUpdateHandler<MateRequestsUseCase>(mateRequestsUseCase) {
    override fun handle(dataUpdate: DataResult): DomainResult? {
        return when (dataUpdate::class) {
            MateRequestAddedDataResult::class ->
                mUseCase.processMateRequestAddedDataResult(dataUpdate as MateRequestAddedDataResult)
            else -> null
        }
    }
}