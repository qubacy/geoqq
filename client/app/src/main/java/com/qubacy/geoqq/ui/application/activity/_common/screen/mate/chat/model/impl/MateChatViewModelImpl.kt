package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.impl

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain._common.usecase.aspect.chat.result.SendMessageDomainResult
import com.qubacy.geoqq.domain.user.usecase._common.result.get.GetUserDomainResult
import com.qubacy.geoqq.domain._common.usecase.aspect.user.result.update.UserUpdatedDomainResult
import com.qubacy.geoqq.domain.mate.chat.projection.MateMessageChunk
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chunk.GetMessageChunkDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chunk.UpdateMessageChunkDomainResult
import com.qubacy.geoqq.domain._common.usecase.aspect.user.result._common.UserDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase._common.MateChatUseCase
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chat.delete.DeleteChatDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.message.MateMessageAddedDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase._common.result.SendMateRequestDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.validator.message.text.MessageTextValidator
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.toMateMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.operation.message.insert.InsertMessagesUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.operation.message.update.UpdateMessageChunkUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.operation.request.ChatDeletedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.operation.MateRequestSentToInterlocutorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.MateChatViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.operation.context.ChatContextUpdatedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.operation.message.add.AddMessageUiOperation
import javax.inject.Inject
import javax.inject.Qualifier

open class MateChatViewModelImpl @Inject constructor(
    mSavedStateHandle: SavedStateHandle,
    mErrorSource: LocalErrorDatabaseDataSource,
    mMateChatUseCase: MateChatUseCase
) : MateChatViewModel(mSavedStateHandle, mErrorSource, mMateChatUseCase) {
    private var mIsGettingNextMessageChunk = false

    override fun getNextMessageChunk() {
        if (!isNextMessageChunkGettingAllowed()) return

        mIsGettingNextMessageChunk = true

        changeLoadingState(true)

        val loadedMessageIds = mUiState.messages.map { it.id }
        val offset = mUiState.messages.size

        mUseCase.getMessageChunk(mUiState.chatContext!!.id, loadedMessageIds, offset)
    }

    override fun isInterlocutorChatable(interlocutor: UserPresentation): Boolean {
        return interlocutor.let { it.isMate && !it.isDeleted }
    }

    override fun isInterlocutorMateable(interlocutor: UserPresentation): Boolean {
        return interlocutor.let { !it.isMate && mUiState.isMateRequestSendingAllowed }
    }

    override fun isInterlocutorMateableOrDeletable(interlocutor: UserPresentation): Boolean {
        return interlocutor.let { isInterlocutorMateable(it) || it.isMate }
    }

    override fun isChatDeletable(interlocutor: UserPresentation): Boolean {
        return interlocutor.let { it.isDeleted || !it.isMate }
    }

    override fun isNextMessageChunkGettingAllowed(): Boolean {
        val lastMessageChunkSize = mUiState.messageChunkSizes.entries.lastOrNull()?.value

        val chunkSizeCheck = (
            lastMessageChunkSize == null ||
            (lastMessageChunkSize != 0 &&
                lastMessageChunkSize % MateChatUseCase.DEFAULT_MESSAGE_CHUNK_SIZE == 0
            )
        )

        return (!mIsGettingNextMessageChunk && chunkSizeCheck)
    }

    override fun isMessageTextValid(text: String): Boolean {
        return MessageTextValidator().isValid(text)
    }

    override fun getInterlocutorProfile(): UserPresentation {
        val interlocutor = mUiState.chatContext!!.user

        mUseCase.getInterlocutor(interlocutor.id)

        return interlocutor
    }

    override fun addInterlocutorAsMate() {
        changeLoadingState(true)
        mUseCase.sendMateRequestToInterlocutor(mUiState.chatContext!!.user.id)
    }

    override fun deleteChat() {
        changeLoadingState(true)
        mUseCase.deleteChat(mUiState.chatContext!!.id)
    }

    override fun sendMessage(text: String) {
        changeLoadingState(true)
        mUseCase.sendMessage(mUiState.chatContext!!.id, text)
    }

    override fun onChatSendMateRequest(domainResult: SendMateRequestDomainResult): List<UiOperation> {
        if (mUiState.isLoading) changeLoadingState(false)

        if (!domainResult.isSuccessful()) return onError(domainResult.error!!)

        mUiState.isMateRequestSendingAllowed = false

        return listOf(MateRequestSentToInterlocutorUiOperation())
    }

    override fun onMateChatDeleteChat(
        deleteChatDomainResult: DeleteChatDomainResult
    ): List<UiOperation> {
        if (mUiState.isLoading) changeLoadingState(false)

        if (!deleteChatDomainResult.isSuccessful())
            return onError(deleteChatDomainResult.error!!)

        return listOf(ChatDeletedUiOperation())
    }

    override fun onMateChatMessageAdded(
        mateMessageAddedDomainResult: MateMessageAddedDomainResult
    ): List<UiOperation> {
        if (!mateMessageAddedDomainResult.isSuccessful())
            return onError(mateMessageAddedDomainResult.error!!)

        val messagePresentation = mateMessageAddedDomainResult.message!!.toMateMessagePresentation()

        mUiState.messages.add(messagePresentation)

        return listOf(AddMessageUiOperation(messagePresentation))
    }

    override fun onMateChatSendMessage(
        sendMessageDomainResult: SendMessageDomainResult
    ): List<UiOperation> {
        if (!sendMessageDomainResult.isSuccessful())
            return onError(sendMessageDomainResult.error!!)

        return emptyList() // todo: nothing to do?
    }

    override fun onMateChatGetMessageChunk(
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

    override fun onMateChatUpdateMessageChunk(
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

    override fun onUserGetUser(
        domainResult: GetUserDomainResult
    ): List<UiOperation> {
        val prevUserPresentation = mUiState.chatContext!!.user
        val superOperations = super.onUserGetUser(domainResult)

        if (!domainResult.isSuccessful()) return superOperations

        val userPresentation = domainResult.interlocutor!!.toUserPresentation()

        return superOperations.also {
            if (userPresentation == prevUserPresentation) it
            else it.plus(ChatContextUpdatedUiOperation(mUiState.chatContext!!))
        }
    }

    override fun onUserUpdateUser(
        domainResult: UserUpdatedDomainResult
    ): List<UiOperation> {
        val superOperations = super.onUserUpdateUser(domainResult)

        if (!domainResult.isSuccessful()) return superOperations

        return superOperations
            .plus(ChatContextUpdatedUiOperation(mUiState.chatContext!!))
    }

    override fun onUserUser(
        domainResult: UserDomainResult
    ): UserPresentation {
        val userPresentation = domainResult.interlocutor!!.toUserPresentation()

        mUiState.chatContext = mUiState.chatContext!!.copy(user = userPresentation)

        return userPresentation
    }

    override fun getChatViewModelBusinessViewModel(): BusinessViewModel<*, *> {
        return this
    }

    override fun getUserViewModelBusinessViewModel(): BusinessViewModel<*, *> {
        return this
    }
}

@Qualifier
annotation class MateChatViewModelFactoryQualifier

class MateChatViewModelImplFactory @Inject constructor(
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mMateChatUseCase: MateChatUseCase
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (!modelClass.isAssignableFrom(MateChatViewModelImpl::class.java))
            throw IllegalArgumentException()

        return MateChatViewModelImpl(handle, mErrorSource, mMateChatUseCase) as T
    }
}