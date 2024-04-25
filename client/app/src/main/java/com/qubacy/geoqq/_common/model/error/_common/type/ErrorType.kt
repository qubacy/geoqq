package com.qubacy.geoqq._common.model.error._common.type

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain

interface ErrorType {
    val domain: ErrorDomain
    val id: Long

    fun getErrorCode(): Long {
        return ErrorDomain.getOffsetForDomain(domain) + id
    }
}