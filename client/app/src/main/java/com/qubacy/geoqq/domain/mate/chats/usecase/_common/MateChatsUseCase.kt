package com.qubacy.geoqq.domain.mate.chats.usecase._common

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.result.added.MateChatAddedDataResult
import com.qubacy.geoqq.data.mate.chat.repository._common.result.updated.MateChatUpdatedDataResult
import com.qubacy.geoqq.domain._common.usecase._common.error.middleware.ErrorMiddleware
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.AuthorizedUseCase
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.error.middleware.AuthorizedErrorMiddleware
import com.qubacy.geoqq.domain._common.usecase.aspect.user.UserAspectUseCase
import com.qubacy.geoqq.domain._common.usecase.base.updatable.UpdatableUseCase
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.error.handler._common.UpdateErrorHandler
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.error.handler.common.UpdateCommonErrorHandler
import com.qubacy.geoqq.domain.mate._common.model.chat.toMateChat
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chat.added.MateChatAddedDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chat.updated.MateChatUpdatedDomainResult

abstract class MateChatsUseCase(
    errorSource: LocalErrorDatabaseDataSource
) : UpdatableUseCase(errorSource = errorSource), AuthorizedUseCase, UserAspectUseCase {
    companion object {
        const val DEFAULT_CHAT_CHUNK_SIZE = 20
    }

    override fun generateUpdateErrorHandlers(): Array<UpdateErrorHandler> {
        return arrayOf(UpdateCommonErrorHandler(this))
    }

    override fun generateErrorMiddlewares(): Array<ErrorMiddleware> {
        return arrayOf(AuthorizedErrorMiddleware(this))
    }

    abstract fun getChatChunk(loadedChatIds: List<Long>, offset: Int)
    open fun processMateChatAddedDataResult(
        dataResult: MateChatAddedDataResult
    ): MateChatAddedDomainResult {
        val chat = dataResult.mateChat.toMateChat()

        return MateChatAddedDomainResult(chat = chat)
    }
    open fun processMateChatUpdatedDataResult(
        dataResult: MateChatUpdatedDataResult
    ): MateChatUpdatedDomainResult {
        val chat = dataResult.mateChat.toMateChat()

        return MateChatUpdatedDomainResult(chat = chat)
    }
}