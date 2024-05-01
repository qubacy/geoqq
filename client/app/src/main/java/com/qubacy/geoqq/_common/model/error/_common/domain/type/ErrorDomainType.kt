package com.qubacy.geoqq._common.model.error._common.domain.type

enum class ErrorDomainType(val offset: Int, val size: Int) {
    LOCAL(0,50),
    SHARED(1200, 200);
}