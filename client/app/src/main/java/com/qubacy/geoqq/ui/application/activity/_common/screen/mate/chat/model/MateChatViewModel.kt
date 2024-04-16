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
import com.qubacy.geoqq.domain.mate.chat.usecase.result.message.SendMessageDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase.result.request.DeleteChatDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase.result.request.SendMateRequestToInterlocutorDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.validator.message.text.MessageTextValidator
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.toMateMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.message.InsertMessagesUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.message.MessageSentUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.user.ShowInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.message.UpdateMessageChunkUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.request.ChatDeletedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.request.MateRequestSentToInterlocutorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.user.UpdateInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.state.MateChatUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
open class MateChatViewModel @Inject constructor(
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

    open fun isInterlocutorChatable(
        interlocutor: UserPresentation = mUiState.chatContext!!.user
    ): Boolean {
        return interlocutor.let { isInterlocutorMate(it) && !it.isDeleted }
    }

    open fun isInterlocutorMate(
        interlocutor: UserPresentation = mUiState.chatContext!!.user
    ): Boolean {
        return interlocutor.isMate
    }

    open fun isInterlocutorMateable(
        interlocutor: UserPresentation = mUiState.chatContext!!.user
    ): Boolean {
        return interlocutor.let { !isInterlocutorMate(it) && mUiState.isMateRequestSendingAllowed }
    }

    open fun isInterlocutorMateableOrDeletable(
        interlocutor: UserPresentation = mUiState.chatContext!!.user
    ): Boolean {
        return interlocutor.let { isInterlocutorMateable(it) || isInterlocutorMate(it) }
    }

    open fun isChatDeletable(
        interlocutor: UserPresentation = mUiState.chatContext!!.user
    ): Boolean {
        return interlocutor.let { it.isDeleted || !isInterlocutorMate(it) }
    }

    open fun isNextMessageChunkGettingAllowed(): Boolean {
        val prevMessageChunkIndex = mLastMessageChunkIndex - 1
        val prevMessageCount = mUiState.prevMessages.size

        val chunkSizeCheck = (prevMessageChunkIndex < 0 ||
                (prevMessageCount % MateChatUseCase.DEFAULT_MESSAGE_CHUNK_SIZE == 0))

        return (!mIsGettingNextMessageChunk && chunkSizeCheck)
    }

    open fun isMessageTextValid(text: String): Boolean {
        return MessageTextValidator().isValid(text)
    }

    open fun getInterlocutorProfile() {
        val contextUser = mUiState.chatContext!!.user

        viewModelScope.launch {
            mUiOperationFlow.emit(ShowInterlocutorDetailsUiOperation(contextUser))
        }

        mUseCase.getInterlocutor(contextUser.id)
    }

    open fun addInterlocutorAsMate() {
        changeLoadingState(true)
        mUseCase.sendMateRequestToInterlocutor(mUiState.chatContext!!.user.id)
    }

    open fun deleteChat() {
        changeLoadingState(true)
        mUseCase.deleteChat(mUiState.chatContext!!.id)
    }

    open fun sendMessage(text: String) {
        changeLoadingState(true)
        mUseCase.sendMessage(mUiState.chatContext!!.id, text)
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
            SendMateRequestToInterlocutorDomainResult::class ->
                processSendMateRequestToInterlocutorDomainResult(
                    domainResult as SendMateRequestToInterlocutorDomainResult)
            DeleteChatDomainResult::class ->
                processDeleteChatDomainResult(domainResult as DeleteChatDomainResult)
            SendMessageDomainResult::class ->
                processSendMessageDomainResult(domainResult as SendMessageDomainResult)
            else -> null
        }
    }

    private fun processSendMessageDomainResult(
        sendMessageResult: SendMessageDomainResult
    ): UiOperation {
        if (mUiState.isLoading) changeLoadingState(false)

        if (!sendMessageResult.isSuccessful())
            return processErrorDomainResult(sendMessageResult.error!!)

        return MessageSentUiOperation()
    }

    private fun processSendMateRequestToInterlocutorDomainResult(
        sendMateRequestToInterlocutorResult: SendMateRequestToInterlocutorDomainResult
    ): UiOperation {
        if (mUiState.isLoading) changeLoadingState(false)

        if (!sendMateRequestToInterlocutorResult.isSuccessful())
            return processErrorDomainResult(sendMateRequestToInterlocutorResult.error!!)

        mUiState.isMateRequestSendingAllowed = false

        return MateRequestSentToInterlocutorUiOperation()
    }

    private fun processDeleteChatDomainResult(
        deleteChatDomainResult: DeleteChatDomainResult
    ): UiOperation {
        if (mUiState.isLoading) changeLoadingState(false)

        if (!deleteChatDomainResult.isSuccessful())
            return processErrorDomainResult(deleteChatDomainResult.error!!)

        return ChatDeletedUiOperation()
    }

    private fun processGetInterlocutorDomainResult(
        getInterlocutorResult: GetInterlocutorDomainResult
    ): UiOperation {
        if (!getInterlocutorResult.isSuccessful())
            return processErrorDomainResult(getInterlocutorResult.error!!)

        val userPresentation = processInterlocutorResult(getInterlocutorResult)

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

        val prevMessageChunkSize = mUiState.prevMessageChunkSizes[updateMessageChunkResult.chunk!!.index]
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
        val prevMessageChunkSizesSize = mUiState.prevMessageChunkSizes.size
        val messagePresentationChunkSize = messagePresentationChunk.size

        if (prevMessageChunkSizesSize < messageChunk.index + 1) {
            mUiState.prevMessageChunkSizes.add(messagePresentationChunkSize)
            mUiState.prevMessages.addAll(messagePresentationChunk)

        } else {
            val prevMessageChunkSize = mUiState.prevMessageChunkSizes[messageChunk.index]
            val prevMessageToRemovePosition =
                messageChunk.index * MateChatUseCase.DEFAULT_MESSAGE_CHUNK_SIZE
            val prevMessagesToRemove = mUiState.prevMessages.subList(
                prevMessageToRemovePosition, prevMessageToRemovePosition + prevMessageChunkSize)

            mUiState.prevMessages.removeAll(prevMessagesToRemove)
            mUiState.prevMessages.addAll(prevMessageToRemovePosition, messagePresentationChunk)

            mUiState.prevMessageChunkSizes[messageChunk.index] = messagePresentationChunkSize
        }

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