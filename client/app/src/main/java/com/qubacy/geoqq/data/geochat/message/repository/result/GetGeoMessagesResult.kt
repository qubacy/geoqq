package com.qubacy.geoqq.data.geochat.message.repository.result

import com.qubacy.geoqq.data.common.model.message.DataMessage
import com.qubacy.geoqq.data.common.repository.common.result.common.Result

class GetGeoMessagesResult(
    val messages: List<DataMessage>
) : Result() {
}