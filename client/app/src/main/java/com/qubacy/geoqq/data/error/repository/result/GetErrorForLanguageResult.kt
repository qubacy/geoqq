package com.qubacy.geoqq.data.error.repository.result

import com.qubacy.geoqq.common.error.common.Error
import com.qubacy.geoqq.data.common.repository.common.result.common.Result

data class GetErrorForLanguageResult(
    val error: Error
) : Result() {

}