package com.qubacy.geoqq.domain.geo.chat.usecase

import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data.geo.message.repository.impl.GeoMessageDataRepositoryImpl
import com.qubacy.geoqq.data.user.repository.impl.UserDataRepositoryImpl
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.authorized.AuthorizedUseCase
import com.qubacy.geoqq.domain._common.usecase.authorized.error.middleware.authorizedErrorMiddleware
import com.qubacy.geoqq.domain._common.usecase.chat.result.SendMessageDomainResult
import com.qubacy.geoqq.domain.geo.chat.model.toGeoMessage
import com.qubacy.geoqq.domain.geo.chat.usecase.result.message.get.GetGeoMessagesDomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase.result.message.update.UpdateGeoMessagesDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.InterlocutorUseCase
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import com.qubacy.geoqq.domain.mate.request.usecase.MateRequestUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import javax.inject.Inject

open class GeoChatUseCase @Inject constructor(
    errorSource: LocalErrorDatabaseDataSourceImpl,
    private val mMateRequestUseCase: MateRequestUseCase,
    private val mInterlocutorUseCase: InterlocutorUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val mGeoMessageDataRepository: GeoMessageDataRepositoryImpl,
    private val mUserDataRepository: UserDataRepositoryImpl
) : UseCase(mErrorSource = errorSource), AuthorizedUseCase {
    override val resultFlow: Flow<DomainResult> = merge(
        mResultFlow,
        mMateRequestUseCase.resultFlow,
        mInterlocutorUseCase.resultFlow
    )

    // TODO: Optimization?:
    open fun getMessages(
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

    open fun sendMessage(
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

    open fun getLocalUserId(): Long {
        return mUserDataRepository.getLocalUserId()
    }

    open fun getInterlocutor(interlocutorId: Long) {
        mInterlocutorUseCase.getInterlocutor(interlocutorId)
    }

    open fun sendMateRequestToInterlocutor(interlocutorId: Long) {
        mMateRequestUseCase.sendMateRequest(interlocutorId)
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

    private suspend fun processCollectedDataResult(dataResult: DataResult) {
        when (dataResult::class) {
            else -> throw IllegalArgumentException()
        }
    }

    override fun getLogoutUseCase(): LogoutUseCase {
        return logoutUseCase
    }
}