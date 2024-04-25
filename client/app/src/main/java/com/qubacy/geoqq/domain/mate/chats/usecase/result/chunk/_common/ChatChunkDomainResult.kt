package com.qubacy.geoqq.domain.mate.chats.usecase.result.chunk._common

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.mate.chats.projection.MateChatChunk

abstract class ChatChunkDomainResult(
    error: Error? = null,
    val chunk: MateChatChunk? = null
) : DomainResult(error) {

}