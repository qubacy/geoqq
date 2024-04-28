package com.qubacy.geoqq._common.model.error.geo

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class GeoErrorType (
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.GEO
) : ErrorType {
    WRONG_LATITUDE(1),
    WRONG_LONGITUDE(2);
}