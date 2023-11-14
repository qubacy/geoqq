package com.qubacy.geoqq.domain.mate.chat.result

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.domain.common.model.message.Message

class ProcessDataMessageResult(
    val message: Message
) : Result() {

}