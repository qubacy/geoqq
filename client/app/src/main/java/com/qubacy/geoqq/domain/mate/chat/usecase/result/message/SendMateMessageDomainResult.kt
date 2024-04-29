package com.qubacy.geoqq.domain.mate.chat.usecase.result.message

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult

class SendMateMessageDomainResult(
    error: Error? = null
) : DomainResult(error) {

}