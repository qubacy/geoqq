package com.qubacy.geoqq.domain._common.usecase.user.result

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.model.user.User
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult

// todo: should be user after upcoming refactoring:
class UpdateUsersDomainResult(
    error: Error? = null,
    users: List<User>? = null
) : DomainResult(error = error) {

}