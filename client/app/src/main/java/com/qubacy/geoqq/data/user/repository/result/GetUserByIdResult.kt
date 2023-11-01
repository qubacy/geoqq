package com.qubacy.geoqq.data.user.repository.result

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.user.models.DataUser

class GetUserByIdResult(
    val user: DataUser
) : Result() {

}