package com.qubacy.geoqq.domain._common.usecase._common.result.failure

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult

class FailureDomainResult(
    error: Error
) : DomainResult(error) {

}