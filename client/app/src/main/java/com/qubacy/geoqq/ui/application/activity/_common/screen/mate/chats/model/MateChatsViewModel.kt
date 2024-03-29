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
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats._common.presentation.toMateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.operation.InsertChatsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.operation.UpdateChatChunkUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.state.MateChatsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
class MateChatsViewModel @Inject constructor(
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
    ): UiOperation {
        changeLoadingState(false)
        changeLastChatChunkIndex(mLastChatChunkIndex + 1)

        if (!getChatChunkResult.isSuccessful())
            return processErrorDomainResult(getChatChunkResult.error!!)

        val chatPresentationChunk = processDomainChatChunk(getChatChunkResult.chunk!!)
        val chatChunkPosition = getChatChunkPositionByChunkIndex(getChatChunkResult.chunk.index)

        return InsertChatsUiOperation(chatChunkPosition, chatPresentationChunk)
    }

    private fun processUpdateChatChunkDomainResult(
        updateChatChunkResult: UpdateChatChunkDomainResult
    ): UiOperation {
        if (mUiState.isLoading) changeLoadingState(false)

        if (!updateChatChunkResult.isSuccessful())
            return processErrorDomainResult(updateChatChunkResult.error!!)

        val chatPresentationChunk = processDomainChatChunk(updateChatChunkResult.chunk!!)
        val chatChunkPosition = getChatChunkPositionByChunkIndex(updateChatChunkResult.chunk.index)

        return UpdateChatChunkUiOperation(chatChunkPosition, chatPresentationChunk)
    }

    private fun processDomainChatChunk(chatChunk: MateChatChunk): List<MateChatPresentation> {
        val chatPresentationChunk = chatChunk.chats.map { it.toMateChatPresentation() }

        mUiState.chatChunks[chatChunk.index] = chatPresentationChunk

        return chatPresentationChunk
    }

    private fun getChatChunkPositionByChunkIndex(index: Int): Int {
        return index * MateChatsUseCase.DEFAULT_CHAT_CHUNK_SIZE
    }

    private fun changeLastChatChunkIndex(newValue: Int) {
        mLastChatChunkIndex = newValue
    }

    fun getNextChatChunk() {
        changeLoadingState(true)

        mUseCase.getChatChunk(mLastChatChunkIndex)
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