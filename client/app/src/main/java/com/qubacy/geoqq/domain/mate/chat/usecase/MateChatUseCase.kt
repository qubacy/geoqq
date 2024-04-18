package com.qubacy.geoqq.domain.mate.chat.usecase

import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.MateChatDataRepository
import com.qubacy.geoqq.data.mate.message.repository.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.message.repository.result.GetMessagesDataResult
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.InterlocutorUseCase
import com.qubacy.geoqq.domain.mate.chat.model.toMateMessage
import com.qubacy.geoqq.domain.mate.chat.projection.MateMessageChunk
import com.qubacy.geoqq.domain.mate.chat.usecase.result.chunk.GetMessageChunkDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase.result.chunk.UpdateMessageChunkDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase.result.message.SendMessageDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase.result.chat.DeleteChatDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase.MateRequestUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import javax.inject.Inject

class MateChatUseCase @Inject constructor(
    errorDataRepository: ErrorDataRepository,
    private val mMateRequestUseCase: MateRequestUseCase,
    private val mInterlocutorUseCase: InterlocutorUseCase,
    private val mMateMessageDataRepository: MateMessageDataRepository,
    private val mMateChatDataRepository: MateChatDataRepository
) : UseCase(mErrorDataRepository = errorDataRepository) {
    companion object {
        const val DEFAULT_MESSAGE_CHUNK_SIZE = 20
    }

    override val resultFlow: Flow<DomainResult> = merge(
        mResultFlow,
        mMateRequestUseCase.resultFlow,
        mInterlocutorUseCase.resultFlow
    )

    fun getMessageChunk(chatId: Long, chunkIndex: Int) {
        executeLogic({
            val offset = chunkIndex * DEFAULT_MESSAGE_CHUNK_SIZE
            val count = DEFAULT_MESSAGE_CHUNK_SIZE

            val getMessagesResult = mMateMessageDataRepository
                .getMessages(chatId, offset, count).await()

            val messages = getMessagesResult?.messages?.map { it.toMateMessage() }
            val messageChunk = messages?.let { MateMessageChunk(chunkIndex, messages)}

            mResultFlow.emit(GetMessageChunkDomainResult(chunk = messageChunk))

        }) {
            GetMessageChunkDomainResult(error = it)
        }
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

        }) {
            DeleteChatDomainResult(error = it)
        }
    }

    fun sendMessage(chatId: Long, text: String) {
        executeLogic({
            mMateMessageDataRepository.sendMessage(chatId, text)

            mResultFlow.emit(SendMessageDomainResult())

        }) {
            SendMessageDomainResult(error = it)
        }
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
            GetMessagesDataResult::class ->
                processGetMessagesDataResult(dataResult as GetMessagesDataResult)
            else -> throw IllegalArgumentException()
        }
    }

    private suspend fun processGetMessagesDataResult(
        getMessagesResult: GetMessagesDataResult
    ) {
        val chunkIndex = getMessagesResult.offset / DEFAULT_MESSAGE_CHUNK_SIZE
        val messages = getMessagesResult.messages.map { it.toMateMessage() }
        val messageChunk = MateMessageChunk(chunkIndex, messages)

        mResultFlow.emit(UpdateMessageChunkDomainResult(chunk = messageChunk))
    }
}