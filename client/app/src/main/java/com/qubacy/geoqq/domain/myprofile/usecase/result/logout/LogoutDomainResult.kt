package com.qubacy.geoqq.domain.myprofile.usecase.result.logout

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult

class LogoutDomainResult(
    error: Error? = null
) : DomainResult(error) {

}