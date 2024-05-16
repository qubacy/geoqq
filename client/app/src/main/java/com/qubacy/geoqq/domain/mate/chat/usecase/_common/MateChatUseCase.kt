package com.qubacy.geoqq.domain.mate.chat.usecase._common

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.AuthorizedUseCase
import com.qubacy.geoqq.domain._common.usecase.aspect.user.UserUseCase
import com.qubacy.geoqq.domain._common.usecase.base.updatable.UpdatableUseCase

abstract class MateChatUseCase(
    errorSource: LocalErrorDatabaseDataSource
) : UpdatableUseCase(errorSource = errorSource), AuthorizedUseCase, UserUseCase {
    companion object {
        const val DEFAULT_MESSAGE_CHUNK_SIZE = 20
    }

    abstract fun getMessageChunk(chatId: Long, loadedMessageIds: List<Long>, offset: Int)
    abstract fun sendMateRequestToInterlocutor(interlocutorId: Long)
    abstract fun getInterlocutor(interlocutorId: Long)
    abstract fun deleteChat(chatId: Long)
    abstract fun sendMessage(chatId: Long, text: String)
}