package com.qubacy.geoqq.data.geo.message.repository.result

import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.qubacy.geoqq.data._common.repository.producing.result.ProducingDataResult

class GetGeoMessagesDataResult(
    isNewest: Boolean,
    val messages: List<DataMessage>
) : ProducingDataResult(isNewest) {

}