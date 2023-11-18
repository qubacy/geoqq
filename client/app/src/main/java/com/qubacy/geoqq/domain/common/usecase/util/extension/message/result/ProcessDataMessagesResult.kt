package com.qubacy.geoqq.domain.common.usecase.util.extension.message.result

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.domain.common.model.message.Message

class ProcessDataMessagesResult(
    val messages: List<Message>
) : Result() {

}