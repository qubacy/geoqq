package com.qubacy.geoqq.domain.mate.chats.usecase.result

import com.qubacy.geoqq._common.error.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.mate.chats.model.MateChat

class GetChatsDomainResult(
    error: Error? = null,
    val chats: List<MateChat>? = null
) : DomainResult(error) {
}