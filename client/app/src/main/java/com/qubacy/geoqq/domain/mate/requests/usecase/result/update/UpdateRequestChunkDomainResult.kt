package com.qubacy.geoqq.domain.mate.requests.usecase.result.update

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain.mate.requests.projection.MateRequestChunk
import com.qubacy.geoqq.domain.mate.requests.usecase.result._common.RequestChunkDomainResult

class UpdateRequestChunkDomainResult(
    error: Error? = null,
    chunk: MateRequestChunk? = null
) : RequestChunkDomainResult(error, chunk) {

}