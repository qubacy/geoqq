package com.qubacy.geoqq.domain._common.usecase.aspect.authorized.result.error

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult

class ErrorWithLogoutDomainResult(
    error: Error
) : DomainResult(error) {

}