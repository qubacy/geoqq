package com.qubacy.geoqq.data.myprofile.repository._common.result

import com.qubacy.geoqq.data._common.repository.producing.result.ProducingDataResult
import com.qubacy.geoqq.data.myprofile.model.profile.DataMyProfile

class GetMyProfileDataResult(
    isNewest: Boolean,
    val myProfile: DataMyProfile
) : ProducingDataResult(isNewest) {

}