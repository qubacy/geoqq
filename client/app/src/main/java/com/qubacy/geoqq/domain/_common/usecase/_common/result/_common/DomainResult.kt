package com.qubacy.geoqq.domain._common.usecase._common.result._common

import com.qubacy.geoqq._common.error.Error

abstract class DomainResult(
    val error: Error? = null
) {
    fun isSuccessful(): Boolean {
        return error == null
    }
}