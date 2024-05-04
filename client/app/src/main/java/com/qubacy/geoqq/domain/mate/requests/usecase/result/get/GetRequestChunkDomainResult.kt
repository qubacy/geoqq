package com.qubacy.geoqq.domain.mate.requests.usecase.result.get

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.mate.requests.projection.MateRequestChunk
import com.qubacy.geoqq.domain.mate.requests.usecase.result._common.RequestChunkDomainResult

class GetRequestChunkDomainResult(
    error: Error? = null,
    chunk: MateRequestChunk? = null
) : RequestChunkDomainResult(error, chunk) {

}