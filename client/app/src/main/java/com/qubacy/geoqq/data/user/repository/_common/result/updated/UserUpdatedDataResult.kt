package com.qubacy.geoqq.data.user.repository._common.result.updated

import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data.user.model.DataUser

class UserUpdatedDataResult(
    val user: DataUser
) : DataResult {

}