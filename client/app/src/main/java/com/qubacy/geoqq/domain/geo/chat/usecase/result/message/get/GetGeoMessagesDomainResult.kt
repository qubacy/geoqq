package com.qubacy.geoqq.domain.geo.chat.usecase.result.message.get

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain.geo.chat.model.GeoMessage
import com.qubacy.geoqq.domain.geo.chat.usecase.result.message._common.GeoMessagesDomainResult

class GetGeoMessagesDomainResult(
    error: Error? = null,
    messages: List<GeoMessage>? = null
) : GeoMessagesDomainResult(error, messages) {

}