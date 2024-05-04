package com.qubacy.geoqq.domain.mate.chats.usecase

import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data.mate.chat.repository.MateChatDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain._common.usecase.authorized.AuthorizedUseCase
import com.qubacy.geoqq.domain._common.usecase.authorized.error.middleware.authorizedErrorMiddleware
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import com.qubacy.geoqq.domain.mate.chats.model.toMateChat
import com.qubacy.geoqq.domain.mate.chats.projection.MateChatChunk
import com.qubacy.geoqq.domain.mate.chats.usecase.result.chunk.GetChatChunkDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase.result.chunk.UpdateChatChunkDomainResult
import kotlinx.coroutines.launch
import javax.inject.Inject

class MateChatsUseCase @Inject constructor(
    errorSource: LocalErrorDataSource,
    private val mLogoutUseCase: LogoutUseCase,
    private val mMateChatDataRepository: MateChatDataRepository
) : UseCase(mErrorSource = errorSource), AuthorizedUseCase {
    companion object {
        const val DEFAULT_CHAT_CHUNK_SIZE = 20
    }

    // TODO: Optimization?:
    fun getChatChunk(loadedChatIds: List<Long>, offset: Int) {
        executeLogic({
            val count = DEFAULT_CHAT_CHUNK_SIZE

            val getChatsResultLiveData = mMateChatDataRepository
                .getChats(loadedChatIds, offset, count)

            val initGetChatsResult = getChatsResultLiveData.await()
            val initChats = initGetChatsResult.chats?.map { it.toMateChat() }
            val initChatChunk = initChats?.let { MateChatChunk(offset, it)}

            mResultFlow.emit(GetChatChunkDomainResult(chunk = initChatChunk))

            if (initGetChatsResult.isNewest) return@executeLogic

            val newestGetChatsResult = getChatsResultLiveData.await()
            val newestChats = newestGetChatsResult.chats?.map { it.toMateChat() }
            val newestChatChunk = newestChats?.let { MateChatChunk(offset, it)}

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