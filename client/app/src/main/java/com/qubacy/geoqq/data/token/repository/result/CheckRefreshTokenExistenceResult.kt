package com.qubacy.geoqq.data.token.repository.result

import com.qubacy.geoqq.data.common.repository.result.common.Result

class CheckRefreshTokenExistenceResult(
    val isExisting: Boolean
) : Result() {
}