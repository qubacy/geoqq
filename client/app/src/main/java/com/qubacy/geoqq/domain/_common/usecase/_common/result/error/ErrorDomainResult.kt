package com.qubacy.geoqq.domain._common.usecase._common.result.error

import com.qubacy.geoqq._common.error.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult

class ErrorDomainResult(
    val error: Error
) : DomainResult {

}