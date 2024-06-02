package com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chat.updated

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain.mate._common.model.chat.MateChat
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chat._common.MateChatDomainResult

class MateChatUpdatedDomainResult(
    error: Error? = null,
    chat: MateChat? = null
) : MateChatDomainResult(error, chat) {

}