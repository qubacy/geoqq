package com.qubacy.geoqq.domain.mate.requests.usecase._common

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain._common.usecase.base._common.UseCase
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.AuthorizedUseCase

abstract class MateRequestsUseCase(
    errorSource: LocalErrorDatabaseDataSource
) : UseCase(mErrorSource = errorSource), AuthorizedUseCase {
    companion object {
        const val DEFAULT_REQUEST_CHUNK_SIZE = 20
    }

    abstract fun getRequestChunk(offset: Int)
    abstract fun answerRequest(requestId: Long, isAccepted: Boolean)
    abstract fun getInterlocutor(interlocutorId: Long)
}