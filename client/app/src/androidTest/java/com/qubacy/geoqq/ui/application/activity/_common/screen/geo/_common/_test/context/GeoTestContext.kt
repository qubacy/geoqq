package com.qubacy.geoqq.ui.application.activity._common.screen.geo._common._test.context

import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.presentation.GeoMessagePresentation

object GeoTestContext {
    fun generateGeoMessagePresentation(user: UserPresentation): GeoMessagePresentation {
        return GeoMessagePresentation(0L, user, "test", "TIME")
    }
}