package com.qubacy.geoqq.domain.geo.chat.usecase.impl

import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data.geo.message.repository._common.GeoMessageDataRepository
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.error.middleware.authorizedErrorMiddleware
import com.qubacy.geoqq.domain._common.usecase.aspect.chat.result.SendMessageDomainResult
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.handler.DataUpdateHandler
import com.qubacy.geoqq.domain.geo._common.model.toGeoMessage
import com.qubacy.geoqq.domain.geo.chat.usecase._common.GeoChatUseCase
import com.qubacy.geoqq.domain.geo.chat.usecase._common.result.message.get.GetGeoMessagesDomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase._common.result.message.update.UpdateGeoMessagesDomainResult
import com.qubacy.geoqq.domain.user.usecase._common.UserUseCase
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import com.qubacy.geoqq.domain.mate.request.usecase._common.MateRequestUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import javax.inject.Inject

open class GeoChatUseCaseImpl @Inject constructor(
    errorSource: LocalErrorDatabaseDataSource,
    private val mMateRequestUseCase: MateRequestUseCase,
    private val mInterlocutorUseCase: UserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val mGeoMessageDataRepository: GeoMessageDataRepository,
    private val mUserDataRepository: UserDataRepository
) : GeoChatUseCase(errorSource) {
    override val resultFlow: Flow<DomainResult> = merge(
        mResultFlow,
        mMateRequestUseCase.resultFlow,
        mInterlocutorUseCase.resultFlow
    )

    override fun generateDataUpdateHandlers(): Array<DataUpdateHandler<*>> {
        return super.generateDataUpdateHandlers()
    }

    override fun getUpdatableRepositories(): Array<ProducingDataRepository> {
        return arrayOf(mGeoMessageDataRepository, mUserDataRepository)
    }

    // TODO: Optimization?:
    override fun getMessages(
        radius: Int,
        longitude: Float,
        latitude: Float
    ) {
        executeLogic({
            val getMessagesResultLiveData = mGeoMessageDataRepository
                .getMessages(radius, longitude, latitude)

            var version = 0

            val initGetMessagesResult = getMessagesResultLiveData.awaitUntilVersion(version)
            val initMessages =  initGetMessagesResult.messages.map { it.toGeoMessage() }

            mResultFlow.emit(GetGeoMessagesDomainResult(messages = initMessages))

            if (initGetMessagesResult.isNewest) return@executeLogic

            ++version

            val newestGetMessagesResult = getMessagesResultLiveData.awaitUntilVersion(version)
            val newestMessages = newestGetMessagesResult.messages.map { it.toGeoMessage() }

            mResultFlow.emit(UpdateGeoMessagesDomainResult(messages = newestMessages))

        }, {
            GetGeoMessagesDomainResult(error = it)
        }, ::authorizedErrorMiddleware)
    }

    override fun sendMessage(
        text: String,
        radius: Int,
        latitude: Float,
        longitude: Float
    ) {
        executeLogic({
            mGeoMessageDataRepository.sendMessage(text, radius, longitude, latitude)

            mResultFlow.emit(SendMessageDomainResult())
        }, {
            SendMessageDomainResult(error = it)
        }, ::authorizedErrorMiddleware)
    }

    override fun getLocalUserId(): Long {
        return mUserDataRepository.getLocalUserId()
    }

    override fun getInterlocutor(interlocutorId: Long) {
        mInterlocutorUseCase.getUser(interlocutorId)
    }

    override fun sendMateRequestToInterlocutor(interlocutorId: Long) {
        mMateRequestUseCase.sendMateRequest(interlocutorId)
    }

    override fun getLogoutUseCase(): LogoutUseCase {
        return logoutUseCase
    }

    override fun onCoroutineScopeSet() {
        super.onCoroutineScopeSet()

        mCoroutineScope.launch {
            mGeoMessageDataRepository.resultFlow.collect {
                processCollectedDataResult(it)
            }
        }

        mMateRequestUseCase.setCoroutineScope(mCoroutineScope)
        mInterlocutorUseCase.setCoroutineScope(mCoroutineScope)
    }
}