package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common

import androidx.lifecycle.SavedStateHandle
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain.mate.chats.usecase._common.MateChatsUseCase
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk.GetChatChunkDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk.UpdateChatChunkDomainResult
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
        getChatChunkResult: GetChatChunkDomainResult
    ): List<UiOperation>
    abstract fun onMateChatsUpdateChatChunk(
        updateChatChunkResult: UpdateChatChunkDomainResult
    ): List<UiOperation>
    abstract fun getNextChatChunk()
    abstract fun isNextChatChunkGettingAllowed(): Boolean
    abstract fun prepareChatForEntering(chatId: Long): MateChatPresentation
    open fun resetChatChunks() {
        mUiState.apply {
            newChatCount = 0

            chats.clear()
            chatChunkSizes.clear()
        }
    }
    open fun areChatChunksInitialized(): Boolean {
        return mUiState.chatChunkSizes.isNotEmpty()
    }
}