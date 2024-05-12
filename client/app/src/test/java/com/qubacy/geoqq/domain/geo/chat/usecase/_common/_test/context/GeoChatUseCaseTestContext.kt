package com.qubacy.geoqq.domain.geo.chat.usecase._common._test.context

import com.qubacy.geoqq.data.geo.message.repository.impl._common._test.context.GeoMessageDataRepositoryTestContext
import com.qubacy.geoqq.domain.geo.chat.model.toGeoMessage

object GeoChatUseCaseTestContext {
    val DEFAULT_GEO_MESSAGE = GeoMessageDataRepositoryTestContext.DEFAULT_DATA_MESSAGE.toGeoMessage()
}