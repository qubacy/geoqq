package com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chunk.common

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.mate.chat.projection.MateMessageChunk

abstract class MessageChunkDomainResult(
    error: Error? = null,
    val chunk: MateMessageChunk? = null
) : DomainResult(error) {

}