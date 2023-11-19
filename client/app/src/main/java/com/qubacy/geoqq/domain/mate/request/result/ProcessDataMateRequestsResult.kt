package com.qubacy.geoqq.domain.mate.request.result

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.domain.mate.request.model.MateRequest

class ProcessDataMateRequestsResult(
    val mateRequests: List<MateRequest>
) : Result() {

}