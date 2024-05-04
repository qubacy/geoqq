package com.qubacy.geoqq.data._common.repository.producing.result

import com.qubacy.geoqq.data._common.repository._common.result.DataResult

abstract class ProducingDataResult(
    val isNewest: Boolean = true
) : DataResult {

}