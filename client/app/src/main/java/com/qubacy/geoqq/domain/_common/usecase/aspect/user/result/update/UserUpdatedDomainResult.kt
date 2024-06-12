package com.qubacy.geoqq.domain._common.usecase.aspect.user.result.update

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.model.user.User
import com.qubacy.geoqq.domain._common.usecase.aspect.user.result._common.UserDomainResult

class UserUpdatedDomainResult(
    error: Error? = null,
    user: User? = null
) : UserDomainResult(error, user) {

}