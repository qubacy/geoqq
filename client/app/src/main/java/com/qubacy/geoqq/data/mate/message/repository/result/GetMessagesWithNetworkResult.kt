package com.qubacy.geoqq.data.mate.message.repository.result

import com.qubacy.geoqq.data.common.message.model.DataMessage
import com.qubacy.geoqq.data.common.repository.common.result.common.Result

class GetMessagesWithNetworkResult(
    val messages: List<DataMessage>
) : Result() {

}