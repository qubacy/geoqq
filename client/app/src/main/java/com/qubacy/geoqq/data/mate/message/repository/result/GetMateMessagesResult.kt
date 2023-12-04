package com.qubacy.geoqq.data.mate.message.repository.result

import com.qubacy.geoqq.data.common.model.message.DataMessage
import com.qubacy.geoqq.data.common.repository.message.result.GetMessagesResult

class GetMateMessagesResult(
    messages: List<DataMessage>,
    val areLocal: Boolean,
    val isInitial: Boolean
) : GetMessagesResult(messages) {

}