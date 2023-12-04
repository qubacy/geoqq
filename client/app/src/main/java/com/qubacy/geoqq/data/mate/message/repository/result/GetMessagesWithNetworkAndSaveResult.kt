package com.qubacy.geoqq.data.mate.message.repository.result

import com.qubacy.geoqq.data.common.model.message.DataMessage
import com.qubacy.geoqq.data.common.repository.common.result.common.Result

class GetMessagesWithNetworkAndSaveResult(
    val messages: List<DataMessage>,
    val areUpdated: Boolean
) : Result() {

}