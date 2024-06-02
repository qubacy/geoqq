package com.qubacy.geoqq.domain.mate.chats.usecase._common.update.handler

import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data.mate.chat.repository._common.result.added.MateChatAddedDataResult
import com.qubacy.geoqq.data.mate.chat.repository._common.result.updated.MateChatUpdatedDataResult
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.handler.DataUpdateHandler
import com.qubacy.geoqq.domain.mate.chats.usecase._common.MateChatsUseCase

class MateChatsDataUpdateHandler(
    mateChatsUseCase: MateChatsUseCase
) : DataUpdateHandler<MateChatsUseCase>(mateChatsUseCase) {
    override fun handle(dataUpdate: DataResult): DomainResult? {
        return when (dataUpdate::class) {
            MateChatAddedDataResult::class ->
                mUseCase.processMateChatAddedDataResult(dataUpdate as MateChatAddedDataResult)
            MateChatUpdatedDataResult::class ->
                mUseCase.processMateChatUpdatedDataResult(dataUpdate as MateChatUpdatedDataResult)
            else -> null
        }
    }
}