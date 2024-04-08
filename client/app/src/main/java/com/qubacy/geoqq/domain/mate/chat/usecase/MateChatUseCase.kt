package com.qubacy.geoqq.domain.mate.chat.usecase

import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.message.repository.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.message.repository.result.GetMessagesDataResult
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsDataResult
import com.qubacy.geoqq.domain._common.model.user.toUser
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain.mate.chat.model.toMateMessage
import com.qubacy.geoqq.domain.mate.chat.projection.MateMessageChunk
import com.qubacy.geoqq.domain.mate.chat.usecase.result.chunk.GetMessageChunkDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase.result.chunk.UpdateMessageChunkDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase.result.interlocutor.GetInterlocutorDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase.result.interlocutor.UpdateInterlocutorDomainResult
import kotlinx.coroutines.launch

class MateChatUseCase(
    errorDataRepository: ErrorDataRepository,
    private val mMateMessageDataRepository: MateMessageDataRepository,
    private val mUserDataRepository: UserDataRepository
) : UseCase(mErrorDataRepository = errorDataRepository) {
    companion object {
        const val DEFAULT_MESSAGE_CHUNK_SIZE = 20
    }

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

    fun getInterlocutor(interlocutorId: Long) {
        executeLogic({
            val getUsersResult = mUserDataRepository.getUsersByIds(listOf(interlocutorId))
                .await()
            val interlocutor = getUsersResult.users.first().toUser()

            mResultFlow.emit(GetInterlocutorDomainResult(interlocutor = interlocutor))

        }) {
            GetInterlocutorDomainResult(error = it)
        }
    }

    override fun onCoroutineScopeSet() {
        super.onCoroutineScopeSet()

        mCoroutineScope.launch {
            mMateMessageDataRepository.resultFlow.collect {
                processCollectedDataResult(it)
            }
        }
    }

    private suspend fun processCollectedDataResult(dataResult: DataResult) {
        when (dataResult::class) {
            GetMessagesDataResult::class ->
                processGetMessagesDataResult(dataResult as GetMessagesDataResult)
            GetUsersByIdsDataResult::class ->
                processGetUsersByIdsDataResult(dataResult as GetUsersByIdsDataResult)
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

    private suspend fun processGetUsersByIdsDataResult(
        getUsersByIdsDataResult: GetUsersByIdsDataResult
    ) {
        val interlocutor = getUsersByIdsDataResult.users.first().toUser()

        mResultFlow.emit(UpdateInterlocutorDomainResult(interlocutor = interlocutor))
    }
}