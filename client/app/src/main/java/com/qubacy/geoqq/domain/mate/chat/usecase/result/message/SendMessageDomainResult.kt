package com.qubacy.geoqq.domain.mate.chat.usecase.result.message

import com.qubacy.geoqq._common.model.error.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult

class SendMessageDomainResult(
    error: Error? = null
) : DomainResult(error) {

}