package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common

import androidx.lifecycle.SavedStateHandle
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain.mate.chat.usecase._common.MateChatUseCase
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chat.DeleteChatDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chunk.GetMessageChunkDomainResult
import com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chunk.UpdateMessageChunkDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.AuthorizedViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.result.handler.AuthorizedDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.ChatViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.result.handler.ChatDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.InterlocutorViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.result.handler.InterlocutorDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.result.handler.MateChatDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.state.MateChatUiState

abstract class MateChatViewModel(
    mSavedStateHandle: SavedStateHandle,
    mErrorSource: LocalErrorDatabaseDataSource,
    mMateChatUseCase: MateChatUseCase
) : BusinessViewModel<MateChatUiState, MateChatUseCase>(
    mSavedStateHandle, mErrorSource, mMateChatUseCase
), AuthorizedViewModel, InterlocutorViewModel, ChatViewModel {
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
    abstract fun getNextMessageChunk()
    abstract fun isInterlocutorChatable(interlocutor: UserPresentation): Boolean
    abstract fun isInterlocutorMate(interlocutor: UserPresentation): Boolean
    abstract fun isInterlocutorMateable(interlocutor: UserPresentation): Boolean
    abstract fun isInterlocutorMateableOrDeletable(interlocutor: UserPresentation): Boolean
    abstract fun isChatDeletable(interlocutor: UserPresentation): Boolean
    abstract fun isNextMessageChunkGettingAllowed(): Boolean
    abstract fun isMessageTextValid(text: String): Boolean
    abstract fun getInterlocutorProfile(): UserPresentation
    abstract fun addInterlocutorAsMate()
    abstract fun deleteChat()
    abstract fun sendMessage(text: String)
    abstract fun onMateChatDeleteChat(
        deleteChatDomainResult: DeleteChatDomainResult
    ): List<UiOperation>
    abstract fun onMateChatGetMessageChunk(
        getMessageChunkResult: GetMessageChunkDomainResult
    ): List<UiOperation>
    abstract fun onMateChatUpdateMessageChunk(
        updateMessageChunkResult: UpdateMessageChunkDomainResult
    ): List<UiOperation>
    open fun isInterlocutorChatable(): Boolean {
        return isInterlocutorChatable(mUiState.chatContext!!.user)
    }
    open fun isInterlocutorMate(): Boolean {
        return isInterlocutorMate(mUiState.chatContext!!.user)
    }
    open fun isInterlocutorMateable(): Boolean {
        return isInterlocutorMateable(mUiState.chatContext!!.user)
    }
    open fun isInterlocutorMateableOrDeletable(): Boolean {
        return isInterlocutorMateableOrDeletable(mUiState.chatContext!!.user)
    }
    open fun isChatDeletable(): Boolean {
        return isChatDeletable(mUiState.chatContext!!.user)
    }
    open fun areMessageChunksInitialized(): Boolean {
        return mUiState.messageChunkSizes.isNotEmpty()
    }
    open fun setChatContext(chat: MateChatPresentation) {
        mUiState.chatContext = chat
    }
    open fun resetMessageChunks() {
        mUiState.apply {
            newMessageCount = 0

            messageChunkSizes.clear()
            messages.clear()
        }
    }
}