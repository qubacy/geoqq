package com.qubacy.geoqq.data.mate.message.repository.result

import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.qubacy.geoqq.data._common.repository._common.result.DataResult

class GetMessagesDataResult(
    val messages: List<DataMessage>
) : DataResult {

}