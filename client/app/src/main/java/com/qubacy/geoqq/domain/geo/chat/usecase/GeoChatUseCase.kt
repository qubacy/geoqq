package com.qubacy.geoqq.domain.geo.chat.usecase

import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.geo.message.repository.GeoMessageDataRepository
import com.qubacy.geoqq.data.geo.message.repository.result.GetGeoMessagesDataResult
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.authorized.AuthorizedUseCase
import com.qubacy.geoqq.domain._common.usecase.authorized.error.middleware.authorizedErrorMiddleware
import com.qubacy.geoqq.domain.geo.chat.model.toGeoMessage
import com.qubacy.geoqq.domain.geo.chat.usecase.result.message.get.GetGeoMessagesDomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase.result.message.newer.NewGeoMessagesDomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase.result.send.SendGeoMessageDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.InterlocutorUseCase
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import com.qubacy.geoqq.domain.mate.request.usecase.MateRequestUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import javax.inject.Inject

class GeoChatUseCase @Inject constructor(
    errorDataRepository: ErrorDataRepository,
    private val mMateRequestUseCase: MateRequestUseCase,
    private val mInterlocutorUseCase: InterlocutorUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val mGeoMessageDataRepository: GeoMessageDataRepository,
    private val mUserDataRepository: UserDataRepository
) : UseCase(mErrorDataRepository = errorDataRepository), AuthorizedUseCase {
    override val resultFlow: Flow<DomainResult> = merge(
        mResultFlow,
        mMateRequestUseCase.resultFlow,
        mInterlocutorUseCase.resultFlow
    )

    fun getMessages(
        radius: Int,
        longitude: Float,
        latitude: Float
    ) {
        executeLogic({
            val getMessagesResult = mGeoMessageDataRepository
                .getMessages(radius, longitude, latitude)

            val messages = getMessagesResult.messages.map { it.toGeoMessage() }

            mResultFlow.emit(GetGeoMessagesDomainResult(messages = messages))

        }, {
            GetGeoMessagesDomainResult(error = it)
        }, ::authorizedErrorMiddleware)
    }

    fun sendMessage(
        text: String,
        radius: Int,
        latitude: Float,
        longitude: Float
    ) {
        executeLogic({
            mGeoMessageDataRepository.sendMessage(text, radius, longitude, latitude)

            mResultFlow.emit(SendGeoMessageDomainResult())
        }, {
            SendGeoMessageDomainResult(error = it)
        }, ::authorizedErrorMiddleware)
    }

    fun getLocalUserId(): Long {
        return mUserDataRepository.getLocalUserId()
    }

    fun getInterlocutor(interlocutorId: Long) {
        mInterlocutorUseCase.getInterlocutor(interlocutorId)
    }

    fun sendMateRequestToInterlocutor(interlocutorId: Long) {
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
            GetGeoMessagesDataResult::class ->
                processGetMessagesDataResult(dataResult as GetGeoMessagesDataResult)
            else -> throw IllegalArgumentException()
        }
    }

    private suspend fun processGetMessagesDataResult(
        getMessagesResult: GetGeoMessagesDataResult
    ) {
        val messages = getMessagesResult.messages.map { it.toGeoMessage() }

        mResultFlow.emit(NewGeoMessagesDomainResult(messages = messages))
    }

    override fun getLogoutUseCase(): LogoutUseCase {
        return logoutUseCase
    }
}