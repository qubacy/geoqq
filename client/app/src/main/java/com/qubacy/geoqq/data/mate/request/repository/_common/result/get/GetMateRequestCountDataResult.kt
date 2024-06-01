package com.qubacy.geoqq.data.mate.request.repository._common.result.get

import com.qubacy.geoqq.data._common.repository.producing.result.ProducingDataResult

class GetMateRequestCountDataResult(
    val count: Int
) : ProducingDataResult(isNewest = true) {

}