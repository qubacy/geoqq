package com.qubacy.geoqq.domain.mate.chats.operation

import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.mate.chats.model.MateChat

class AddPrecedingChatsOperation(
    val precedingChats: List<MateChat>,
    val areUpdated: Boolean
) : Operation() {

}