package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.mate.chat.projection.MateMessageChunk
import com.qubacy.geoqq.domain.mate.chat.usecase.MateChatUseCase
import com.qubacy.geoqq.domain.mate.chat.usecase.result.chunk.GetMessageChunkDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase.result.chunk.UpdateMessageChunkDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase.result.interlocutor.GetInterlocutorDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase.result.interlocutor.UpdateInterlocutorDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase.result.interlocutor._common.InterlocutorDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.toMateMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.message.InsertMessagesUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.user.ShowInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.message.UpdateMessageChunkUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.user.UpdateInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.state.MateChatUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
class MateChatViewModel @Inject constructor(
    mSavedStateHandle: SavedStateHandle,
    mErrorDataRepository: ErrorDataRepository,
    mMateChatUseCase: MateChatUseCase
) : BusinessViewModel<MateChatUiState, MateChatUseCase>(
    mSavedStateHandle, mErrorDataRepository, mMateChatUseCase
) {
    private var mLastMessageChunkIndex: Int = 0
    private var mIsGettingNextMessageChunk = false

    override fun generateDefaultUiState(): MateChatUiState {
        return MateChatUiState()
    }

    // todo: is it ok?:
    open fun setChatContext(chat: MateChatPresentation) {
        mUiState.chatContext = chat
    }

    open fun getNextMessageChunk() {
        if (!isNextMessageChunkGettingAllowed()) return

        mIsGettingNextMessageChunk = true

        changeLoadingState(true)

        mUseCase.getMessageChunk(mUiState.chatContext!!.id, mLastMessageChunkIndex)
    }

    open fun isNextMessageChunkGettingAllowed(): Boolean {
        val prevMessageChunkIndex = mLastMessageChunkIndex - 1
        val prevChunkSize = mUiState.messageChunks[prevMessageChunkIndex]?.size

        val chunkSizeCheck = (prevMessageChunkIndex < 0 || (prevChunkSize != null
                && prevChunkSize >= MateChatUseCase.DEFAULT_MESSAGE_CHUNK_SIZE))

        return (!mIsGettingNextMessageChunk && chunkSizeCheck)
    }

    open fun getInterlocutorProfile() {
        val contextUser = mUiState.chatContext!!.user

        viewModelScope.launch {
            mUiOperationFlow.emit(ShowInterlocutorDetailsUiOperation(contextUser))
        }

        mUseCase.getInterlocutor(contextUser.id)
    }

    override fun processDomainResultFlow(domainResult: DomainResult): UiOperation? {
        val uiOperation = super.processDomainResultFlow(domainResult)

        if (uiOperation != null) return uiOperation

        return when (domainResult::class) {
            GetMessageChunkDomainResult::class ->
                processGetMessageChunkDomainResult(domainResult as GetMessageChunkDomainResult)
            UpdateMessageChunkDomainResult::class ->
                processUpdateMessageChunkDomainResult(domainResult as UpdateMessageChunkDomainResult)
            GetInterlocutorDomainResult::class ->
                processGetInterlocutorDomainResult(domainResult as GetInterlocutorDomainResult)
            UpdateInterlocutorDomainResult::class ->
                processUpdateInterlocutorDomainResult(domainResult as UpdateInterlocutorDomainResult)
            else -> null
        }
    }

    private fun processGetInterlocutorDomainResult(
        getInterlocutorResult: GetInterlocutorDomainResult
    ): UiOperation {
        if (!getInterlocutorResult.isSuccessful())
            return processErrorDomainResult(getInterlocutorResult.error!!)

        val userPresentation = getInterlocutorResult.interlocutor!!.toUserPresentation()

        return ShowInterlocutorDetailsUiOperation(userPresentation)
    }

    private fun processUpdateInterlocutorDomainResult(
        updateInterlocutorResult: UpdateInterlocutorDomainResult
    ): UiOperation {
        if (!updateInterlocutorResult.isSuccessful())
            return processErrorDomainResult(updateInterlocutorResult.error!!)

        val userPresentation = processInterlocutorResult(updateInterlocutorResult)

        return UpdateInterlocutorDetailsUiOperation(userPresentation)
    }

    private fun processInterlocutorResult(
        interlocutorResult: InterlocutorDomainResult
    ): UserPresentation {
        val userPresentation = interlocutorResult.interlocutor!!.toUserPresentation()

        mUiState.chatContext = mUiState.chatContext?.copy(user = userPresentation)

        return userPresentation
    }

    private fun processGetMessageChunkDomainResult(
        getMessageChunkResult: GetMessageChunkDomainResult
    ): UiOperation? {
        if (mUiState.isLoading) changeLoadingState(false)

        mIsGettingNextMessageChunk = false

        if (!getMessageChunkResult.isSuccessful())
            return processErrorDomainResult(getMessageChunkResult.error!!)

        if (getMessageChunkResult.chunk == null) return null

        changeLastMessageChunkIndex(mLastMessageChunkIndex + 1)

        val messagePresentationChunk = processDomainMessageChunk(getMessageChunkResult.chunk)
        val chatChunkPosition = getMessageChunkPositionByChunkIndex(getMessageChunkResult.chunk.index)

        return InsertMessagesUiOperation(chatChunkPosition, messagePresentationChunk)
    }

    private fun processUpdateMessageChunkDomainResult(
        updateMessageChunkResult: UpdateMessageChunkDomainResult
    ): UiOperation {
        if (mUiState.isLoading) changeLoadingState(false)

        mIsGettingNextMessageChunk = false

        if (!updateMessageChunkResult.isSuccessful())
            return processErrorDomainResult(updateMessageChunkResult.error!!)

        val prevMessageChunkSize = mUiState.messageChunks[updateMessageChunkResult.chunk!!.index]!!.size
        val curMessageChunkSize = updateMessageChunkResult.chunk.messages.size

        val messagePresentationChunk = processDomainMessageChunk(updateMessageChunkResult.chunk)
        val messageChunkPosition = getMessageChunkPositionByChunkIndex(
            updateMessageChunkResult.chunk.index)

        return UpdateMessageChunkUiOperation(
            messageChunkPosition,
            messagePresentationChunk,
            prevMessageChunkSize - curMessageChunkSize
        )
    }

    private fun processDomainMessageChunk(
        messageChunk: MateMessageChunk
    ): List<MateMessagePresentation> {
        val messagePresentationChunk = messageChunk.messages.map { it.toMateMessagePresentation() }

        mUiState.messageChunks[messageChunk.index] = messagePresentationChunk

        return messagePresentationChunk
    }

    private fun changeLastMessageChunkIndex(newValue: Int) {
        mLastMessageChunkIndex = newValue
    }

    private fun getMessageChunkPositionByChunkIndex(index: Int): Int {
        return index * MateChatUseCase.DEFAULT_MESSAGE_CHUNK_SIZE
    }
}

@Qualifier
annotation class MateChatViewModelFactoryQualifier

class MateChatViewModelFactory(
    private val mErrorDataRepository: ErrorDataRepository,
    private val mMateChatUseCase: MateChatUseCase
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (!modelClass.isAssignableFrom(MateChatViewModel::class.java))
            throw IllegalArgumentException()

        return MateChatViewModel(handle, mErrorDataRepository, mMateChatUseCase) as T
    }
}