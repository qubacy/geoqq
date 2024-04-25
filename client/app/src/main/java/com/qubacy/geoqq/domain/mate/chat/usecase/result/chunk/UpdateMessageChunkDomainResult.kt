package com.qubacy.geoqq.domain.mate.chat.usecase.result.chunk

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain.mate.chat.projection.MateMessageChunk
import com.qubacy.geoqq.domain.mate.chat.usecase.result.chunk.common.MessageChunkDomainResult

class UpdateMessageChunkDomainResult(
    error: Error? = null,
    chunk: MateMessageChunk? = null
) : MessageChunkDomainResult(error, chunk) {

}