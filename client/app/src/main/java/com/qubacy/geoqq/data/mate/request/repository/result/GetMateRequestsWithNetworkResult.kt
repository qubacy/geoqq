package com.qubacy.geoqq.data.mate.request.repository.result

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.mate.request.model.DataMateRequest

class GetMateRequestsWithNetworkResult(
    val mateRequests: List<DataMateRequest>
) : Result() {

}