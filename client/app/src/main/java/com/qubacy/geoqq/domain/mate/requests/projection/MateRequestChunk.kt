package com.qubacy.geoqq.domain.mate.requests.projection

import com.qubacy.geoqq.domain.mate._common.model.request.MateRequest

data class MateRequestChunk(
    val offset: Int,
    val requests: List<MateRequest>
) {

}