package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model

import android.location.Location
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.domain.geo.chat.usecase.GeoChatUseCase
import com.qubacy.geoqq.domain.geo.chat.usecase.result.message.get.GetGeoMessagesDomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase.result.message.newer.NewGeoMessagesDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.result.interlocutor.UpdateInterlocutorDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.result.interlocutor._common.InterlocutorDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.AuthorizedViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.result.handler.AuthorizedDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.ChatViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.validator.message.text.MessageTextValidator
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.result.handler.ChatDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.InterlocutorViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.result.handler.InterlocutorDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.location.model.LocationViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.operation.AddGeoMessagesUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.operation.UpdateGeoMessagesUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.result.handler.GeoChatDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.state.GeoChatUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.presentation.GeoMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.presentation.toGeoMessagePresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
open class GeoChatViewModel @Inject constructor(
    mSavedStateHandle: SavedStateHandle,
    mErrorSource: LocalErrorDataSource,
    mGeoChatUseCase: GeoChatUseCase
) : BusinessViewModel<GeoChatUiState, GeoChatUseCase>(
    mSavedStateHandle, mErrorSource, mGeoChatUseCase
), LocationViewModel, AuthorizedViewModel, ChatViewModel, InterlocutorViewModel {
    companion object {
        const val RADIUS_KEY = "radius"
        const val LONGITUDE_KEY = "longitude"
        const val LATITUDE_KEY = "latitude"
    }

    private var mRadius: Int? = null
    private var mLongitude: Float? = null
    private var mLatitude: Float? = null

    override fun generateDomainResultHandlers(): Array<DomainResultHandler<*>> {
        return super.generateDomainResultHandlers()
            .plus(GeoChatDomainResultHandler(this))
            .plus(AuthorizedDomainResultHandler(this))
            .plus(ChatDomainResultHandler(this))
            .plus(InterlocutorDomainResultHandler(this))
    }

    init {
        mRadius = mSavedStateHandle[RADIUS_KEY]
        mLongitude = mSavedStateHandle[LONGITUDE_KEY]
        mLatitude = mSavedStateHandle[LATITUDE_KEY]
    }

    override fun generateDefaultUiState(): GeoChatUiState {
        return GeoChatUiState()
    }

    open fun setLocationContext(
        radius: Int,
        longitude: Float,
        latitude: Float
    ) {
        changeRadius(radius)
        changeLocation(longitude, latitude)
    }

    open fun isLocationContextSet(): Boolean {
        return (mRadius != null && mLongitude != null && mLatitude != null)
    }

    open fun getLocalUserId(): Long {
        return mUseCase.getLocalUserId()
    }

    private fun changeRadius(radius: Int) {
        mRadius = radius

        mSavedStateHandle[RADIUS_KEY] = radius
    }

    private fun changeLocation(
        longitude: Float,
        latitude: Float
    ) {
        mLongitude = longitude
        mLatitude = latitude

        mSavedStateHandle[LONGITUDE_KEY] = longitude
        mSavedStateHandle[LATITUDE_KEY] = latitude
    }

    open fun getMessages() {
        changeLoadingState(true)

        mUseCase.getMessages(mRadius!!, mLongitude!!, mLatitude!!)
    }

    open fun areMessagesLoaded(): Boolean {
        return mUiState.messages.isNotEmpty()
    }

    /**
     * It's supposed that the text is already trimmed;
     */
    open fun isMessageTextValid(text: String): Boolean {
        return MessageTextValidator().isValid(text)
    }

    open fun getUserProfileByMessagePosition(position: Int) {
        val userId = mUiState.messages[position].user.id

        mUseCase.getInterlocutor(userId)
    }

    open fun addInterlocutorAsMate(userId: Long) {
        changeLoadingState(true)
        mUseCase.sendMateRequestToInterlocutor(userId)
    }

    open fun sendMessage(text: String) {
        changeLoadingState(true)

        mUseCase.sendMessage(text, mRadius!!, mLatitude!!, mLongitude!!)
    }

    fun onGeoChatGetGeoMessages(
        getGeoMessagesDomainResult: GetGeoMessagesDomainResult
    ): List<UiOperation> {
        if (mUiState.isLoading) changeLoadingState(false)

        if (!getGeoMessagesDomainResult.isSuccessful())
            return onError(getGeoMessagesDomainResult.error!!)

        val geoMessages = getGeoMessagesDomainResult.messages!!.map { it.toGeoMessagePresentation() }

        mUiState.messages.addAll(geoMessages)

        return listOf(AddGeoMessagesUiOperation(geoMessages))
    }

    fun onGeoChatNewGeoMessages(
        newGeoMessagesDomainResult: NewGeoMessagesDomainResult
    ): List<UiOperation> {
        // todo: implement..

        return listOf()
    }

    override fun onInterlocutorUpdateInterlocutor(
        domainResult: UpdateInterlocutorDomainResult
    ): List<UiOperation> {
        val superUiOperations = super.onInterlocutorUpdateInterlocutor(domainResult)

        if (!domainResult.isSuccessful()) return superUiOperations

        val userPresentation = domainResult.interlocutor!!.toUserPresentation()

        val updatedMessagesPositions = mutableListOf<Int>()
        val updatedMessages = mutableListOf<GeoMessagePresentation>()

        mUiState.messages.forEachIndexed { index, geoMessagePresentation ->
            if (geoMessagePresentation.user.id == userPresentation.id) {
                mUiState.messages[index] = geoMessagePresentation.copy(user = userPresentation)

                updatedMessagesPositions.add(index)
                updatedMessages.add(geoMessagePresentation)
            }
        }

        return superUiOperations
            .plus(UpdateGeoMessagesUiOperation(updatedMessagesPositions, updatedMessages))
    }

    override fun onInterlocutorInterlocutor(
        domainResult: InterlocutorDomainResult
    ): UserPresentation {
        val userPresentation = domainResult.interlocutor!!.toUserPresentation()

        // todo: this is overkilling:
        mUiState.messages.forEachIndexed { index, item ->
            if (item.user.id == userPresentation.id)
                mUiState.messages[index] = item.copy(user = userPresentation)
        }

        return userPresentation
    }

    open fun resetMessages() {
        mUiState.apply {
            messages.clear()
        }
    }

    override fun changeLastLocation(newLocation: Location) {
        changeLocation(newLocation.longitude.toFloat(), newLocation.latitude.toFloat())
    }

    override fun getChatViewModelBusinessViewModel(): BusinessViewModel<*, *> {
        return this
    }

    override fun getInterlocutorViewModelBusinessViewModel(): BusinessViewModel<*, *> {
        return this
    }
}

@Qualifier
annotation class GeoChatViewModelFactoryQualifier

class GeoChatViewModelFactory(
    private val mErrorSource: LocalErrorDataSource,
    private val mGeoChatUseCase: GeoChatUseCase
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (!modelClass.isAssignableFrom(GeoChatViewModel::class.java))
            throw IllegalArgumentException()

        return GeoChatViewModel(handle, mErrorSource, mGeoChatUseCase) as T
    }
}