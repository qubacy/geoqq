package com.qubacy.geoqq.domain.mate.chat.usecase._common

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.result.added.MateMessageAddedDataResult
import com.qubacy.geoqq.domain._common.usecase._common.error.middleware.ErrorMiddleware
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.AuthorizedUseCase
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.error.middleware.AuthorizedErrorMiddleware
import com.qubacy.geoqq.domain._common.usecase.aspect.user.UserAspectUseCase
import com.qubacy.geoqq.domain._common.usecase.base.updatable.UpdatableUseCase
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.error.handler._common.UpdateErrorHandler
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.error.handler.common.UpdateCommonErrorHandler
import com.qubacy.geoqq.domain.mate._common.model.message.toMateMessage
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.message.MateMessageAddedDomainResult

abstract class MateChatUseCase(
    errorSource: LocalErrorDatabaseDataSource
) : UpdatableUseCase(errorSource = errorSource), AuthorizedUseCase, UserAspectUseCase {
    companion object {
        const val DEFAULT_MESSAGE_CHUNK_SIZE = 20
    }

    override fun generateUpdateErrorHandlers(): Array<UpdateErrorHandler> {
        return arrayOf(UpdateCommonErrorHandler(this))
    }

    override fun generateErrorMiddlewares(): Array<ErrorMiddleware> {
        return arrayOf(AuthorizedErrorMiddleware(this))
    }

    abstract fun getMessageChunk(chatId: Long, loadedMessageIds: List<Long>, offset: Int)
    abstract fun sendMateRequestToInterlocutor(interlocutorId: Long)
    abstract fun getInterlocutor(interlocutorId: Long)
    abstract fun deleteChat(chatId: Long)
    abstract fun sendMessage(chatId: Long, text: String)
    open fun processMateMessageAddedDataResult(
        dataResult: MateMessageAddedDataResult
    ): MateMessageAddedDomainResult {
        val message = dataResult.message.toMateMessage()

        return MateMessageAddedDomainResult(message = message)
    }
}