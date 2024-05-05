package com.qubacy.geoqq.domain.myprofile.usecase.result.profile.update

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.domain.myprofile.model.profile.MyProfile
import com.qubacy.geoqq.domain.myprofile.usecase.result.profile._common.MyProfileDomainResult

class UpdateMyProfileDomainResult(
    error: Error? = null,
    myProfile: MyProfile? = null
) : MyProfileDomainResult(error, myProfile) {

}