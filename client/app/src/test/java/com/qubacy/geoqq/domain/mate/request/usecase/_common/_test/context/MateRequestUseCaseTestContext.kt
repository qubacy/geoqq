package com.qubacy.geoqq.domain.mate.request.usecase._common._test.context

import com.qubacy.geoqq.domain.interlocutor.usecase._common._test.context.InterlocutorUseCaseTestContext
import com.qubacy.geoqq.domain.mate.request.model.MateRequest

object MateRequestUseCaseTestContext {
    private val DEFAULT_USER = InterlocutorUseCaseTestContext.DEFAULT_USER

    val DEFAULT_MATE_REQUEST = MateRequest(0L, DEFAULT_USER)
}