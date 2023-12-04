package com.qubacy.geoqq.domain.common.usecase.util.extension.user.result

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.domain.common.model.user.User

class GetUsersFromGetUsersByIdsResult(
    val users: List<User>
) : Result() {

}