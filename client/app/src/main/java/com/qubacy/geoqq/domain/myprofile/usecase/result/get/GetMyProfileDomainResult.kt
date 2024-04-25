package com.qubacy.geoqq.domain.myprofile.usecase.result.get

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.myprofile.model.profile.MyProfile

class GetMyProfileDomainResult(
    error: Error? = null,
    val myProfile: MyProfile? = null
) : DomainResult(error) {

}