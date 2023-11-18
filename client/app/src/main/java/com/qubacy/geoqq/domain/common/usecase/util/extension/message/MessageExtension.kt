package com.qubacy.geoqq.domain.common.usecase.util.extension.message

import com.qubacy.geoqq.data.common.message.model.DataMessage
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.common.usecase.util.extension.message.result.ProcessDataMessagesResult

interface MessageExtension {
    fun processDataMessages(messages: List<DataMessage>): Result {
        val resultMessages = messages.map { dataMessage ->
            Message(dataMessage.id, dataMessage.userId, dataMessage.text, dataMessage.time)
        }

        return ProcessDataMessagesResult(resultMessages)
    }
}