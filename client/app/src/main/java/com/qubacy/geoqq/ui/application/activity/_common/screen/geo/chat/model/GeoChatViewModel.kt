package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model

import android.location.Location
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.authorized.result.error.ErrorWithLogoutDomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase.GeoChatUseCase
import com.qubacy.geoqq.domain.geo.chat.usecase.result.message.get.GetGeoMessagesDomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase.result.message.newer.NewGeoMessagesDomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase.result.send.SendGeoMessageDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.result.interlocutor.GetInterlocutorDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.result.interlocutor.UpdateInterlocutorDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.result.interlocutor._common.InterlocutorDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase.result.SendMateRequestDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.AuthorizedViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.validator.message.text.MessageTextValidator
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.operation.ShowInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.operation.UpdateInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.operation.MateRequestSentToInterlocutorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.operation.MessageSentUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.location.model.LocationViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.operation.AddGeoMessagesUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.state.GeoChatUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.presentation.toGeoMessagePresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
open class GeoChatViewModel @Inject constructor(
    mSavedStateHandle: SavedStateHandle,
    mErrorDataRepository: ErrorDataRepository,
    mGeoChatUseCase: GeoChatUseCase
) : BusinessViewModel<GeoChatUiState, GeoChatUseCase>(
    mSavedStateHandle, mErrorDataRepository, mGeoChatUseCase
), LocationViewModel, AuthorizedViewModel {
    companion object {
        const val RADIUS_KEY = "radius"
        const val LONGITUDE_KEY = "longitude"
        const val LATITUDE_KEY = "latitude"
    }

    private var mRadius: Int? = null
    private var mLongitude: Float? = null
    private var mLatitude: Float? = null

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

    override fun processDomainResultFlow(domainResult: DomainResult): List<UiOperation> {
        val uiOperations = super.processDomainResultFlow(domainResult)

        if (uiOperations.isNotEmpty()) return uiOperations

        return when (domainResult::class) {
            GetGeoMessagesDomainResult::class ->
                processGetGeoMessagesDomainResult(domainResult as GetGeoMessagesDomainResult)
            NewGeoMessagesDomainResult::class ->
                processNewGeoMessagesDomainResult(domainResult as NewGeoMessagesDomainResult)
            GetInterlocutorDomainResult::class ->
                processGetInterlocutorDomainResult(domainResult as GetInterlocutorDomainResult)
            UpdateInterlocutorDomainResult::class ->
                processUpdateInterlocutorDomainResult(domainResult as UpdateInterlocutorDomainResult)
            SendMateRequestDomainResult::class ->
                processSendMateRequestToInterlocutorDomainResult(
                    domainResult as SendMateRequestDomainResult)
            SendGeoMessageDomainResult::class ->
                processSendMessageDomainResult(domainResult as SendGeoMessageDomainResult)
            ErrorWithLogoutDomainResult::class ->
                processErrorWithLogoutDomainResult(domainResult as ErrorWithLogoutDomainResult)
            else -> listOf()
        }
    }

    private fun processGetGeoMessagesDomainResult(
        getGeoMessagesDomainResult: GetGeoMessagesDomainResult
    ): List<UiOperation> {
        if (mUiState.isLoading) changeLoadingState(false)

        if (!getGeoMessagesDomainResult.isSuccessful())
            return processErrorDomainResult(getGeoMessagesDomainResult.error!!)

        val geoMessages = getGeoMessagesDomainResult.messages!!.map { it.toGeoMessagePresentation() }

        mUiState.messages.addAll(geoMessages)

        return listOf(AddGeoMessagesUiOperation(geoMessages))
    }

    private fun processNewGeoMessagesDomainResult(
        newGeoMessagesDomainResult: NewGeoMessagesDomainResult
    ): List<UiOperation> {
        // todo: implement..

        return listOf()
    }

    private fun processSendMessageDomainResult(
        sendMessageResult: SendGeoMessageDomainResult
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

        return listOf(MateRequestSentToInterlocutorUiOperation())
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
}

@Qualifier
annotation class GeoChatViewModelFactoryQualifier

class GeoChatViewModelFactory(
    private val mErrorDataRepository: ErrorDataRepository,
    private val mGeoChatUseCase: GeoChatUseCase
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (!modelClass.isAssignableFrom(GeoChatViewModel::class.java))
            throw IllegalArgumentException()

        return GeoChatViewModel(handle, mErrorDataRepository, mGeoChatUseCase) as T
    }
}