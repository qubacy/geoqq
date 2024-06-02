package com.qubacy.geoqq.domain.mate.chat.usecase._common.result.message

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.mate._common.model.message.MateMessage

class MateMessageAddedDomainResult(
    error: Error? = null,
    val message: MateMessage? = null
) : DomainResult(error) {

}