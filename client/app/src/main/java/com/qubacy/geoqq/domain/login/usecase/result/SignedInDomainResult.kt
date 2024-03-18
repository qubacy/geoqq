package com.qubacy.geoqq.domain.login.usecase.result

import com.qubacy.geoqq._common.error.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult

class SignedInDomainResult(
    error: Error? = null
) : DomainResult(error) {

}