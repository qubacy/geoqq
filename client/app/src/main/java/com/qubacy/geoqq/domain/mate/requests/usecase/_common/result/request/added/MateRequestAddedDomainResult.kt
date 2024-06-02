package com.qubacy.geoqq.domain.mate.requests.usecase._common.result.request.added

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.mate._common.model.request.MateRequest

class MateRequestAddedDomainResult(
    error: Error? = null,
    val request: MateRequest? = null
) : DomainResult(error) {

}