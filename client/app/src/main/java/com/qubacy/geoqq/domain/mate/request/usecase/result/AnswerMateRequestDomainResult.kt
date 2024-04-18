package com.qubacy.geoqq.domain.mate.request.usecase.result

import com.qubacy.geoqq._common.model.error.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult

class AnswerMateRequestDomainResult(
    error: Error? = null,
    val requestId: Long? = null
) : DomainResult(error) {

}