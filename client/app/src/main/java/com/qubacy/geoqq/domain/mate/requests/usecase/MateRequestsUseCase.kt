package com.qubacy.geoqq.domain.mate.requests.usecase

import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data.mate.request.repository.impl.MateRequestDataRepositoryImpl
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.authorized.AuthorizedUseCase
import com.qubacy.geoqq.domain._common.usecase.authorized.error.middleware.authorizedErrorMiddleware
import com.qubacy.geoqq.domain.interlocutor.usecase.InterlocutorUseCase
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import com.qubacy.geoqq.domain.mate.request.usecase.MateRequestUseCase
import com.qubacy.geoqq.domain.mate.request.model.toMateRequest
import com.qubacy.geoqq.domain.mate.requests.projection.MateRequestChunk
import com.qubacy.geoqq.domain.mate.requests.usecase.result.get.GetRequestChunkDomainResult
import com.qubacy.geoqq.domain.mate.requests.usecase.result.update.UpdateRequestChunkDomainResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

class MateRequestsUseCase @Inject constructor(
    errorSource: LocalErrorDatabaseDataSourceImpl,
    private val mMateRequestUseCase: MateRequestUseCase,
    private val mInterlocutorUseCase: InterlocutorUseCase,
    private val mLogoutUseCase: LogoutUseCase,
    private val mMateRequestDataRepository: MateRequestDataRepositoryImpl
) : UseCase(mErrorSource = errorSource), AuthorizedUseCase {
    companion object {
        const val DEFAULT_REQUEST_CHUNK_SIZE = 20
    }

    override val resultFlow: Flow<DomainResult> = merge(
        mResultFlow,
        mMateRequestUseCase.resultFlow,
        mInterlocutorUseCase.resultFlow
    )

    fun getRequestChunk(offset: Int) {
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
        }, ::authorizedErrorMiddleware)
    }

    fun answerRequest(requestId: Long, isAccepted: Boolean) {
        mMateRequestUseCase.answerMateRequest(requestId, isAccepted)
    }

    fun getInterlocutor(interlocutorId: Long) {
        mInterlocutorUseCase.getInterlocutor(interlocutorId)
    }

    override fun onCoroutineScopeSet() {
        super.onCoroutineScopeSet()

        mMateRequestUseCase.setCoroutineScope(mCoroutineScope)
        mInterlocutorUseCase.setCoroutineScope(mCoroutineScope)
    }

    override fun getLogoutUseCase(): LogoutUseCase {
        return mLogoutUseCase
    }
}