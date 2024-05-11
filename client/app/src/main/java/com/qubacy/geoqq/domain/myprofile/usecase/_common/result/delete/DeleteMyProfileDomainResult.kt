package com.qubacy.geoqq.domain.myprofile.usecase._common.result.delete

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult

class DeleteMyProfileDomainResult(
    error: Error? = null
) : DomainResult(error) {

}