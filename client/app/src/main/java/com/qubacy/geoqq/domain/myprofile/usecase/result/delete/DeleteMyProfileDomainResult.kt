package com.qubacy.geoqq.domain.myprofile.usecase.result.delete

import com.qubacy.geoqq._common.model.error.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult

class DeleteMyProfileDomainResult(
    error: Error? = null
) : DomainResult(error) {

}