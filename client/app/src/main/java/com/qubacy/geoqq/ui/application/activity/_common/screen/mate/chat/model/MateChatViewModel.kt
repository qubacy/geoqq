package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain.mate.chat.projection.MateMessageChunk
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chunk.GetMessageChunkDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chunk.UpdateMessageChunkDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase._common.result.interlocutor._common.InterlocutorDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase._common.MateChatUseCase
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chat.DeleteChatDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase._common.result.SendMateRequestDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.validator.message.text.MessageTextValidator
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.AuthorizedViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.result.handler.AuthorizedDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.ChatViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.toMateMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.message.InsertMessagesUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.message.UpdateMessageChunkUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.operation.request.ChatDeletedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.operation.MateRequestSentToInterlocutorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.result.handler.ChatDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.InterlocutorViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.result.handler.InterlocutorDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.result.handler.MateChatDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.state.MateChatUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
open class MateChatViewModel @Inject constructor(
    mSavedStateHandle: SavedStateHandle,
    mErrorSource: LocalErrorDatabaseDataSource,
    mMateChatUseCase: MateChatUseCase
) : BusinessViewModel<MateChatUiState, MateChatUseCase>(
    mSavedStateHandle, mErrorSource, mMateChatUseCase
), AuthorizedViewModel, InterlocutorViewModel, ChatViewModel {
    private var mIsGettingNextMessageChunk = false

    override fun generateDomainResultHandlers(): Array<DomainResultHandler<*>> {
        return super.generateDomainResultHandlers()
            .plus(AuthorizedDomainResultHandler(this))
            .plus(InterlocutorDomainResultHandler(this))
            .plus(ChatDomainResultHandler(this))
            .plus(MateChatDomainResultHandler(this))
    }

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

    override fun onChatSendMateRequest(domainResult: SendMateRequestDomainResult): List<UiOperation> {
        if (mUiState.isLoading) changeLoadingState(false)

        if (!domainResult.isSuccessful()) return onError(domainResult.error!!)

        mUiState.isMateRequestSendingAllowed = false

        return listOf(MateRequestSentToInterlocutorUiOperation())
    }

    fun onMateChatDeleteChat(
        deleteChatDomainResult: DeleteChatDomainResult
    ): List<UiOperation> {
        if (mUiState.isLoading) changeLoadingState(false)

        if (!deleteChatDomainResult.isSuccessful())
            return onError(deleteChatDomainResult.error!!)

        return listOf(ChatDeletedUiOperation())
    }

    fun onMateChatGetMessageChunk(
        getMessageChunkResult: GetMessageChunkDomainResult
    ): List<UiOperation> {
        if (mUiState.isLoading) changeLoadingState(false)

        mIsGettingNextMessageChunk = false

        if (!getMessageChunkResult.isSuccessful())
            return onError(getMessageChunkResult.error!!)

        if (getMessageChunkResult.chunk == null) return listOf()

        val messagePresentationChunk = processDomainMessageChunk(getMessageChunkResult.chunk)

        return listOf(
            InsertMessagesUiOperation(getMessageChunkResult.chunk.offset, messagePresentationChunk)
        )
    }

    fun onMateChatUpdateMessageChunk(
        updateMessageChunkResult: UpdateMessageChunkDomainResult
    ): List<UiOperation> {
        if (mUiState.isLoading) changeLoadingState(false)

        mIsGettingNextMessageChunk = false

        if (!updateMessageChunkResult.isSuccessful())
            return onError(updateMessageChunkResult.error!!)

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
                prevMessageToRemovePosition, prevMessageToRemovePosition + prevMessageChunkSize
            ).toMutableList()

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

    override fun onInterlocutorInterlocutor(domainResult: InterlocutorDomainResult): UserPresentation {
        val userPresentation = domainResult.interlocutor!!.toUserPresentation()

        mUiState.chatContext = mUiState.chatContext?.copy(user = userPresentation)

        return userPresentation
    }

    override fun getChatViewModelBusinessViewModel(): BusinessViewModel<*, *> {
        return this
    }

    override fun getInterlocutorViewModelBusinessViewModel(): BusinessViewModel<*, *> {
        return this
    }
}

@Qualifier
annotation class MateChatViewModelFactoryQualifier

class MateChatViewModelFactory(
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mMateChatUseCase: MateChatUseCase
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (!modelClass.isAssignableFrom(MateChatViewModel::class.java))
            throw IllegalArgumentException()

        return MateChatViewModel(handle, mErrorSource, mMateChatUseCase) as T
    }
}