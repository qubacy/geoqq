package com.qubacy.geoqq.domain.mate.chats.usecase.result.chunk

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain.mate.chats.projection.MateChatChunk
import com.qubacy.geoqq.domain.mate.chats.usecase.result.chunk._common.ChatChunkDomainResult

class GetChatChunkDomainResult(
    error: Error? = null,
    chunk: MateChatChunk? = null
) : ChatChunkDomainResult(error, chunk) {

}