package com.qubacy.geoqq._common.model.error.type

import com.qubacy.geoqq._common.model.error.domain.ErrorDomain

interface ErrorType {
    val domain: ErrorDomain
    val id: Long

    fun getErrorCode(): Long {
        return domain.id * ErrorDomain.DOMAIN_SIZE + id
    }
}