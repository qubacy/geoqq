package com.qubacy.geoqq.data.common.util.generator

import com.qubacy.geoqq.data.mate.request.model.DataMateRequest

object DataMateRequestGeneratorUtility {
    fun generateDataMateRequests(count: Int, startId: Long = 0L): List<DataMateRequest> {
        return LongRange(startId, startId + count - 1).map {
            DataMateRequest(it, 0L)
        }
    }
}