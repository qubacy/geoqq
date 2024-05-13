package com.qubacy.geoqq.domain.geo._common.test.context

import com.qubacy.geoqq.data.geo.message.repository.impl._common._test.context.GeoMessageDataRepositoryTestContext
import com.qubacy.geoqq.domain.geo._common.model.toGeoMessage

object GeoUseCaseTestContext {
    val DEFAULT_GEO_MESSAGE = GeoMessageDataRepositoryTestContext.DEFAULT_DATA_MESSAGE.toGeoMessage()
}