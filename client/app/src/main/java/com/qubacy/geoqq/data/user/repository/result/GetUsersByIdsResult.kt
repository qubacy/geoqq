package com.qubacy.geoqq.data.user.repository.result

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.user.model.DataUser

class GetUsersByIdsResult(
    val users: List<DataUser>,
    val isLocal: Boolean
) : Result() {

}