package com.qubacy.geoqq.data.common.repository.message.result

import com.qubacy.geoqq.data.common.model.message.DataMessage
import com.qubacy.geoqq.data.common.repository.common.result.common.Result

open class GetMessagesResult(
    val messages: List<DataMessage>
) : Result() {

}