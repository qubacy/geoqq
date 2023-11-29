package com.qubacy.geoqq.data.common.util.generator

import com.qubacy.geoqq.data.common.message.model.DataMessage

object DataMessageGeneratorUtility {
    fun generateDataMessages(count: Int, startId: Long = 0L): List<DataMessage> {
        return LongRange(startId, startId + count - 1).map {
            DataMessage(it, 0L, "test message $it", 123123123)
        }
    }
}