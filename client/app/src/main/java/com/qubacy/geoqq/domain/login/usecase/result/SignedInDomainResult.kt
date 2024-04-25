package com.qubacy.geoqq.domain.login.usecase.result

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult

class SignedInDomainResult(
    error: Error? = null
) : DomainResult(error) {

}