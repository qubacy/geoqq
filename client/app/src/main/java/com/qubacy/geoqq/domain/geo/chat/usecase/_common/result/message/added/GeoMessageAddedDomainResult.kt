package com.qubacy.geoqq.domain.geo.chat.usecase._common.result.message.added

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.geo._common.model.GeoMessage

class GeoMessageAddedDomainResult(
    error: Error? = null,
    val message: GeoMessage? = null
) : DomainResult(error) {

}