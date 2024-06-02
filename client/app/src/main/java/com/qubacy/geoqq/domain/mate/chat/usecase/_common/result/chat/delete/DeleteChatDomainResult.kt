package com.qubacy.geoqq.domain.mate.chat.usecase._common.result.chat.delete

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult

class DeleteChatDomainResult(
    error: Error? = null
) : DomainResult(error) {

}