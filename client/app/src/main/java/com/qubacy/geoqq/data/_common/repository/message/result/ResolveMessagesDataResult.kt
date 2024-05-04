package com.qubacy.geoqq.data._common.repository.message.result

import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.qubacy.geoqq.data._common.repository.producing.result.ProducingDataResult

class ResolveMessagesDataResult(
    isNewest: Boolean,
    val messages: List<DataMessage>
) : ProducingDataResult(isNewest) {
}