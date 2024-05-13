package com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chat

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.mate._common.model.chat.MateChat

class UpdateChatDomainResult(
    error: Error? = null,
    val chat: MateChat? = null
) : DomainResult(error) {

}