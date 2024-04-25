package com.qubacy.geoqq.domain.mate.request.usecase.result

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult

class SendMateRequestDomainResult(
    error: Error? = null
) : DomainResult(error) {

}