package com.qubacy.geoqq.domain.mate.chats.usecase.impl

import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data.auth.repository._common.AuthDataRepository
import com.qubacy.geoqq.data.mate.chat.repository._common.MateChatDataRepository
import com.qubacy.geoqq.domain._common.usecase.aspect.user.update.handler.UserDataUpdateHandler
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.handler.DataUpdateHandler
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import com.qubacy.geoqq.domain.mate._common.model.chat.toMateChat
import com.qubacy.geoqq.domain.mate.chats.projection.MateChatChunk
import com.qubacy.geoqq.domain.mate.chats.usecase._common.MateChatsUseCase
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk.get.GetMateChatChunkDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk.update.UpdateMateChatChunkDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase._common.update.handler.MateChatsDataUpdateHandler
import javax.inject.Inject

class MateChatsUseCaseImpl @Inject constructor(
    errorSource: LocalErrorDatabaseDataSource,
    private val mLogoutUseCase: LogoutUseCase,
    private val mMateChatDataRepository: MateChatDataRepository,
    private val mAuthDataRepository: AuthDataRepository
) : MateChatsUseCase(errorSource) {
    override fun getUpdatableRepositories(): Array<ProducingDataRepository> {
        return arrayOf(mAuthDataRepository, mMateChatDataRepository)
    }

    override fun generateDataUpdateHandlers(): Array<DataUpdateHandler<*>> {
        return super.generateDataUpdateHandlers()
            .plus(UserDataUpdateHandler(this))
            .plus(MateChatsDataUpdateHandler(this))
    }

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

            mResultFlow.emit(GetMateChatChunkDomainResult(chunk = initChatChunk))

            if (initGetChatsResult.isNewest) return@executeLogic

            ++version

            val newestGetChatsResult = getChatsResultLiveData.awaitUntilVersion(version)

            if (newestGetChatsResult.chats == null) return@executeLogic

            val newestChats = newestGetChatsResult.chats.map { it.toMateChat() }
            val newestChatChunk = MateChatChunk(offset, newestChats)

            mResultFlow.emit(UpdateMateChatChunkDomainResult(chunk = newestChatChunk))

        }, {
            GetMateChatChunkDomainResult(error = it)
        })
    }

    override fun onCoroutineScopeSet() {
        super.onCoroutineScopeSet()

        mAuthDataRepository.setCoroutineScope(mCoroutineScope)
        mMateChatDataRepository.setCoroutineScope(mCoroutineScope)
    }

    override fun getLogoutUseCase(): LogoutUseCase {
        return mLogoutUseCase
    }
}