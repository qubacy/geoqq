package com.qubacy.geoqq.data.user.repository.result

import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data.user.model.DataUser

data class GetUsersByIdsDataResult(
    val users: List<DataUser>
) : DataResult {

}