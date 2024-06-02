package com.qubacy.geoqq.domain.mate.requests.usecase._common

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.mate.request.repository._common.result.added.MateRequestAddedDataResult
import com.qubacy.geoqq.domain._common.usecase._common.error.middleware.ErrorMiddleware
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.AuthorizedUseCase
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.error.middleware.AuthorizedErrorMiddleware
import com.qubacy.geoqq.domain._common.usecase.aspect.user.UserAspectUseCase
import com.qubacy.geoqq.domain._common.usecase.base.updatable.UpdatableUseCase
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.error.handler._common.UpdateErrorHandler
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.error.handler.common.UpdateCommonErrorHandler
import com.qubacy.geoqq.domain.mate._common.model.request.toMateRequest
import com.qubacy.geoqq.domain.mate.requests.usecase._common.result.request.added.MateRequestAddedDomainResult

abstract class MateRequestsUseCase(
    errorSource: LocalErrorDatabaseDataSource
) : UpdatableUseCase(errorSource = errorSource), AuthorizedUseCase, UserAspectUseCase {
    companion object {
        const val DEFAULT_REQUEST_CHUNK_SIZE = 20
    }

    override fun generateUpdateErrorHandlers(): Array<UpdateErrorHandler> {
        return arrayOf(UpdateCommonErrorHandler(this))
    }

    override fun generateErrorMiddlewares(): Array<ErrorMiddleware> {
        return arrayOf(AuthorizedErrorMiddleware(this))
    }

    abstract fun getRequestChunk(offset: Int)
    abstract fun answerRequest(requestId: Long, isAccepted: Boolean)
    abstract fun getInterlocutor(interlocutorId: Long)
    open fun processMateRequestAddedDataResult(
        dataResult: MateRequestAddedDataResult
    ): MateRequestAddedDomainResult {
        val request = dataResult.mateRequest.toMateRequest()

        return MateRequestAddedDomainResult(request = request)
    }
}