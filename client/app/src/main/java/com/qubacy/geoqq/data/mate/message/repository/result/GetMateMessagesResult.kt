package com.qubacy.geoqq.data.mate.message.repository.result

import com.qubacy.geoqq.data.common.message.model.DataMessage
import com.qubacy.geoqq.data.common.message.repository.result.GetMessagesResult

class GetMateMessagesResult(
    messages: List<DataMessage>,
    val areLocal: Boolean,
    val isInitial: Boolean
) : GetMessagesResult(messages) {

}