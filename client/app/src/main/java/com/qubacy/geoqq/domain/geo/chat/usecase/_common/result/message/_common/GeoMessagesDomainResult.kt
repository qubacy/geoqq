package com.qubacy.geoqq.domain.geo.chat.usecase._common.result.message._common

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.geo._common.model.GeoMessage

abstract class GeoMessagesDomainResult(
    error: Error? = null,
    val messages: List<GeoMessage>? = null
) : DomainResult(error) {

}