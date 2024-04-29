package com.qubacy.geoqq.data.geo.message.repository.result

import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.qubacy.geoqq.data._common.repository._common.result.DataResult

class GetGeoMessagesDataResult(
    val messages: List<DataMessage>
) : DataResult {

}