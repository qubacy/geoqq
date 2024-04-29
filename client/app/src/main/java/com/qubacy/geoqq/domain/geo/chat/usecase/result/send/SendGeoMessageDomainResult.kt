package com.qubacy.geoqq.domain.geo.chat.usecase.result.send

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult

class SendGeoMessageDomainResult(
    error: Error? = null
) : DomainResult(error) {

}