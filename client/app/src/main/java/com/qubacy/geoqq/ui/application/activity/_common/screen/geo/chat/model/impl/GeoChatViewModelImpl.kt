package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.impl

import android.location.Location
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain.geo.chat.usecase._common.GeoChatUseCase
import com.qubacy.geoqq.domain.geo.chat.usecase._common.result.message.get.GetGeoMessagesDomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase._common.result.message.added.GeoMessageAddedDomainResult
import com.qubacy.geoqq.domain.user.usecase._common.result.update.UpdateUserDomainResult
import com.qubacy.geoqq.domain.user.usecase._common.result._common.UserDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.GeoChatViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.operation.add.AddGeoMessagesUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.operation.update.UpdateGeoMessagesUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.presentation.GeoMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.presentation.toGeoMessagePresentation
import javax.inject.Inject
import javax.inject.Qualifier

open class GeoChatViewModelImpl @Inject constructor(
    mSavedStateHandle: SavedStateHandle,
    mErrorSource: LocalErrorDatabaseDataSource,
    mGeoChatUseCase: GeoChatUseCase
) : GeoChatViewModel(
    mSavedStateHandle, mErrorSource, mGeoChatUseCase
) {
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

    override fun setLocationContext(
        radius: Int,
        longitude: Float,
        latitude: Float
    ) {
        changeRadius(radius)
        changeLocation(longitude, latitude)
    }

    override fun isLocationContextSet(): Boolean {
        return (mRadius != null && mLongitude != null && mLatitude != null)
    }

    override fun getMessages() {
        changeLoadingState(true)

        mUseCase.getMessages(mRadius!!, mLongitude!!, mLatitude!!)
    }

    override fun addInterlocutorAsMate(userId: Long) {
        changeLoadingState(true)
        mUseCase.sendMateRequestToInterlocutor(userId)
    }

    override fun sendMessage(text: String) {
        changeLoadingState(true)

        mUseCase.sendMessage(text, mRadius!!, mLatitude!!, mLongitude!!)
    }

    override fun onGeoChatGetGeoMessages(
        getGeoMessagesDomainResult: GetGeoMessagesDomainResult
    ): List<UiOperation> {
        if (mUiState.isLoading) changeLoadingState(false)

        if (!getGeoMessagesDomainResult.isSuccessful())
            return onError(getGeoMessagesDomainResult.error!!)

        val geoMessages = getGeoMessagesDomainResult.messages!!.map { it.toGeoMessagePresentation() }

        mUiState.messages.addAll(geoMessages)

        return listOf(AddGeoMessagesUiOperation(geoMessages))
    }

    override fun onGeoChatNewGeoMessages(
        newGeoMessagesDomainResult: GeoMessageAddedDomainResult
    ): List<UiOperation> {
        if (!newGeoMessagesDomainResult.isSuccessful())
            return onError(newGeoMessagesDomainResult.error!!)

        val messagePresentation = newGeoMessagesDomainResult.message!!.toGeoMessagePresentation()

        mUiState.messages.add(messagePresentation)

        return listOf(AddGeoMessagesUiOperation(listOf(messagePresentation)))
    }

    override fun onUserUpdateUser(
        domainResult: UpdateUserDomainResult
    ): List<UiOperation> {
        val superUiOperations = super.onUserUpdateUser(domainResult)

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

    override fun onUserUser(
        domainResult: UserDomainResult
    ): UserPresentation {
        val userPresentation = domainResult.interlocutor!!.toUserPresentation()

        // todo: this is overkilling:
        mUiState.messages.forEachIndexed { index, item ->
            if (item.user.id == userPresentation.id)
                mUiState.messages[index] = item.copy(user = userPresentation)
        }

        return userPresentation
    }

    override fun resetMessages() {
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

    override fun getUserViewModelBusinessViewModel(): BusinessViewModel<*, *> {
        return this
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
}

@Qualifier
annotation class GeoChatViewModelFactoryQualifier

class GeoChatViewModelImplFactory @Inject constructor(
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mGeoChatUseCase: GeoChatUseCase
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (!modelClass.isAssignableFrom(GeoChatViewModelImpl::class.java))
            throw IllegalArgumentException()

        return GeoChatViewModelImpl(handle, mErrorSource, mGeoChatUseCase) as T
    }
}