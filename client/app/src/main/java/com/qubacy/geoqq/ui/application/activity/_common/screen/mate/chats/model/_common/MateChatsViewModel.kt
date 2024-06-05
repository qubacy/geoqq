package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common

import androidx.lifecycle.SavedStateHandle
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain.mate.chats.usecase._common.MateChatsUseCase
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chat.added.MateChatAddedDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chat.updated.MateChatUpdatedDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk.get.GetMateChatChunkDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk.update.UpdateMateChatChunkDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.AuthorizedViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.result.handler.AuthorizedDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.user.model.UserViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.user.model.result.handler.UserDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.result.handler.MateChatsDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.state.MateChatsUiState

abstract class MateChatsViewModel(
    mSavedStateHandle: SavedStateHandle,
    mErrorSource: LocalErrorDatabaseDataSource,
    mUseCase: MateChatsUseCase
) : BusinessViewModel<MateChatsUiState, MateChatsUseCase>(
    mSavedStateHandle, mErrorSource, mUseCase
), AuthorizedViewModel, UserViewModel {
    override fun generateDomainResultHandlers(): Array<DomainResultHandler<*>> {
        return super.generateDomainResultHandlers()
            .plus(AuthorizedDomainResultHandler(this))
            .plus(UserDomainResultHandler(this))
            .plus(MateChatsDomainResultHandler(this))
    }
    override fun generateDefaultUiState(): MateChatsUiState {
        return MateChatsUiState()
    }
    abstract fun onMateChatsGetChatChunk(
        getChatChunkResult: GetMateChatChunkDomainResult
    ): List<UiOperation>
    abstract fun onMateChatsUpdateChatChunk(
        updateChatChunkResult: UpdateMateChatChunkDomainResult
    ): List<UiOperation>
    abstract fun onMateChatsMateChatAdded(
        mateChatAddedDomainResult: MateChatAddedDomainResult
    ): List<UiOperation>
    abstract fun onMateChatsMateChatUpdated(
        mateChatUpdatedDomainResult: MateChatUpdatedDomainResult
    ): List<UiOperation>
    abstract fun getNextChatChunk()
    abstract fun isNextChatChunkGettingAllowed(): Boolean
    abstract fun prepareChatForEntering(chatId: Long): MateChatPresentation
    open fun resetChatChunks() {
        mUiState.apply {
            affectedChatCount = 0

            chats.clear()
            chatChunkSizes.clear()
        }
    }
    open fun areChatChunksInitialized(): Boolean {
        return mUiState.chatChunkSizes.isNotEmpty()
    }
}