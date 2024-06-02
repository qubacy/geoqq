package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.impl

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain.mate.chats.projection.MateChatChunk
import com.qubacy.geoqq.domain.mate.chats.usecase._common.MateChatsUseCase
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chat.added.MateChatAddedDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chat.updated.MateChatUpdatedDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk.get.GetMateChatChunkDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk.update.UpdateMateChatChunkDomainResult
import com.qubacy.geoqq.domain.user.usecase._common.result._common.UserDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.toMateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.MateChatsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.operation.chat.add.AddChatUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.operation.chat.insert.InsertChatsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.operation.chat.update.UpdateChatUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.operation.chunk.update.UpdateChatChunkUiOperation
import javax.inject.Inject
import javax.inject.Qualifier

open class MateChatsViewModelImpl @Inject constructor(
    mSavedStateHandle: SavedStateHandle,
    mErrorSource: LocalErrorDatabaseDataSource,
    mUseCase: MateChatsUseCase
) : MateChatsViewModel(mSavedStateHandle, mErrorSource, mUseCase) {
    companion object {
        const val TAG = "MateChatsViewModel"
    }

    private var mIsGettingNextChatChunk: Boolean = false

    override fun onMateChatsGetChatChunk(
        getChatChunkResult: GetMateChatChunkDomainResult
    ): List<UiOperation> {
        if (mUiState.isLoading) changeLoadingState(false)

        mIsGettingNextChatChunk = false

        if (!getChatChunkResult.isSuccessful())
            return onError(getChatChunkResult.error!!)

        if (getChatChunkResult.chunk == null) return listOf()

        val chatPresentationChunk = processDomainChatChunk(getChatChunkResult.chunk)

        return listOf(InsertChatsUiOperation(getChatChunkResult.chunk.offset, chatPresentationChunk))
    }

    override fun onMateChatsUpdateChatChunk(
        updateChatChunkResult: UpdateMateChatChunkDomainResult
    ): List<UiOperation> {
        if (mUiState.isLoading) changeLoadingState(false)

        if (!updateChatChunkResult.isSuccessful())
            return onError(updateChatChunkResult.error!!)

        val prevChatChunkOffset = getPrevChatChunkOffset(updateChatChunkResult.chunk!!.offset)
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

    override fun onMateChatsMateChatAdded(
        mateChatAddedDomainResult: MateChatAddedDomainResult
    ): List<UiOperation> {
        if (!mateChatAddedDomainResult.isSuccessful())
            return onError(mateChatAddedDomainResult.error!!)

        val chatPresentation = mateChatAddedDomainResult.chat!!.toMateChatPresentation()

        mUiState.chats.add(0, chatPresentation)

        return listOf(AddChatUiOperation(chatPresentation))
    }

    override fun onMateChatsMateChatUpdated(
        mateChatUpdatedDomainResult: MateChatUpdatedDomainResult
    ): List<UiOperation> {
        if (!mateChatUpdatedDomainResult.isSuccessful())
            return onError(mateChatUpdatedDomainResult.error!!)

        val chatPresentation = mateChatUpdatedDomainResult.chat!!.toMateChatPresentation()
        val prevChatPresentation = mUiState.chats.find { it.id == chatPresentation.id }

        if (prevChatPresentation == null) {
            if (chatPresentation.lastMessage == null) return emptyList()

            val lastChatPresentation = mUiState.chats.last()

            if (lastChatPresentation.lastMessage != null) {
                if (chatPresentation.lastMessage.timeInSeconds <
                    lastChatPresentation.lastMessage.timeInSeconds
                ) {
                    return emptyList()
                }
            }

            // todo: implement the rest..



        } else {

        }

        return listOf(UpdateChatUiOperation(, chatPresentation))
    }

    // todo: reconsider this (mb there is another optimal way?): DOESNT WORK
    override fun getNextChatChunk() {
        if (!isNextChatChunkGettingAllowed()) return

        mIsGettingNextChatChunk = true

        changeLoadingState(true)

        val loadedChatIds = mUiState.chats.map { it.id }
        val offset = mUiState.chats.size

        mUseCase.getChatChunk(loadedChatIds, offset)
    }

    override fun isNextChatChunkGettingAllowed(): Boolean {
        val lastChatChunkSize = mUiState.chatChunkSizes.entries.lastOrNull()?.value

        val chunkSizeCheck = (
            lastChatChunkSize == null ||
            (lastChatChunkSize != 0 &&
                lastChatChunkSize % MateChatsUseCase.DEFAULT_CHAT_CHUNK_SIZE == 0
            )
        )

        return (!mIsGettingNextChatChunk && chunkSizeCheck)
    }

    override fun prepareChatForEntering(chatId: Long): MateChatPresentation {
        for (chatIndex in mUiState.chats.indices) {
            val chat = mUiState.chats[chatIndex]

            if (chat.id == chatId) {
                mUiState.chats[chatIndex] = chat.copy(newMessageCount = 0)

                return mUiState.chats[chatIndex]
            }
        }

        throw IllegalStateException()
    }

    override fun generateUserGetUserUiOperations(
        userPresentation: UserPresentation
    ): List<UiOperation> {
        return emptyList()
    }

    override fun generateUserUpdateUserUiOperations(
        userPresentation: UserPresentation
    ): List<UiOperation> {
        // todo: implement..

        return emptyList()
    }

    override fun onUserUser(domainResult: UserDomainResult): UserPresentation {
        // todo: implement..

        return domainResult.interlocutor!!.toUserPresentation()
    }

    override fun getUserViewModelBusinessViewModel(): BusinessViewModel<*, *> {
        return this
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
                prevChatToRemovePosition, prevChatToRemovePosition + prevChatChunkSize
            ).toMutableList()

            mUiState.chats.removeAll(prevChatsToRemove)
            mUiState.chats.addAll(prevChatToRemovePosition, chatPresentationChunk)

            mUiState.chatChunkSizes[chunkOffset] = chatPresentationChunkSize
        }

        return chatPresentationChunk
    }

    private fun getPrevChatChunkOffset(offset: Int): Int {
        return offset - mUiState.newChatCount
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

class MateChatsViewModelImplFactory @Inject constructor(
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mMateChatsUseCase: MateChatsUseCase
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (!modelClass.isAssignableFrom(MateChatsViewModelImpl::class.java))
            throw IllegalArgumentException()

        return MateChatsViewModelImpl(handle, mErrorSource, mMateChatsUseCase) as T
    }
}