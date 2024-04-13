package com.qubacy.geoqq.domain.mate.chat.usecase.result.request

import com.qubacy.geoqq._common.model.error.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult

class SendMateRequestToInterlocutorDomainResult(
    error: Error? = null
) : DomainResult(error) {

}