package com.qubacy.geoqq.domain._common.usecase._common.error.middleware

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult

open class ErrorMiddleware {
    open fun processError(
        error: Error,
        errorResultProducer: (error: Error) -> DomainResult
    ): DomainResult? {
        return errorResultProducer(error)
    }
}