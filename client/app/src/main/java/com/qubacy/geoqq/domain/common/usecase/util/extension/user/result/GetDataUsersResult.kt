package com.qubacy.geoqq.domain.common.usecase.util.extension.user.result

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.user.model.DataUser

class GetDataUsersResult(
    val dataUsers: List<DataUser>
) : Result() {

}