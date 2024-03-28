package com.qubacy.geoqq.domain.mate.chats.usecase.result

import com.qubacy.geoqq._common.error.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.mate.chats.projection.MateChatChunk

class GetChatChunkDomainResult(
    error: Error? = null,
    val chunk: MateChatChunk? = null
) : DomainResult(error) {

}