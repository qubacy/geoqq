package com.qubacy.geoqq.domain.geo.chat.usecase._common.result.message.newer

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain.geo._common.model.GeoMessage
import com.qubacy.geoqq.domain.geo.chat.usecase._common.result.message._common.GeoMessagesDomainResult

class NewGeoMessagesDomainResult(
    error: Error? = null,
    messages: List<GeoMessage>? = null
) : GeoMessagesDomainResult(error, messages) {

}