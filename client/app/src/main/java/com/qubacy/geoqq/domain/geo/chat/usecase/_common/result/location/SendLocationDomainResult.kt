package com.qubacy.geoqq.domain.geo.chat.usecase._common.result.location

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult

class SendLocationDomainResult(
    error: Error? = null
) : DomainResult(error) {
}