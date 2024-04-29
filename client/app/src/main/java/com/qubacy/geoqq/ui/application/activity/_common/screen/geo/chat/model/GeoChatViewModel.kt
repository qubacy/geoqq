package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase.GeoChatUseCase
import com.qubacy.geoqq.domain.geo.chat.usecase.result.message.get.GetGeoMessagesDomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase.result.message.newer.NewGeoMessagesDomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase.result.send.SendGeoMessageDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.result.interlocutor.GetInterlocutorDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.result.interlocutor.UpdateInterlocutorDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.result.interlocutor._common.InterlocutorDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase.result.SendMateRequestDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.validator.message.text.MessageTextValidator
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.mateable.model.operation.ShowInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.mateable.model.operation.UpdateInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.model.operation.MateRequestSentToInterlocutorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.model.operation.MessageSentUiOperation
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
) {
    companion object {
        const val RADIUS_KEY = "radius"
        const val LONGITUDE_KEY = "longitude"
        const val LATITUDE_KEY = "latitude"
    }

    private var mRadius: Int? = null
    private var mLongitude: Double? = null
    private var mLatitude: Double? = null

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
        longitude: Double,
        latitude: Double
    ) {
        changeRadius(radius)
        changeLocation(longitude, latitude)
    }

    private fun changeRadius(radius: Int) {
        mRadius = radius

        mSavedStateHandle[RADIUS_KEY] = radius
    }

    private fun changeLocation(
        longitude: Double,
        latitude: Double
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

    open fun isMessageTextValid(text: String): Boolean {
        return MessageTextValidator().isValid(text)
    }

    open fun getUserProfileByMessagePosition(position: Int) {
        val userId = mUiState.messages[position].user.id

        mUseCase.getInterlocutor(userId)
    }

    open fun addUserAsMate(userId: Long) {
        changeLoadingState(true)
        mUseCase.sendMateRequestToInterlocutor(userId)
    }

    open fun sendMessage(text: String) {
        changeLoadingState(true)

        // todo: implement..

    }

    override fun processDomainResultFlow(domainResult: DomainResult): UiOperation? {
        val uiOperation = super.processDomainResultFlow(domainResult)

        if (uiOperation != null) return uiOperation

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
            else -> null
        }
    }

    private fun processGetGeoMessagesDomainResult(
        getGeoMessagesDomainResult: GetGeoMessagesDomainResult
    ): UiOperation {
        if (mUiState.isLoading) changeLoadingState(false)

        if (!getGeoMessagesDomainResult.isSuccessful())
            return processErrorDomainResult(getGeoMessagesDomainResult.error!!)

        val geoMessages = getGeoMessagesDomainResult.messages!!.map { it.toGeoMessagePresentation() }

        return AddGeoMessagesUiOperation(geoMessages)
    }

    private fun processNewGeoMessagesDomainResult(
        newGeoMessagesDomainResult: NewGeoMessagesDomainResult
    ): UiOperation? {
        // todo: implement..

        return null
    }

    private fun processSendMessageDomainResult(
        sendMessageResult: SendGeoMessageDomainResult
    ): UiOperation {
        if (mUiState.isLoading) changeLoadingState(false)

        if (!sendMessageResult.isSuccessful())
            return processErrorDomainResult(sendMessageResult.error!!)

        return MessageSentUiOperation()
    }

    private fun processSendMateRequestToInterlocutorDomainResult(
        sendMateRequestToInterlocutorResult: SendMateRequestDomainResult
    ): UiOperation {
        if (mUiState.isLoading) changeLoadingState(false)

        if (!sendMateRequestToInterlocutorResult.isSuccessful())
            return processErrorDomainResult(sendMateRequestToInterlocutorResult.error!!)

        return MateRequestSentToInterlocutorUiOperation()
    }

    private fun processGetInterlocutorDomainResult(
        getInterlocutorResult: GetInterlocutorDomainResult
    ): UiOperation {
        if (!getInterlocutorResult.isSuccessful())
            return processErrorDomainResult(getInterlocutorResult.error!!)

        val userPresentation = processInterlocutorResult(getInterlocutorResult)

        return ShowInterlocutorDetailsUiOperation(userPresentation)
    }

    private fun processUpdateInterlocutorDomainResult(
        updateInterlocutorResult: UpdateInterlocutorDomainResult
    ): UiOperation {
        if (!updateInterlocutorResult.isSuccessful())
            return processErrorDomainResult(updateInterlocutorResult.error!!)

        val userPresentation = processInterlocutorResult(updateInterlocutorResult)

        return UpdateInterlocutorDetailsUiOperation(userPresentation)
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