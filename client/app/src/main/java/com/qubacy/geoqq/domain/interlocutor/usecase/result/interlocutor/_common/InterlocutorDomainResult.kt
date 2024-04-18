package com.qubacy.geoqq.domain.interlocutor.usecase.result.interlocutor._common

import com.qubacy.geoqq._common.model.error.Error
import com.qubacy.geoqq.domain._common.model.user.User
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult

abstract class InterlocutorDomainResult(
    error: Error? = null,
    val interlocutor: User? = null
) : DomainResult(error) {

}