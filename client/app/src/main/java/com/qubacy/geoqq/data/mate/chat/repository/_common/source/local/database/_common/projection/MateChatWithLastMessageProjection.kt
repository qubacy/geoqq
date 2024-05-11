package com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.projection

import androidx.room.Embedded
import androidx.room.Relation
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity.MateMessageEntity

data class MateChatWithLastMessageProjection(
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