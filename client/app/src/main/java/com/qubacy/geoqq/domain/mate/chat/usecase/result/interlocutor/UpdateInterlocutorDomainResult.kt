package com.qubacy.geoqq.domain.mate.chat.usecase.result.interlocutor

import com.qubacy.geoqq._common.model.error.Error
import com.qubacy.geoqq.domain._common.model.user.User
import com.qubacy.geoqq.domain.mate.chat.usecase.result.interlocutor._common.InterlocutorDomainResult

class UpdateInterlocutorDomainResult(
    error: Error? = null,
    interlocutor: User? = null
) : InterlocutorDomainResult(error, interlocutor) {

}