package com.qubacy.geoqq.domain.common.util.generator

import com.qubacy.geoqq.domain.mate.request.model.MateRequest

object MateRequestGeneratorUtility {
    fun generateMateRequests(count: Int, startId: Long = 0L): List<MateRequest> {
        return LongRange(startId, startId + count - 1).map {
            MateRequest(it, 0L)
        }
    }
}