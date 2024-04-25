package com.qubacy.geoqq.domain.mate.requests.usecase.result

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.mate.requests.projection.MateRequestChunk

class GetRequestChunkDomainResult(
    error: Error? = null,
    val chunk: MateRequestChunk? = null
) : DomainResult(error) {

}