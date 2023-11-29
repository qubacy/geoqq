package com.qubacy.geoqq.domain.mate.chat.operation

import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.common.operation.common.Operation

class AddPrecedingMessagesOperation(
    val precedingMessages: List<Message>,
    val areUpdated: Boolean
) : Operation() {

}