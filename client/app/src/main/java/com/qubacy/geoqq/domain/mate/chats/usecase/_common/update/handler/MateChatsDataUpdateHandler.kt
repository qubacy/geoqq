package com.qubacy.geoqq.domain.mate.chats.usecase._common.update.handler

import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.handler.DataUpdateHandler
import com.qubacy.geoqq.domain.mate.chats.usecase._common.MateChatsUseCase

class MateChatsDataUpdateHandler(
    mateChatsUseCase: MateChatsUseCase
) : DataUpdateHandler<MateChatsUseCase>(mateChatsUseCase) {
    override fun handle(dataUpdate: DataResult): DomainResult? {
        // todo: implement..

        return null
    }
}