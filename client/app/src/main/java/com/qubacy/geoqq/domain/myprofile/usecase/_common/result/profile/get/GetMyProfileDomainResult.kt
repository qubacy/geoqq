package com.qubacy.geoqq.domain.myprofile.usecase._common.result.profile.get

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain.myprofile.model.profile.MyProfile
import com.qubacy.geoqq.domain.myprofile.usecase._common.result.profile._common.MyProfileDomainResult

class GetMyProfileDomainResult(
    error: Error? = null,
    myProfile: MyProfile? = null
) : MyProfileDomainResult(error, myProfile) {

}