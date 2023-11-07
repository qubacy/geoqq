package com.qubacy.geoqq.data.myprofile.model.avatar.labeled

import com.qubacy.geoqq.data.myprofile.MyProfileContext
import com.qubacy.geoqq.data.myprofile.model.common.DataMyProfile

class DataMyProfileWithAvatarId(
    username: String,
    description: String,
    hitUpOption: MyProfileContext.HitUpOption,
    val avatarId: Long
) : DataMyProfile(username, description, hitUpOption) {

}