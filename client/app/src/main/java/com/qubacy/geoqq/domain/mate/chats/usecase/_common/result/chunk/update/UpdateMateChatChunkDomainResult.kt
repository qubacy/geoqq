package com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk.update

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain.mate.chats.projection.MateChatChunk
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk._common.MateChatChunkDomainResult

class UpdateMateChatChunkDomainResult(
    error: Error? = null,
    chunk: MateChatChunk? = null
) : MateChatChunkDomainResult(error, chunk) {

}