package com.qubacy.geoqq.domain.mate.chats.usecase.impl

import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.MateChatDataRepository
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import com.qubacy.geoqq.domain.mate._common.model.chat.toMateChat
import com.qubacy.geoqq.domain.mate.chats.projection.MateChatChunk
import com.qubacy.geoqq.domain.mate.chats.usecase._common.MateChatsUseCase
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk.GetChatChunkDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk.UpdateChatChunkDomainResult
import kotlinx.coroutines.launch
import javax.inject.Inject

class MateChatsUseCaseImpl @Inject constructor(
    errorSource: LocalErrorDatabaseDataSource,
    private val mLogoutUseCase: LogoutUseCase,
    private val mMateChatDataRepository: MateChatDataRepository
) : MateChatsUseCase(errorSource) {
    // TODO: Optimization?:
    override fun getChatChunk(loadedChatIds: List<Long>, offset: Int) {
        executeLogic({
            val count = DEFAULT_CHAT_CHUNK_SIZE

            val getChatsResultLiveData = mMateChatDataRepository
                .getChats(loadedChatIds, offset, count)

            var version = 0

            val initGetChatsResult = getChatsResultLiveData.awaitUntilVersion(version)
            val initChats = initGetChatsResult.chats?.map { it.toMateChat() }
            val initChatChunk = initChats?.let { MateChatChunk(offset, it)}

            mResultFlow.emit(GetChatChunkDomainResult(chunk = initChatChunk))

            if (initGetChatsResult.isNewest) return@executeLogic

            ++version

            val newestGetChatsResult = getChatsResultLiveData.awaitUntilVersion(version)

            if (newestGetChatsResult.chats == null) return@executeLogic

            val newestChats = newestGetChatsResult.chats.map { it.toMateChat() }
            val newestChatChunk = MateChatChunk(offset, newestChats)

            mResultFlow.emit(UpdateChatChunkDomainResult(chunk = newestChatChunk))

        }, {
            GetChatChunkDomainResult(error = it)
        }, ::authorizedErrorMiddleware)
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
            else -> throw IllegalArgumentException()
        }
    }

    override fun getLogoutUseCase(): LogoutUseCase {
        return mLogoutUseCase
    }
}