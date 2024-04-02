package com.qubacy.geoqq.data.myprofile.repository.result

import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data.myprofile.model.DataMyProfile

data class GetMyProfileResult(
    val myProfile: DataMyProfile
) : DataResult {

}