package com.qubacy.geoqq.domain.user.usecase._common.result.interlocutor

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.model.user.User
import com.qubacy.geoqq.domain.user.usecase._common.result.interlocutor._common.InterlocutorDomainResult

class GetInterlocutorDomainResult(
    error: Error? = null,
    interlocutor: User? = null
) : InterlocutorDomainResult(error, interlocutor) {

}