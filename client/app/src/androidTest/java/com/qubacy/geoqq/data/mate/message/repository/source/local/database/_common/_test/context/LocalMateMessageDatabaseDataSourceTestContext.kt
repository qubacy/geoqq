package com.qubacy.geoqq.data.mate.message.repository.source.local.database._common._test.context

import com.qubacy.geoqq.data.mate.chat.repository.source.local.database._common._test.context.LocalMateChatDatabaseDataSourceTestContext
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity.MateMessageEntity

object LocalMateMessageDatabaseDataSourceTestContext {
    private val DEFAULT_MATE_CHAT_ENTITY = LocalMateChatDatabaseDataSourceTestContext
        .DEFAULT_MATE_CHAT_ENTITY

    val DEFAULT_MATE_MESSAGE_ENTITY = MateMessageEntity(
        0L,
        DEFAULT_MATE_CHAT_ENTITY.id,
        DEFAULT_MATE_CHAT_ENTITY.userId,
        "test",
        0L
    )
}