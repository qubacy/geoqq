package com.qubacy.geoqq.data.mate.request.repository.result

import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data.mate.request.model.DataMateRequest

class GetMateRequestsDataResult(
    val requests: List<DataMateRequest>
) : DataResult {

}