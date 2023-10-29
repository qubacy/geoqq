package com.qubacy.geoqq.data.common.repository.result.error

import com.qubacy.geoqq.common.error.common.TypedErrorBase
import com.qubacy.geoqq.data.common.repository.result.common.Result

class ErrorResult(
    val error: TypedErrorBase
) : Result() {

}