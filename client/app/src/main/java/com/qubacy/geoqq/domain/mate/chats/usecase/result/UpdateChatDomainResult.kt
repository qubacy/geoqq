package com.qubacy.geoqq.domain.mate.chats.usecase.result

import com.qubacy.geoqq._common.error.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.mate.chats.model.MateChat

class UpdateChatDomainResult(
    error: Error? = null,
    val chat: MateChat? = null
) : DomainResult(error) {

}