package com.qubacy.geoqq.data.myprofile.model.common

import com.qubacy.geoqq.data.myprofile.MyProfileContext

abstract class DataMyProfile(
    val username: String,
    val description: String,
    val hitUpOption: MyProfileContext.HitUpOption
) {

}