package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase.MateChatsUseCase
import com.qubacy.geoqq.domain.mate.chats.usecase.result.GetChatChunkDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats._common.presentation.toMateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.operation.InsertChatsUiOperation
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

        val chatChunk = getChatChunkResult.chunk!!

        val chatPresentationChunk = chatChunk.chats.map { it.toMateChatPresentation() }
        val chatChunkPosition = chatChunk.index * MateChatsUseCase.DEFAULT_CHAT_CHUNK_SIZE

        mUiState.chatChunks[getChatChunkResult.chunk.index] = chatPresentationChunk

        return InsertChatsUiOperation(chatChunkPosition, chatPresentationChunk)
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