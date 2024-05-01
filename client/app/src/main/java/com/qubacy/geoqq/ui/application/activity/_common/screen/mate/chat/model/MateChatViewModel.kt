package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.authorized.result.error.ErrorWithLogoutDomainResult
import com.qubacy.geoqq.domain.mate.chat.projection.MateMessageChunk
import com.qubacy.geoqq.domain.mate.chat.usecase.MateChatUseCase
import com.qubacy.geoqq.domain.mate.chat.usecase.result.chunk.GetMessageChunkDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase.result.chunk.UpdateMessageChunkDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.result.interlocutor.GetInterlocutorDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.result.interlocutor.UpdateInterlocutorDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.result.interlocutor._common.InterlocutorDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase.result.message.SendMateMessageDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase.result.chat.DeleteChatDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase.result.SendMateRequestDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.validator.message.text.MessageTextValidator
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.AuthorizedViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.toMateMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.message.InsertMessagesUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.operation.MessageSentUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.operation.ShowInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.message.UpdateMessageChunkUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.request.ChatDeletedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.operation.MateRequestSentToInterlocutorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.operation.UpdateInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.state.MateChatUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
open class MateChatViewModel @Inject constructor(
    mSavedStateHandle: SavedStateHandle,
    mErrorDataRepository: ErrorDataRepository,
    mMateChatUseCase: MateChatUseCase
) : BusinessViewModel<MateChatUiState, MateChatUseCase>(
    mSavedStateHandle, mErrorDataRepository, mMateChatUseCase
), AuthorizedViewModel {
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

        val loadedMessageIds = mUiState.messages.map { it.id }
        val offset = mUiState.messages.size

        mUseCase.getMessageChunk(mUiState.chatContext!!.id, loadedMessageIds, offset)
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
        val lastMessageChunkSize = mUiState.messageChunkSizes.entries.lastOrNull()?.value

        val chunkSizeCheck = (
            lastMessageChunkSize == null ||
            (lastMessageChunkSize != 0 &&
                lastMessageChunkSize % MateChatUseCase.DEFAULT_MESSAGE_CHUNK_SIZE == 0
            )
        )

        return (!mIsGettingNextMessageChunk && chunkSizeCheck)
    }

    open fun isMessageTextValid(text: String): Boolean {
        return MessageTextValidator().isValid(text)
    }

    open fun getInterlocutorProfile(): UserPresentation {
        val interlocutor = mUiState.chatContext!!.user

        mUseCase.getInterlocutor(interlocutor.id)

        return interlocutor
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

    override fun processDomainResultFlow(domainResult: DomainResult): List<UiOperation> {
        val uiOperations = super.processDomainResultFlow(domainResult)

        if (uiOperations.isNotEmpty()) return uiOperations

        return when (domainResult::class) {
            GetMessageChunkDomainResult::class ->
                processGetMessageChunkDomainResult(domainResult as GetMessageChunkDomainResult)
            UpdateMessageChunkDomainResult::class ->
                processUpdateMessageChunkDomainResult(domainResult as UpdateMessageChunkDomainResult)
            GetInterlocutorDomainResult::class ->
                processGetInterlocutorDomainResult(domainResult as GetInterlocutorDomainResult)
            UpdateInterlocutorDomainResult::class ->
                processUpdateInterlocutorDomainResult(domainResult as UpdateInterlocutorDomainResult)
            SendMateRequestDomainResult::class ->
                processSendMateRequestToInterlocutorDomainResult(
                    domainResult as SendMateRequestDomainResult)
            DeleteChatDomainResult::class ->
                processDeleteChatDomainResult(domainResult as DeleteChatDomainResult)
            SendMateMessageDomainResult::class ->
                processSendMessageDomainResult(domainResult as SendMateMessageDomainResult)
            ErrorWithLogoutDomainResult::class ->
                processErrorWithLogoutDomainResult(domainResult as ErrorWithLogoutDomainResult)
            else -> listOf()
        }
    }

    private fun processSendMessageDomainResult(
        sendMessageResult: SendMateMessageDomainResult
    ): List<UiOperation> {
        if (mUiState.isLoading) changeLoadingState(false)

        if (!sendMessageResult.isSuccessful())
            return processErrorDomainResult(sendMessageResult.error!!)

        return listOf(MessageSentUiOperation())
    }

    private fun processSendMateRequestToInterlocutorDomainResult(
        sendMateRequestToInterlocutorResult: SendMateRequestDomainResult
    ): List<UiOperation> {
        if (mUiState.isLoading) changeLoadingState(false)

        if (!sendMateRequestToInterlocutorResult.isSuccessful())
            return processErrorDomainResult(sendMateRequestToInterlocutorResult.error!!)

        mUiState.isMateRequestSendingAllowed = false

        return listOf(MateRequestSentToInterlocutorUiOperation())
    }

    private fun processDeleteChatDomainResult(
        deleteChatDomainResult: DeleteChatDomainResult
    ): List<UiOperation> {
        if (mUiState.isLoading) changeLoadingState(false)

        if (!deleteChatDomainResult.isSuccessful())
            return processErrorDomainResult(deleteChatDomainResult.error!!)

        return listOf(ChatDeletedUiOperation())
    }

    private fun processGetInterlocutorDomainResult(
        getInterlocutorResult: GetInterlocutorDomainResult
    ): List<UiOperation> {
        if (!getInterlocutorResult.isSuccessful())
            return processErrorDomainResult(getInterlocutorResult.error!!)

        val userPresentation = processInterlocutorResult(getInterlocutorResult)

        return listOf(ShowInterlocutorDetailsUiOperation(userPresentation))
    }

    private fun processUpdateInterlocutorDomainResult(
        updateInterlocutorResult: UpdateInterlocutorDomainResult
    ): List<UiOperation> {
        if (!updateInterlocutorResult.isSuccessful())
            return processErrorDomainResult(updateInterlocutorResult.error!!)

        val userPresentation = processInterlocutorResult(updateInterlocutorResult)

        return listOf(UpdateInterlocutorDetailsUiOperation(userPresentation))
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
    ): List<UiOperation> {
        if (mUiState.isLoading) changeLoadingState(false)

        mIsGettingNextMessageChunk = false

        if (!getMessageChunkResult.isSuccessful())
            return processErrorDomainResult(getMessageChunkResult.error!!)

        if (getMessageChunkResult.chunk == null) return listOf()

        val messagePresentationChunk = processDomainMessageChunk(getMessageChunkResult.chunk)

        return listOf(
            InsertMessagesUiOperation(getMessageChunkResult.chunk.offset, messagePresentationChunk)
        )
    }

    private fun processUpdateMessageChunkDomainResult(
        updateMessageChunkResult: UpdateMessageChunkDomainResult
    ): List<UiOperation> {
        if (mUiState.isLoading) changeLoadingState(false)

        mIsGettingNextMessageChunk = false

        if (!updateMessageChunkResult.isSuccessful())
            return processErrorDomainResult(updateMessageChunkResult.error!!)

        val prevMessageChunkOffset = getPrevMessageChunkOffset(updateMessageChunkResult.chunk!!.offset)
        val prevMessageChunkSize = mUiState.messageChunkSizes[prevMessageChunkOffset]!!
        val curMessageChunkSize = updateMessageChunkResult.chunk.messages.size

        val messagePresentationChunk = processDomainMessageChunk(updateMessageChunkResult.chunk)

        return listOf(
            UpdateMessageChunkUiOperation(
                updateMessageChunkResult.chunk.offset,
                messagePresentationChunk,
                prevMessageChunkSize - curMessageChunkSize
            )
        )
    }

    private fun processDomainMessageChunk(
        messageChunk: MateMessageChunk
    ): List<MateMessagePresentation> {
        val messagePresentationChunk = messageChunk.messages.map { it.toMateMessagePresentation() }
        val messagePresentationChunkSize = messagePresentationChunk.size
        val chunkOffset = getPrevMessageChunkOffset(messageChunk.offset)

        if (!mUiState.messageChunkSizes.contains(chunkOffset)) {
            mUiState.messageChunkSizes[chunkOffset] = messagePresentationChunkSize
            mUiState.messages.addAll(messagePresentationChunk)

        } else {
            val prevMessageChunkSize = mUiState.messageChunkSizes[chunkOffset]!!
            val prevMessageToRemovePosition = messageChunk.offset
            val prevMessagesToRemove = mUiState.messages.subList(
                prevMessageToRemovePosition, prevMessageToRemovePosition + prevMessageChunkSize)

            mUiState.messages.removeAll(prevMessagesToRemove)
            mUiState.messages.addAll(prevMessageToRemovePosition, messagePresentationChunk)

            mUiState.messageChunkSizes[chunkOffset] = messagePresentationChunkSize
        }

        return messagePresentationChunk
    }

    private fun getPrevMessageChunkOffset(offset: Int): Int {
        return offset - mUiState.newMessageCount
    }

    open fun areMessageChunksInitialized(): Boolean {
        return mUiState.messageChunkSizes.isNotEmpty()
    }

    open fun resetMessageChunks() {
        mUiState.apply {
            newMessageCount = 0

            messageChunkSizes.clear()
            messages.clear()
        }
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