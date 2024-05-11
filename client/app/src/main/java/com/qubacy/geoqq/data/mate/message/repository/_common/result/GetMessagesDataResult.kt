package com.qubacy.geoqq.data.mate.message.repository._common.result

import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.qubacy.geoqq.data._common.repository.producing.result.ProducingDataResult

class GetMessagesDataResult(
    isNewest: Boolean,
    val offset: Int? = null,
    val messages: List<DataMessage>? = null
) : ProducingDataResult(isNewest) {

}