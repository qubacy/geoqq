package com.qubacy.geoqq.data.mate.chat.repository.source.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.qubacy.geoqq.data.mate.message.repository.source.local.model.MateMessageEntity

class MateChatWithLastMessageModel(
    @Embedded
    val mateChatEntity: MateChatEntity,
    @Relation(
        entity = MateMessageEntity::class,
        parentColumn = MateChatEntity.LAST_MESSAGE_ID_PROP_NAME,
        entityColumn = MateMessageEntity.ID_PROP_NAME
    )
    val lastMessage: MateMessageEntity?
) {

}