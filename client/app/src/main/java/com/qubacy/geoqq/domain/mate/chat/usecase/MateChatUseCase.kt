package com.qubacy.geoqq.domain.mate.chat.usecase

import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data.mate.chat.repository.impl.MateChatDataRepositoryImpl
import com.qubacy.geoqq.data.mate.message.repository.impl.MateMessageDataRepositoryImpl
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.authorized.AuthorizedUseCase
import com.qubacy.geoqq.domain._common.usecase.authorized.error.middleware.authorizedErrorMiddleware
import com.qubacy.geoqq.domain._common.usecase.chat.result.SendMessageDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.InterlocutorUseCase
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import com.qubacy.geoqq.domain.mate.chat.model.toMateMessage
import com.qubacy.geoqq.domain.mate.chat.projection.MateMessageChunk
import com.qubacy.geoqq.domain.mate.chat.usecase.result.chunk.GetMessageChunkDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase.result.chunk.UpdateMessageChunkDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase.result.chat.DeleteChatDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase.MateRequestUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import javax.inject.Inject

class MateChatUseCase @Inject constructor(
    errorSource: LocalErrorDatabaseDataSourceImpl,
    private val mMateRequestUseCase: MateRequestUseCase,
    private val mInterlocutorUseCase: InterlocutorUseCase,
    private val mLogoutUseCase: LogoutUseCase,
    private val mMateMessageDataRepository: MateMessageDataRepositoryImpl,
    private val mMateChatDataRepository: MateChatDataRepositoryImpl
) : UseCase(mErrorSource = errorSource), AuthorizedUseCase {
    companion object {
        const val DEFAULT_MESSAGE_CHUNK_SIZE = 20
    }

    override val resultFlow: Flow<DomainResult> = merge(
        mResultFlow,
        mMateRequestUseCase.resultFlow,
        mInterlocutorUseCase.resultFlow
    )

    // todo: Optimization?:
    fun getMessageChunk(chatId: Long, loadedMessageIds: List<Long>, offset: Int) {
        executeLogic({
            val count = DEFAULT_MESSAGE_CHUNK_SIZE

            val getMessagesResultLiveData = mMateMessageDataRepository
                .getMessages(chatId, loadedMessageIds, offset, count)

            var version = 0

            val initGetMessagesResult = getMessagesResultLiveData.awaitUntilVersion(version)
            val initMessages = initGetMessagesResult.messages?.map { it.toMateMessage() }
            val initMessageChunk = initMessages?.let { MateMessageChunk(offset, it)}

            mResultFlow.emit(GetMessageChunkDomainResult(chunk = initMessageChunk))

            if (initGetMessagesResult.isNewest) return@executeLogic

            ++version

            val newestGetMessagesResult = getMessagesResultLiveData.awaitUntilVersion(version)
            val newestMessages = newestGetMessagesResult.messages?.map { it.toMateMessage() }
            val newestMessageChunk = newestMessages?.let { MateMessageChunk(offset, it)}

            mResultFlow.emit(UpdateMessageChunkDomainResult(chunk = newestMessageChunk))

        }, {
            GetMessageChunkDomainResult(error = it)
        }, ::authorizedErrorMiddleware)
    }

    fun sendMateRequestToInterlocutor(interlocutorId: Long) {
        mMateRequestUseCase.sendMateRequest(interlocutorId)
    }

    fun getInterlocutor(interlocutorId: Long) {
        mInterlocutorUseCase.getInterlocutor(interlocutorId)
    }

    fun deleteChat(chatId: Long) {
        executeLogic({
            mMateChatDataRepository.deleteChat(chatId)

            mResultFlow.emit(DeleteChatDomainResult())

        }, {
            DeleteChatDomainResult(error = it)
        }, ::authorizedErrorMiddleware)
    }

    fun sendMessage(chatId: Long, text: String) {
        executeLogic({
            mMateMessageDataRepository.sendMessage(chatId, text)

            mResultFlow.emit(SendMessageDomainResult())

        }, {
            SendMessageDomainResult(error = it)
        }, ::authorizedErrorMiddleware)
    }

    override fun onCoroutineScopeSet() {
        super.onCoroutineScopeSet()

        mCoroutineScope.launch {
            mMateMessageDataRepository.resultFlow.collect {
                processCollectedDataResult(it)
            }
        }

        mMateRequestUseCase.setCoroutineScope(mCoroutineScope)
        mInterlocutorUseCase.setCoroutineScope(mCoroutineScope)
    }

    private suspend fun processCollectedDataResult(dataResult: DataResult) {
        when (dataResult::class) {
            else -> throw IllegalArgumentException()
        }
    }

    override fun getLogoutUseCase(): LogoutUseCase {
        return mLogoutUseCase
    }
}