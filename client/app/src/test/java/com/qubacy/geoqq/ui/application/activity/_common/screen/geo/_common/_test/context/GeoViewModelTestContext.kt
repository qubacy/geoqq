package com.qubacy.geoqq.ui.application.activity._common.screen.geo._common._test.context

import com.qubacy.geoqq.domain.geo._common.test.context.GeoUseCaseTestContext
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.presentation.toGeoMessagePresentation

object GeoViewModelTestContext {
    val DEFAULT_GEO_MESSAGE_PRESENTATION = GeoUseCaseTestContext.DEFAULT_GEO_MESSAGE
        .toGeoMessagePresentation()
}