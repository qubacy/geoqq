package com.qubacy.geoqq.data.myprofile.repository.result

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.myprofile.model.common.DataMyProfile

class GetMyProfileResult(
    val myProfileData: DataMyProfile
) : Result() {

}