package com.qubacy.geoqq.domain.mate.requests.usecase.impl

import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data.auth.repository._common.AuthDataRepository
import com.qubacy.geoqq.data.mate.request.repository._common.MateRequestDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.aspect.user.update.handler.UserDataUpdateHandler
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.handler.DataUpdateHandler
import com.qubacy.geoqq.domain.user.usecase._common.UserUseCase
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import com.qubacy.geoqq.domain.mate._common.model.request.toMateRequest
import com.qubacy.geoqq.domain.mate.request.usecase._common.MateRequestUseCase
import com.qubacy.geoqq.domain.mate.requests.projection.MateRequestChunk
import com.qubacy.geoqq.domain.mate.requests.usecase._common.MateRequestsUseCase
import com.qubacy.geoqq.domain.mate.requests.usecase._common.result.chunk.get.GetRequestChunkDomainResult
import com.qubacy.geoqq.domain.mate.requests.usecase._common.result.chunk.update.UpdateRequestChunkDomainResult
import com.qubacy.geoqq.domain.mate.requests.usecase._common.update.handler.MateRequestDataUpdateHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

class MateRequestsUseCaseImpl @Inject constructor(
    errorSource: LocalErrorDatabaseDataSource,
    private val mMateRequestUseCase: MateRequestUseCase,
    private val mInterlocutorUseCase: UserUseCase,
    private val mLogoutUseCase: LogoutUseCase,
    private val mAuthDataRepository: AuthDataRepository,
    private val mMateRequestDataRepository: MateRequestDataRepository
) : MateRequestsUseCase(errorSource) {
    override val resultFlow: Flow<DomainResult> = merge(
        mResultFlow,
        mMateRequestUseCase.resultFlow,
        mInterlocutorUseCase.resultFlow
    )

    override fun generateDataUpdateHandlers(): Array<DataUpdateHandler<*>> {
        return arrayOf(
            UserDataUpdateHandler<MateRequestsUseCase>(this),
            MateRequestDataUpdateHandler(this)
        )
    }

    override fun getUpdatableRepositories(): Array<ProducingDataRepository> {
        return arrayOf(mMateRequestDataRepository, mAuthDataRepository)
    }

    override fun getRequestChunk(offset: Int) {
        executeLogic({
            val count = DEFAULT_REQUEST_CHUNK_SIZE

            val getRequestsResultLiveData = mMateRequestDataRepository
                .getMateRequests(offset, count)

            var version = 0

            val initGetRequestsResult = getRequestsResultLiveData.awaitUntilVersion(version)
            val initRequests = initGetRequestsResult.requests.map { it.toMateRequest() }
            val initRequestChunk = MateRequestChunk(offset, initRequests)

            mResultFlow.emit(GetRequestChunkDomainResult(chunk = initRequestChunk))

            if (initGetRequestsResult.isNewest) return@executeLogic

            ++version

            val newestGetRequestsResult = getRequestsResultLiveData.awaitUntilVersion(version)
            val newestRequests = newestGetRequestsResult.requests.map { it.toMateRequest() }
            val newestRequestChunk = MateRequestChunk(offset, newestRequests)

            mResultFlow.emit(UpdateRequestChunkDomainResult(chunk = newestRequestChunk))

        }, {
            GetRequestChunkDomainResult(error = it)
        })
    }

    override fun answerRequest(requestId: Long, isAccepted: Boolean) {
        mMateRequestUseCase.answerMateRequest(requestId, isAccepted)
    }

    override fun getInterlocutor(interlocutorId: Long) {
        mInterlocutorUseCase.getUser(interlocutorId)
    }

    override fun onCoroutineScopeSet() {
        super.onCoroutineScopeSet()

        mMateRequestUseCase.setCoroutineScope(mCoroutineScope)
        mInterlocutorUseCase.setCoroutineScope(mCoroutineScope)
        mLogoutUseCase.setCoroutineScope(mCoroutineScope)
        mAuthDataRepository.setCoroutineScope(mCoroutineScope)
        mMateRequestDataRepository.setCoroutineScope(mCoroutineScope)
    }

    override fun getLogoutUseCase(): LogoutUseCase {
        return mLogoutUseCase
    }
}