package com.qubacy.geoqq.domain.user.usecase._common.result.update

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.model.user.User
import com.qubacy.geoqq.domain.user.usecase._common.result._common.UserDomainResult

class UpdateUserDomainResult(
    error: Error? = null,
    interlocutor: User? = null
) : UserDomainResult(error, interlocutor) {

}