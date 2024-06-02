package com.qubacy.geoqq.domain.mate.chat.usecase._common.update.handler

import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data.mate.message.repository._common.result.added.MateMessageAddedDataResult
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.handler.DataUpdateHandler
import com.qubacy.geoqq.domain.mate.chat.usecase._common.MateChatUseCase

class MateChatDataUpdateHandler(
    mateChatUseCase: MateChatUseCase
) : DataUpdateHandler<MateChatUseCase>(mateChatUseCase) {
    override fun handle(dataUpdate: DataResult): DomainResult? {
        return when (dataUpdate::class) {
            MateMessageAddedDataResult::class ->
                mUseCase.processMateMessageAddedDataResult(dataUpdate as MateMessageAddedDataResult)
            else -> null
        }
    }
}