package com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chunk

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain.mate.chat.projection.MateMessageChunk
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chunk.common.MessageChunkDomainResult

class GetMessageChunkDomainResult(
    error: Error? = null,
    chunk: MateMessageChunk? = null
) : MessageChunkDomainResult(error, chunk) {

}