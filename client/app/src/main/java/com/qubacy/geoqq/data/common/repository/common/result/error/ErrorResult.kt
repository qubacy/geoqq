package com.qubacy.geoqq.data.common.repository.common.result.error

import com.qubacy.geoqq.common.error.common.TypedErrorBase
import com.qubacy.geoqq.data.common.repository.common.result.common.Result

class ErrorResult(
    val error: TypedErrorBase
) : Result() {

}