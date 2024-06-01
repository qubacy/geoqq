package com.qubacy.geoqq.data.mate.request.repository._common.result.get

import com.qubacy.geoqq.data._common.repository.producing.result.ProducingDataResult
import com.qubacy.geoqq.data.mate.request.model.DataMateRequest

class GetMateRequestsDataResult(
    isNewest: Boolean,
    val requests: List<DataMateRequest>
) : ProducingDataResult(isNewest) {

}