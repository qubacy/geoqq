package com.qubacy.geoqq.data.common.util.generator

import com.qubacy.geoqq.data.mate.chat.model.DataMateChat

object DataMateChatGeneratorUtility {
    fun generateDataChats(count: Int, startId: Long = 0L): List<DataMateChat> {
        return LongRange(startId, startId + count - 1).map {
            val lastDataMessage = DataMessageGeneratorUtility.generateDataMessages(1).first()

            DataMateChat(it, it + 1, 0, lastDataMessage)
        }
    }
}