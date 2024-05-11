package com.qubacy.geoqq.domain.mate.chats.usecase._common

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain._common.usecase.authorized.AuthorizedUseCase

abstract class MateChatsUseCase(
    errorSource: LocalErrorDatabaseDataSource
) : UseCase(mErrorSource = errorSource), AuthorizedUseCase {
    companion object {
        const val DEFAULT_CHAT_CHUNK_SIZE = 20
    }

    abstract fun getChatChunk(loadedChatIds: List<Long>, offset: Int)
}