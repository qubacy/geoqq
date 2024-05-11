package com.qubacy.geoqq.domain.mate.request.usecase._common

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain._common.usecase.authorized.AuthorizedUseCase

abstract class MateRequestUseCase(
    errorSource: LocalErrorDatabaseDataSource
) : UseCase(mErrorSource = errorSource), AuthorizedUseCase {
    abstract fun sendMateRequest(interlocutorId: Long)
    abstract fun answerMateRequest(requestId: Long, isAccepted: Boolean)
}