package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.mate.chats.projection.MateChatChunk
import com.qubacy.geoqq.domain.mate.chats.usecase.MateChatsUseCase
import com.qubacy.geoqq.domain.mate.chats.usecase.result.chunk.GetChatChunkDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase.result.chunk.UpdateChatChunkDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.toMateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.operation.InsertChatsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.operation.UpdateChatChunkUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.state.MateChatsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
open class MateChatsViewModel @Inject constructor(
    mSavedStateHandle: SavedStateHandle,
    mErrorDataRepository: ErrorDataRepository,
    mUseCase: MateChatsUseCase
) : BusinessViewModel<MateChatsUiState, MateChatsUseCase>(
    mSavedStateHandle, mErrorDataRepository, mUseCase
) {
    companion object {
        const val TAG = "MateChatsViewModel"

        const val LAST_CHAT_CHUNK_INDEX_KEY = "lastChatChunkIndex"
    }

    private var mLastChatChunkIndex: Int = 0
    private var mIsGettingNextChatChunk: Boolean = false

    init {
        mLastChatChunkIndex = mSavedStateHandle[LAST_CHAT_CHUNK_INDEX_KEY] ?: 0
    }

    override fun onCleared() {
        mSavedStateHandle[LAST_CHAT_CHUNK_INDEX_KEY] = mLastChatChunkIndex

        super.onCleared()
    }

    override fun generateDefaultUiState(): MateChatsUiState {
        return MateChatsUiState()
    }

    override fun processDomainResultFlow(domainResult: DomainResult): UiOperation? {
        val uiOperation = super.processDomainResultFlow(domainResult)

        if (uiOperation != null) return uiOperation

        return when (domainResult::class) {
            GetChatChunkDomainResult::class ->
                processGetChatChunkDomainResult(domainResult as GetChatChunkDomainResult)
            UpdateChatChunkDomainResult::class ->
                processUpdateChatChunkDomainResult(domainResult as UpdateChatChunkDomainResult)
            else -> null
        }
    }

    private fun processGetChatChunkDomainResult(
        getChatChunkResult: GetChatChunkDomainResult
    ): UiOperation? {
        if (mUiState.isLoading) changeLoadingState(false)

        mIsGettingNextChatChunk = false

        if (!getChatChunkResult.isSuccessful())
            return processErrorDomainResult(getChatChunkResult.error!!)

        if (getChatChunkResult.chunk == null) return null

        changeLastChatChunkIndex(mLastChatChunkIndex + 1)

        val chatPresentationChunk = processDomainChatChunk(getChatChunkResult.chunk)
        val chatChunkPosition = getChatChunkPositionByChunkIndex(getChatChunkResult.chunk.index)

        return InsertChatsUiOperation(chatChunkPosition, chatPresentationChunk)
    }

    private fun processUpdateChatChunkDomainResult(
        updateChatChunkResult: UpdateChatChunkDomainResult
    ): UiOperation {
        if (mUiState.isLoading) changeLoadingState(false)

        mIsGettingNextChatChunk = false

        if (!updateChatChunkResult.isSuccessful())
            return processErrorDomainResult(updateChatChunkResult.error!!)

        val prevChatChunkSize = mUiState.chatChunkSizes[updateChatChunkResult.chunk!!.index]
        val curChatChunkSize = updateChatChunkResult.chunk.chats.size

        val chatPresentationChunk = processDomainChatChunk(updateChatChunkResult.chunk)
        val chatChunkPosition = getChatChunkPositionByChunkIndex(updateChatChunkResult.chunk.index)

        return UpdateChatChunkUiOperation(
            chatChunkPosition,
            chatPresentationChunk,
            prevChatChunkSize - curChatChunkSize
        )
    }

    private fun processDomainChatChunk(chatChunk: MateChatChunk): List<MateChatPresentation> {
        val chatPresentationChunk = chatChunk.chats.map { it.toMateChatPresentation() }.toMutableList()
        val chatChunkSizesSize = mUiState.chatChunkSizes.size
        val chatPresentationChunkSize = chatPresentationChunk.size

        if (chatChunkSizesSize < chatChunk.index + 1) {
            mUiState.chatChunkSizes.add(chatPresentationChunkSize)
            mUiState.chats.addAll(chatPresentationChunk)

        } else {
            val prevChatChunkSize = mUiState.chatChunkSizes[chatChunk.index]
            val prevChatToRemovePosition =
                chatChunk.index * MateChatsUseCase.DEFAULT_CHAT_CHUNK_SIZE
            val prevChatsToRemove = mUiState.chats.subList(
                prevChatToRemovePosition, prevChatToRemovePosition + prevChatChunkSize)

            mUiState.chats.removeAll(prevChatsToRemove)
            mUiState.chats.addAll(prevChatToRemovePosition, chatPresentationChunk)

            mUiState.chatChunkSizes[chatChunk.index] = chatPresentationChunkSize
        }

        return chatPresentationChunk
    }

    private fun getChatChunkPositionByChunkIndex(index: Int): Int {
        return index * MateChatsUseCase.DEFAULT_CHAT_CHUNK_SIZE
    }

    private fun changeLastChatChunkIndex(newValue: Int) {
        mLastChatChunkIndex = newValue
    }

    // todo: reconsider this (mb there is another optimal way?): DOESNT WORK
    open fun getNextChatChunk() {
        if (!isNextChatChunkGettingAllowed()) return

        mIsGettingNextChatChunk = true

        changeLoadingState(true)

        mUseCase.getChatChunk(mLastChatChunkIndex)
    }

    open fun isNextChatChunkGettingAllowed(): Boolean {
        val prevChatChunkIndex = mLastChatChunkIndex - 1
        val chatCount = mUiState.chats.size

        val chunkSizeCheck = (prevChatChunkIndex < 0 ||
                (chatCount % MateChatsUseCase.DEFAULT_CHAT_CHUNK_SIZE == 0))

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

    /**
     * Returns a position of the deleted chat;
     */
    open fun removeDeletedChat(chatId: Long): Int {
        val chatToDeletePosition = mUiState.chats.indexOfFirst { it.id == chatId }

        mUiState.chats.removeAt(chatToDeletePosition)

        return chatToDeletePosition
    }
}

@Qualifier
annotation class MateChatsViewModelFactoryQualifier

class MateChatsViewModelFactory(
    private val mErrorDataRepository: ErrorDataRepository,
    private val mMateChatsUseCase: MateChatsUseCase
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (!modelClass.isAssignableFrom(MateChatsViewModel::class.java))
            throw IllegalArgumentException()

        return MateChatsViewModel(handle, mErrorDataRepository, mMateChatsUseCase) as T
    }
}