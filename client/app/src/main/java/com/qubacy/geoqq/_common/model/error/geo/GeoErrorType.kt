package com.qubacy.geoqq._common.model.error.geo

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class GeoErrorType (
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.GEO
) : ErrorType {
    ERROR_1(1),
    ERROR_2(2);
}