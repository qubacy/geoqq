package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common

import androidx.lifecycle.SavedStateHandle
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain._common.usecase.aspect.chat.result.SendMessageDomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase._common.GeoChatUseCase
import com.qubacy.geoqq.domain.geo.chat.usecase._common.result.location.SendLocationDomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase._common.result.message.get.GetGeoMessagesDomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase._common.result.message.added.GeoMessageAddedDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.AuthorizedViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.result.handler.AuthorizedDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.ChatViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.result.handler.ChatDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.validator.message.text.MessageTextValidator
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.InterlocutorViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.location.model.LocationViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.user.model.result.handler.UserDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.result.handler.GeoChatDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.state.GeoChatUiState

abstract class GeoChatViewModel(
    mSavedStateHandle: SavedStateHandle,
    mErrorSource: LocalErrorDatabaseDataSource,
    mGeoChatUseCase: GeoChatUseCase
) : BusinessViewModel<GeoChatUiState, GeoChatUseCase>(
    mSavedStateHandle, mErrorSource, mGeoChatUseCase
), LocationViewModel, AuthorizedViewModel, ChatViewModel, InterlocutorViewModel {
    override fun generateDomainResultHandlers(): Array<DomainResultHandler<*>> {
        return super.generateDomainResultHandlers()
            .plus(GeoChatDomainResultHandler(this))
            .plus(AuthorizedDomainResultHandler(this))
            .plus(ChatDomainResultHandler(this))
            .plus(UserDomainResultHandler(this))
    }
    override fun generateDefaultUiState(): GeoChatUiState {
        return GeoChatUiState()
    }
    abstract fun setLocationContext(
        radius: Int,
        longitude: Float,
        latitude: Float
    )
    abstract fun isLocationContextSet(): Boolean
    abstract fun getMessages()
    abstract fun addInterlocutorAsMate(userId: Long)
    abstract fun sendMessage(text: String)
    abstract fun onGeoChatGetGeoMessages(
        getGeoMessagesDomainResult: GetGeoMessagesDomainResult
    ): List<UiOperation>
    abstract fun onGeoChatNewGeoMessage(
        newGeoMessageDomainResult: GeoMessageAddedDomainResult
    ): List<UiOperation>
    abstract fun onGeoChatSendMessage(
        sendMessageDomainResult: SendMessageDomainResult
    ): List<UiOperation>
    abstract fun onGeoChatSendLocation(
        sendLocationDomainResult: SendLocationDomainResult
    ): List<UiOperation>
    open fun getLocalUserId(): Long {
        return mUseCase.getLocalUserId()
    }
    open fun areMessagesLoaded(): Boolean {
        return mUiState.messages.isNotEmpty()
    }
    open fun isMessageTextValid(text: String): Boolean {
        return MessageTextValidator().isValid(text)
    }
    open fun getUserProfileByMessagePosition(position: Int) {
        val userId = mUiState.messages[position].user.id

        mUseCase.getInterlocutor(userId)
    }
    open fun resetMessages() {
        mUiState.apply {
            messages.clear()
        }
    }
}