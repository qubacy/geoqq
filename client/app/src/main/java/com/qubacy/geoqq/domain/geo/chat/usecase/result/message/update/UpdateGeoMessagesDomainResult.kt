package com.qubacy.geoqq.domain.geo.chat.usecase.result.message.update

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain.geo.chat.model.GeoMessage
import com.qubacy.geoqq.domain.geo.chat.usecase.result.message._common.GeoMessagesDomainResult

class UpdateGeoMessagesDomainResult(
    error: Error? = null,
    messages: List<GeoMessage>? = null
) : GeoMessagesDomainResult(error, messages) {

}