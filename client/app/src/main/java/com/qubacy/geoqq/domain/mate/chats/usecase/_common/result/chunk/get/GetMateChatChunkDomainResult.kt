package com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk.get

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain.mate.chats.projection.MateChatChunk
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk._common.MateChatChunkDomainResult

class GetMateChatChunkDomainResult(
    error: Error? = null,
    chunk: MateChatChunk? = null
) : MateChatChunkDomainResult(error, chunk) {

}