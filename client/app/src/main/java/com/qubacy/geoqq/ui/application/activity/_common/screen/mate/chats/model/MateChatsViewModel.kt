package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.domain.mate.chats.projection.MateChatChunk
import com.qubacy.geoqq.domain.mate.chats.usecase.MateChatsUseCase
import com.qubacy.geoqq.domain.mate.chats.usecase.result.chunk.GetChatChunkDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase.result.chunk.UpdateChatChunkDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.AuthorizedViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.result.handler.AuthorizedDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.toMateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.operation.InsertChatsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.operation.UpdateChatChunkUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.result.handler.MateChatsDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.state.MateChatsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
open class MateChatsViewModel @Inject constructor(
    mSavedStateHandle: SavedStateHandle,
    mErrorSource: LocalErrorDataSource,
    mUseCase: MateChatsUseCase
) : BusinessViewModel<MateChatsUiState, MateChatsUseCase>(
    mSavedStateHandle, mErrorSource, mUseCase
), AuthorizedViewModel {
    companion object {
        const val TAG = "MateChatsViewModel"
    }

    private var mIsGettingNextChatChunk: Boolean = false

    override fun generateDomainResultHandlers(): Array<DomainResultHandler<*>> {
        return super.generateDomainResultHandlers()
            .plus(AuthorizedDomainResultHandler(this))
            .plus(MateChatsDomainResultHandler(this))
    }

    override fun generateDefaultUiState(): MateChatsUiState {
        return MateChatsUiState()
    }

    fun onMateChatsGetChatChunk(
        getChatChunkResult: GetChatChunkDomainResult
    ): List<UiOperation> {
        if (mUiState.isLoading) changeLoadingState(false)

        mIsGettingNextChatChunk = false

        if (!getChatChunkResult.isSuccessful())
            return onError(getChatChunkResult.error!!)

        if (getChatChunkResult.chunk == null) return listOf()

        val chatPresentationChunk = processDomainChatChunk(getChatChunkResult.chunk)

        return listOf(InsertChatsUiOperation(getChatChunkResult.chunk.offset, chatPresentationChunk))
    }

    fun onMateChatsUpdateChatChunk(
        updateChatChunkResult: UpdateChatChunkDomainResult
    ): List<UiOperation> {
        if (mUiState.isLoading) changeLoadingState(false)

        mIsGettingNextChatChunk = false

        if (!updateChatChunkResult.isSuccessful())
            return onError(updateChatChunkResult.error!!)

        val prevChatChunkOffset = getPrevChatChunkOffset(updateChatChunkResult.chunk!!.offset)
        //Log.d(TAG, "processUpdateChatChunkDomainResult(): prevChatChunkOffset = $prevChatChunkOffset; mUiState.chatChunkSizes = ${mUiState.chatChunkSizes.map { "${it.key} -> ${it.value}, " }};")
        val prevChatChunkSize = mUiState.chatChunkSizes[prevChatChunkOffset]!!
        val curChatChunkSize = updateChatChunkResult.chunk.chats.size

        val chatPresentationChunk = processDomainChatChunk(updateChatChunkResult.chunk)

        return listOf(
            UpdateChatChunkUiOperation(
            updateChatChunkResult.chunk.offset,
            chatPresentationChunk,
            prevChatChunkSize - curChatChunkSize)
        )
    }

    private fun processDomainChatChunk(chatChunk: MateChatChunk): List<MateChatPresentation> {
        val chatPresentationChunk =
            chatChunk.chats.map { it.toMateChatPresentation() }.toMutableList()
        val chatPresentationChunkSize = chatPresentationChunk.size
        val chunkOffset = getPrevChatChunkOffset(chatChunk.offset)

        if (!mUiState.chatChunkSizes.contains(chunkOffset)) {
            mUiState.chatChunkSizes[chunkOffset] = chatPresentationChunkSize
            mUiState.chats.addAll(chatPresentationChunk)

        } else {
            val prevChatChunkSize = mUiState.chatChunkSizes[chunkOffset]!!
            val prevChatToRemovePosition = chatChunk.offset
            val prevChatsToRemove = mUiState.chats.subList(
                prevChatToRemovePosition, prevChatToRemovePosition + prevChatChunkSize)

            mUiState.chats.removeAll(prevChatsToRemove)
            mUiState.chats.addAll(prevChatToRemovePosition, chatPresentationChunk)

            mUiState.chatChunkSizes[chunkOffset] = chatPresentationChunkSize
        }

        return chatPresentationChunk
    }

    private fun getPrevChatChunkOffset(offset: Int): Int {
        return offset - mUiState.newChatCount
    }

    // todo: reconsider this (mb there is another optimal way?): DOESNT WORK
    open fun getNextChatChunk() {
        if (!isNextChatChunkGettingAllowed()) return

        mIsGettingNextChatChunk = true

        changeLoadingState(true)

        val loadedChatIds = mUiState.chats.map { it.id }
        val offset = mUiState.chats.size

        //Log.d(TAG, "getNextChatChunk(): getting a new chunk. offset = $offset;")

        mUseCase.getChatChunk(loadedChatIds, offset)
    }

    open fun resetChatChunks() {
        mUiState.apply {
            newChatCount = 0

            chats.clear()
            chatChunkSizes.clear()
        }
    }

    open fun areChatChunksInitialized(): Boolean {
        return mUiState.chatChunkSizes.isNotEmpty()
    }

    open fun isNextChatChunkGettingAllowed(): Boolean {
        val lastChatChunkSize = mUiState.chatChunkSizes.entries.lastOrNull()?.value

        val chunkSizeCheck = (
            lastChatChunkSize == null ||
            (lastChatChunkSize != 0 &&
                lastChatChunkSize % MateChatsUseCase.DEFAULT_CHAT_CHUNK_SIZE == 0
            )
        )

        //Log.d(TAG, "isNextChatChunkGettingAllowed(): lastChatChunkSize = $lastChatChunkSize; mIsGettingNextChatChunk = $mIsGettingNextChatChunk;")

        return (!mIsGettingNextChatChunk && chunkSizeCheck)
    }

    open fun prepareChatForEntering(chatId: Long): MateChatPresentation {
        for (chatIndex in mUiState.chats.indices) {
            val chat = mUiState.chats[chatIndex]

            if (chat.id == chatId) {
                mUiState.chats[chatIndex] = chat.copy(newMessageCount = 0)

                return mUiState.chats[chatIndex]
            }
        }

        throw IllegalStateException()
    }

//    /**
//     * Returns a position of the deleted chat;
//     */
//    open fun removeDeletedChat(chatId: Long): Int {
//        val chatToDeletePosition = mUiState.chats.indexOfFirst { it.id == chatId }
//
//        mUiState.chats.removeAt(chatToDeletePosition)
//
//        return chatToDeletePosition
//    }
}

@Qualifier
annotation class MateChatsViewModelFactoryQualifier

class MateChatsViewModelFactory(
    private val mErrorSource: LocalErrorDataSource,
    private val mMateChatsUseCase: MateChatsUseCase
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (!modelClass.isAssignableFrom(MateChatsViewModel::class.java))
            throw IllegalArgumentException()

        return MateChatsViewModel(handle, mErrorSource, mMateChatsUseCase) as T
    }
}