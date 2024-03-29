package com.qubacy.geoqq.domain.mate.chats.usecase

import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.MateChatDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.result.GetChatsDataResult
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain.mate.chats.model.toMateChat
import com.qubacy.geoqq.domain.mate.chats.projection.MateChatChunk
import com.qubacy.geoqq.domain.mate.chats.usecase.result.chunk.GetChatChunkDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase.result.chunk.UpdateChatChunkDomainResult
import kotlinx.coroutines.launch

class MateChatsUseCase(
    errorDataRepository: ErrorDataRepository,
    private val mMateChatDataRepository: MateChatDataRepository
) : UseCase(mErrorDataRepository = errorDataRepository) {
    companion object {
        const val DEFAULT_CHAT_CHUNK_SIZE = 20
    }

    /**
     * It's supposed that [chunkIndex] is used for calculating both count and offset values;
     */
    fun getChatChunk(chunkIndex: Int) {
        executeLogic({
            val offset = chunkIndex * DEFAULT_CHAT_CHUNK_SIZE
            val count = DEFAULT_CHAT_CHUNK_SIZE

            val getChatsResult = mMateChatDataRepository.getChats(offset, count).await()
                ?: return@executeLogic

            val chats = getChatsResult.chats.map { it.toMateChat() }
            val chatChunk = MateChatChunk(chunkIndex, chats)

            mResultFlow.emit(GetChatChunkDomainResult(chunk = chatChunk))

        }) {
            GetChatChunkDomainResult(error = it)
        }
    }

    override fun onCoroutineScopeSet() {
        super.onCoroutineScopeSet()

        mCoroutineScope.launch {
            mMateChatDataRepository.resultFlow.collect {
                processCollectedDataResult(it)
            }
        }
    }

    private suspend fun processCollectedDataResult(dataResult: DataResult) {
        when (dataResult::class) {
            GetChatsDataResult::class ->
                processGetChatsDataResult(dataResult as GetChatsDataResult)
            else -> throw IllegalArgumentException()
        }
    }

    private suspend fun processGetChatsDataResult(getChatsResult: GetChatsDataResult) {
        val chunkIndex = getChatsResult.offset / DEFAULT_CHAT_CHUNK_SIZE
        val chats = getChatsResult.chats.map { it.toMateChat() }
        val chatChunk = MateChatChunk(chunkIndex, chats)

        mResultFlow.emit(UpdateChatChunkDomainResult(chunk = chatChunk))
    }
}