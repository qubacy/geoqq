package com.qubacy.geoqq.domain.user.usecase._common.result._common

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.model.user.User
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult

abstract class UserDomainResult(
    error: Error? = null,
    val interlocutor: User? = null
) : DomainResult(error) {

}