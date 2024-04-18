package com.qubacy.geoqq.domain.mate.requests.projection

import com.qubacy.geoqq.domain.mate.request.model.MateRequest

data class MateRequestChunk(
    val index: Int,
    val requests: List<MateRequest>
) {

}