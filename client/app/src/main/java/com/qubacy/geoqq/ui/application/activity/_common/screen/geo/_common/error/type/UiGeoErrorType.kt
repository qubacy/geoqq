package com.qubacy.geoqq.ui.application.activity._common.screen.geo._common.error.type

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class UiGeoErrorType(
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.UI_GEO
) : ErrorType {
    LOCATION_REQUEST_FAILED(0),
    LOCATION_SERVICES_UNAVAILABLE(1),
    ;
}