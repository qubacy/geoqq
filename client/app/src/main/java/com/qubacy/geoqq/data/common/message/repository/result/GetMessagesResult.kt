package com.qubacy.geoqq.data.common.message.repository.result

import com.qubacy.geoqq.data.common.message.model.DataMessage
import com.qubacy.geoqq.data.common.repository.common.result.common.Result

open class GetMessagesResult(
    val messages: List<DataMessage>
) : Result() {

}